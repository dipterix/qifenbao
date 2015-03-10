package controllers;

import java.util.List;

import models.Article;
import models.User;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.restricted;
import views.html.*;
import be.objectify.deadbolt.java.actions.*;

@Security.Authenticated(Secured.class)
public class Restricted extends Controller {

	@Restrict(@Group(Application.USER_ROLE))
	public static Result index() {
		final User localUser = Application.getLocalUser(session());
		final List<Article> editedArticles = Article.getArticlesByPage(0, localUser);
		return ok(restricted.render(localUser, editedArticles));
	}
}
