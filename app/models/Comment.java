package models;

import java.util.Date;

import javax.persistence.*;

import com.avaje.ebean.annotation.ConcurrencyMode;
import com.avaje.ebean.annotation.EntityConcurrencyMode;

import play.data.DynamicForm;
import play.data.format.Formats;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

@Entity
//@EntityConcurrencyMode(ConcurrencyMode.NONE)
public class Comment extends Model{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	public Long id;
	
	@Required
	public String email;
	
	public String name;
	
	@Required
	public String content;
	
	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm")
	public Date created;
	
	public Long oo;
	
	public Long xx;
	
	public Comment(){
		created = new Date();
		oo = 0L;
		xx = 0L;
	}
	
	
	public void upVote(){
		this.oo += 1;
		this.save();
	}
	
	public void downVote(){
		this.xx += 1;
		this.save();
	}
	
	public static Finder<Long, Comment> find = new Finder<Long, Comment>(Long.class, Comment.class);
}
