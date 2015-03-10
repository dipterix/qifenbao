package controllers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import models.Article;
import models.User;
import play.Logger;
import play.Routes;
import play.data.Form;
import play.mvc.*;
import play.mvc.Http.Response;
import play.mvc.Http.Session;
import providers.MyUsernamePasswordAuthProvider;
import providers.MyUsernamePasswordAuthProvider.MyLogin;
import providers.MyUsernamePasswordAuthProvider.MySignup;
import views.html.*;
import views.html.account.*;
import be.objectify.deadbolt.java.actions.*;

import com.avaje.ebean.FutureRowCount;
import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthProvider;
import com.feth.play.module.pa.user.AuthUser;

public class Application extends Controller {

	public static final String FLASH_MESSAGE_KEY = "message";
	public static final String FLASH_ERROR_KEY = "error";
	public static final String USER_ROLE = "user";
	public static final String[] USER_PERMISSION = new String[]{
		"admin", "editor", "section manager", "normal"
	};
	public static final String USER_PERMISSION_NORMAL = "normal";
	public static final String USER_PERMISSION_ADMIN = "admin";
	public static final String USER_PERMISSION_EDITOR = "editor";
	public static final String USER_PERMISSION_SECTION_MANAGER = "section manager";
	
	public static final String[] SECTION_NAME = new String[]{"测试"};
	public static final String[] TAG_NAME = new String[]{"test tag"};
	
	private static final String HOST = "127.0.0.1:9000";
	
	public static Result back(){
		String lastPage = Http.Context.current().request().getHeader("Referer");
		String lastHost = Http.Context.current().request().getHeader("Host");
		if(lastPage == null || !lastHost.contains(HOST)){
			return redirect(routes.Application.index());
		}
		return redirect(lastPage);
	}
	
	public static Result indexPage(int page) {
		String expected = routes.Application.restricted().toString();
		String lastPage = Http.Context.current().request().getHeader("Referer");
		String lastHost = Http.Context.current().request().getHeader("Host");
		User user = getLocalUser(session());
		if(page < 1){
			return redirect(expected);
		}
		List<Article> list = Article.getArticlesByPage(page-1, null);

		return ok(index.render(list, page));
	}
	
	public static Result index(){
		return indexPage(1);
	}

	public static User getLocalUser(final Session session) {
		final AuthUser currentAuthUser = PlayAuthenticate.getUser(session);
		final User localUser = User.findByAuthUserIdentity(currentAuthUser);
		if(localUser == null){
			return(User.temporaryUser(null, null));
		}
		return localUser;
	}

	//XXX
	@Restrict(@Group(Application.USER_ROLE))
	public static Result restricted() {
		final User localUser = getLocalUser(session());
		final List<Article> editedArticles = Article.getArticlesByPage(0, localUser);
		return ok(restricted.render(localUser, editedArticles));
	}

	@Restrict(@Group(Application.USER_ROLE))
	public static Result profile() {
		final User localUser = getLocalUser(session());
		return ok(profile.render(localUser));
	}

	public static Result login() {
		return ok(login.render(MyUsernamePasswordAuthProvider.LOGIN_FORM));
	}

	public static Result doLogin() {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		final Form<MyLogin> filledForm = MyUsernamePasswordAuthProvider.LOGIN_FORM
				.bindFromRequest();
		if (filledForm.hasErrors()) {
			// User did not fill everything properly
			return badRequest(login.render(filledForm));
		} else {
			// Everything was filled
			return UsernamePasswordAuthProvider.handleLogin(ctx());
		}
	}

	public static Result signup() {
		return ok(signup.render(MyUsernamePasswordAuthProvider.SIGNUP_FORM));
	}

	public static Result jsRoutes() {
		return ok(
				Routes.javascriptRouter("jsRoutes",
						controllers.routes.javascript.Signup.forgotPassword()))
				.as("text/javascript");
	}

	public static Result doSignup() {
		com.feth.play.module.pa.controllers.Authenticate.noCache(response());
		final Form<MySignup> filledForm = MyUsernamePasswordAuthProvider.SIGNUP_FORM
				.bindFromRequest();
		if (filledForm.hasErrors()) {
			// User did not fill everything properly
			return badRequest(signup.render(filledForm));
		} else {
			// Everything was filled
			// do something with your part of the form before handling the user
			// signup
			UsernamePasswordAuthProvider.handleSignup(ctx());
			return redirect(routes.Application.profile());
		}
	}

	public static String formatTimestamp(final long t) {
		return new SimpleDateFormat("yyyy-dd-MM HH:mm:ss").format(new Date(t));
	}

}