package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import com.avaje.ebean.annotation.ConcurrencyMode;
import com.avaje.ebean.annotation.EntityConcurrencyMode;

import play.db.ebean.Model;


@Entity
@EntityConcurrencyMode(ConcurrencyMode.NONE)
public class UserProfile extends Model {
	private static final long serialVersionUID = 1L;

	public static enum visibilityCode{Self, Fans, All};
	
	@Id
	public Long id;
	
	@ManyToMany
	public List<User> focus;

	@ManyToMany
	public List<User> fans;
	
	/**
	 * Both in focus and fans
	 */
	@ManyToMany
	public List<User> friends;
	
	@ManyToMany
	public List<Notification> notifications;
	
	public visibilityCode visibility;
	
	public static Finder<Long, UserProfile> find = new Finder<Long, UserProfile>(Long.class, UserProfile.class); 

	public static void save(final UserProfile profile){
		profile.save();
		profile.saveManyToManyAssociations("focus");
		profile.saveManyToManyAssociations("fans");
		profile.saveManyToManyAssociations("friends");
		profile.saveManyToManyAssociations("notifications");
	}
	
	/**
	 * Constructor, (CRUD: Create)
	 * @return 
	 */
	public static UserProfile createUserProfile(){
		UserProfile p = new UserProfile();
		p.focus = new ArrayList<User>();
		p.fans = new ArrayList<User>();
		p.friends = new ArrayList<User>();
		p.notifications = new ArrayList<Notification>();
		p.visibility = visibilityCode.All; //VISI_STATUS.get(visibilityCode.All);
		save(p);
		return(p);
	}
	
	public void addFocus(final User user){
		if(!this.focus.contains(user)){
			this.focus.add(user);
			this.isFriends(user);
			this.save();
		}
	}
	
	public void addFans(final User user){
		if(!this.fans.contains(user)){
			this.fans.add(user);
			this.isFriends(user);
			this.save();
		}
	}
	
	public void delFocus(User user) {
		User self = this.getUser();
		try{
			this.focus.remove(user);
			user.profile.fans.remove(self);
			if(this.friends.contains(user)){
				this.friends.remove(user);
				user.profile.friends.remove(self);
			}
			this.save();
			user.profile.save();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	private void isFriends(User user) {
		try{
			if(this.fans.contains(user) && 
					this.focus.contains(user) &&
					!this.friends.contains(user)){
				this.friends.add(user);
				this.save();
				
				User self = this.getUser();
				user.profile.friends.add(self);
				user.profile.save();
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}	
	
	private User getUser(){
		return User.find.where().eq("profile.id", this.id).findUnique();
	}
	
	public void recvNotification(final Notification notification){
		this.notifications.add(notification);
		this.save();
	}
	
	public void changeVisibility(visibilityCode visibility){
		this.visibility = visibility; //VISI_STATUS.get(visibility);
		this.save();
	}

}
