package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Article;
import models.Section;
import models.Tag;
import models.User;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;
import views.html.profile.*;

/**
 * This controller controls the information which could be shown to the public
 * Profile:
 * 		Tags
 * 		Editor
 * 		Normal Users
 * 		About...
 * @author Zhengjia
 *
 */
public class Profiles extends Controller{
	
	public static Result tagsInfo(String name, int page){
		User localUser = Application.getLocalUser(session());
		Tag tag = Tag.find.where().eq("name", name.trim()).findUnique();
		List<Article> aList = Article.getByTag(page-1, tag);
		return ok(tagInfo.render(localUser, tag, aList, page));
	}
	
	public static Result editorInfo(String name){
		User target = User.findByName(name);
		return ok(userInfo.render(target));
	}

	public static Result sectionsInfo(String name){
		User localUser = Application.getLocalUser(session());
		Section section = Section.find.where().eq("name", name.trim()).findUnique();
		return ok(sectionInfo.render(localUser, section));
	}
}
