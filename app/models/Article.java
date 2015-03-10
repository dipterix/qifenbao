package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import org.hibernate.validator.constraints.Length;

import com.avaje.ebean.annotation.*;
import com.feth.play.module.pa.user.AuthUser;
import com.google.common.collect.Lists;

import controllers.Application;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.data.format.Formats;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import play.mvc.Http.Context;
import play.mvc.Result;

@Entity
@EntityConcurrencyMode(ConcurrencyMode.NONE)
public class Article extends Model{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final int LISTING_SIZE = 15;
	
	@Id
	public Long id;
	
	@ManyToMany
	public List<User> editors;
	
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm")
	public Date lastEdited;
	
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm")
	public Date created;
	
	public String permissons;
	
	@ManyToMany
	public List<Tag> tags;
	
	@Required
	public String title;
	
	@Length(max=7000)
	@Column(columnDefinition = "TEXT")
	public String content;
	
	public String preview;
	
	public String cover;
	
	@ManyToMany
	public List<Comment> comments;
	
	@OrderBy("lastEdited desc")
	/**
	 * Get a list of articles edited by an editor
	 * @param page
	 * @param editor	if null, return all articles, else return this guy's work
	 * @return
	 */
	public static List<Article> getArticlesByPage(int page, User editor){
		if(page < 0){
			page=0;
		}
		List<Article> list = null;
		if(editor == null){
			list = find.orderBy("lastEdited desc").
					findPagingList(LISTING_SIZE).
					getPage(page).
					getList();
		}else{
			list = find.where().
					eq("editors", editor).orderBy("lastEdited desc").
					findPagingList(LISTING_SIZE).
					getPage(page).
					getList();
		}
		return list;
	}
	
	
	@OrderBy("lastEdited desc")
	/**
	 * Get a list of articles edited which own the same tag
	 */
	public static List<Article> getByTag(int page, Tag tag){
		if(page < 0){
			page=0;
		}
		List<Article> list = null;
		if(tag == null){
			list = find.orderBy("lastEdited desc").
					findPagingList(LISTING_SIZE).
					getPage(page).
					getList();
		}else{
			list = find.where().
					eq("tags", tag).orderBy("lastEdited desc").
					findPagingList(LISTING_SIZE).
					getPage(page).
					getList();
		}
		return list;
	}
	
	private static void save(final Article a){
		a.save();
		a.saveManyToManyAssociations("editors");
		a.saveManyToManyAssociations("tags");
		a.saveManyToManyAssociations("comments");
	}
	
	private static void update(final Article a){
		a.update();
		a.saveManyToManyAssociations("editors");
		a.saveManyToManyAssociations("tags");
		a.saveManyToManyAssociations("comments");
	}
	
	public static void setLastEdited(final Article a, final User editor) {
		a.lastEdited = new Date();
		if(!a.editors.contains(editor)){
			a.editors.add(editor);
		}
	}
	
	public static final Finder<Long, Article> find = new Finder<Long, Article>(Long.class, Article.class);

	/**
	 * Change article data, currently NOT allow other editor in the same section.
	 * Admin will never appear on the editor list
	 * @param user
	 * @param articleData
	 */
	private void setData(final User user, final DynamicForm articleData){
		if(!editors.contains(user) && !user.hasPermission(Application.USER_PERMISSION_ADMIN)){
			editors.add(user);
		}
		setLastEdited(this, user);
		permissons = articleData.get("permissons");
		if(permissons == null){
			permissons = Application.USER_PERMISSION_NORMAL;
		}
		this.tags.clear();
		String tags = articleData.get("tags");
		if(tags != null && !tags.trim().equals("")){
			for(String t:tags.split(",")){
				t = t.trim();
				Tag tag = Tag.find.where().eq("name", t).findUnique();
				if(tag != null && !this.tags.contains(tag)){
					for(Section s:user.editSections){
						if(s.containsTag(t)){
							this.tags.add(tag);
							break;
						}
					}
				}
			}
		}
		title = articleData.get("title");
		content = articleData.get("content");
		preview = articleData.get("preview");
		preview = preview.substring(0, Math.min(140, preview.length()));
		cover = articleData.get("cover");
		comments = new ArrayList<Comment>();
	}

	public Article(){
		this.editors = new ArrayList<User>();
		this.tags = new ArrayList<Tag>();
		this.created = new Date();
		this.title = "";
		this.content = "";
		this.preview = "";
		this.cover="";
	}
	
	
	public static Article createArticle(final User user, final DynamicForm articleData) {
		Article article = new Article();
		article.setData(user, articleData);
		save(article);
		return article;
	}
	
	public static Article editArticle(final User user, final DynamicForm articleData, Long id){
		Article article = Article.find.where().eq("id", id).findUnique();
		if(article != null){
			article.setData(user, articleData);
			update(article);
		} else{
			// this guy is hacking or not original editor.
			return null;
		}
		return article;
	}
	
	public static void deleteArticle(Long id) {
		Article article = Article.find.where().eq("id", id).findUnique();
		article.permissons = Application.USER_PERMISSION_ADMIN;
		update(article);
	}
	
	
}
