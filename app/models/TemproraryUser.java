package models;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

@Entity
public class TemproraryUser extends Model{

	private static final long serialVersionUID = 1L;

	@Id
	public Long id;
	
	@Required
	public String email;
	
	public String name;
	
	public TemproraryUser(String email, String name){
		this.email = email;
		this.name = name;
		this.save();
	}
	
	public boolean hasPermission(String per){
		return false;
	}
	
	public void save(){
		if(email != null && !email.equals("") && 
				name!=null && !name.equals("")){
			super.save();
		}
	}
	
	
	public static Finder<Long, TemproraryUser> find = new Finder<Long, TemproraryUser>(Long.class, TemproraryUser.class);
}
