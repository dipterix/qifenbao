package common.modelsIdentity;

import java.util.List;

import play.data.DynamicForm;
import models.Article;
import models.Comment;
import models.Notification;
import models.User;

import com.feth.play.module.pa.user.AuthUser;

import be.objectify.deadbolt.core.models.Subject;

/**
 * This interface defines the actions allowed
 * @author Zhengjia
 *
 */
public interface UserIdentity extends Subject{

	public boolean hasPermission(String permission);
	
	public Comment createComment(DynamicForm f);
	
	public void focus(final User user);
	
	public void recvNotification(Notification notice);
	
	public List<Article> listEditedArticles(int page);
	
	public void loseFocus(User user);
}
