package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import play.data.DynamicForm;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import play.db.ebean.Model.*;

@Entity
public class Tag extends Model{
	
	private static final long serialVersionUID = 1L;
	

	@Id
	public Long id;
	
	@Required
	public String name;
	
	@Required
	public String alias;
	
	public static Tag createTag(String sname, String alias){
		if(sname == null || sname.trim().equals("")){
			return null;
		}
		Tag tag = find.where().eq("name", sname).findUnique();
		tag = new Tag();
		tag.name = sname;
		if(alias==null || alias.trim().equals("")){
			alias = sname;
		}
		tag.alias = alias;
		tag.save();
		return tag;
	}
	
	public static Finder<Long, Tag> find = new Finder<Long, Tag>(Long.class, Tag.class);
	
}
