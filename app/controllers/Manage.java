package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Article;
import models.Section;
import models.Tag;
import models.User;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;
import views.html.manage.*;
import be.objectify.deadbolt.java.actions.*;

public class Manage extends Controller{
	
	@Restrict(@Group(Application.USER_ROLE))
	public static Result index(){
		User localUser = Application.getLocalUser(session());
		if(!localUser.hasPermission(Application.USER_PERMISSION_EDITOR)){
			if(!localUser.hasPermission(Application.USER_PERMISSION_SECTION_MANAGER)){
				if(!localUser.hasPermission(Application.USER_PERMISSION_ADMIN)){
					return redirect(routes.Application.signup());
				}
			}
		}
		return ok(manager.render(localUser));
	}
	
	@Pattern(Application.USER_PERMISSION_EDITOR)
	public static Result editorPage(int page){
		final User localUser = Application.getLocalUser(session());
		final List<Article> editedArticles = Article.getArticlesByPage(page-1, localUser);
		return ok(editor.render(localUser, editedArticles, page));
	}
	
	@Pattern(Application.USER_PERMISSION_ADMIN)
	public static Result admin(){
		final User localUser = Application.getLocalUser(session());
		final List<Section> sections = Section.find.all();
		return ok(administrator.render(localUser, sections));
	}
}
