package models;

import java.util.HashMap;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.avaje.ebean.annotation.ConcurrencyMode;
import com.avaje.ebean.annotation.EntityConcurrencyMode;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

@Entity
@Table(name = "notifications")
@EntityConcurrencyMode(ConcurrencyMode.NONE)
public class Notification extends Model {
	private static final long serialVersionUID = 1L;
	
	public static enum noteStatus{Unread, Read}; 
	
	public static enum noteType{Instant, FriendRequest, Normal}; 

	@Id
	public Long id;
	
	@Required
	public String value;
	
	public noteStatus status;
	
	public noteType type;
	
	public static Finder<Notification, Long> find = new Finder<Notification, Long>(Notification.class, Long.class);
	
	/**
	 * Constructor for a notification
	 * @param value
	 */
	public static Notification createNotification(String value, noteType type){
		Notification n = new Notification();
		n.value = value;
		n.status = noteStatus.Unread;
		n.type = type;
		n.save();
		return n;
	}
	
	public void read(){
		this.status = noteStatus.Read;
		this.save();
	}
	
	
	
}
