package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Article;
import models.Comment;
import models.Section;
import models.Tag;
import models.User;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;
import views.html.article.*;
import be.objectify.deadbolt.java.actions.*;

public class Articles extends Controller{

	@Pattern(Application.USER_PERMISSION_EDITOR)
	public static Result newArticle(){
		final User user = Application.getLocalUser(session());
		return ok(createArticle.render(user, Form.form(Article.class)));
	}
	
	@Pattern(Application.USER_PERMISSION_EDITOR)
	/**
	 * create new one
	 * @return redirect to article preview
	 */
	public static Result doCreateArticle(){
		User user = Application.getLocalUser(session());
		DynamicForm articleData = Form.form().bindFromRequest();
		Article article = Article.createArticle(user, articleData);
		return redirect(routes.Articles.viewArticle(article.id));
	}
	
	@Pattern(Application.USER_PERMISSION_EDITOR)
	/**
	 * Here, we only allow admin or original editor to edit article
	 * @return redirect to article preview
	 */
	public static Result doEditArticle(Long id){
		User user = Application.getLocalUser(session());
		DynamicForm articleData = Form.form().bindFromRequest();
		Article article = Article.editArticle(user, articleData, id);
		return redirect(routes.Articles.viewArticle(id));
	}
	
	@Pattern(Application.USER_PERMISSION_EDITOR)
	public static Result editArticle(Long id){
		User user = Application.getLocalUser(session());
		Article a = Article.find.where().eq("id", id).findUnique();
		if(!a.editors.contains(user) && !user.hasPermission(Application.USER_PERMISSION_ADMIN)){
			return redirect(routes.Articles.viewArticle(id));
		}
		return ok(editArticle.render(user,Form.form(Article.class), a));
	}
	
	@Pattern(Application.USER_PERMISSION_EDITOR)
	public static Result doDeleteArticle(Long id){
		String ref = session().get("Refer");
		User user = Application.getLocalUser(session());
		Article.deleteArticle(id);
		return Application.back();		//TODO: test if works
	}
	
	/**
	 * Everyone can view the articles
	 * @param id
	 * @return
	 */
	public static Result viewArticle(Long id){
		Article a = Article.find.where().eq("id", id).findUnique();
		User user = Application.getLocalUser(session());
		return ok(dispArticle.render(user, a));
	}
	
	
	public static Result postComments(Long id){
		User user = Application.getLocalUser(session());
		Article a = Article.find.where().eq("id", id).findUnique();
		if(user == null){
			String email = Form.form().bindFromRequest().get("email");
			String name = Form.form().bindFromRequest().get("name");
			user = User.temporaryUser(email, name);
		}
		Comment comm = user.createComment(Form.form().bindFromRequest());  
		a.comments.add(comm);
		a.save();
		return redirect(routes.Articles.viewArticle(id));
	}
}
 