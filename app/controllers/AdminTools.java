package controllers;

import java.util.List;

import models.Section;
import models.Tag;
import models.User;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;
import views.html.account.*;
import be.objectify.deadbolt.java.actions.*;


public class AdminTools extends Application{
	
	@Pattern(Application.USER_PERMISSION_ADMIN)
	/**
	 * For Admins to create a new section, this admin automatically become the section manager and editor
	 * @return
	 */
	public static Result sectionCreate(){
		User localUser = Application.getLocalUser(session());
		final DynamicForm request = Form.form().bindFromRequest();
		String sname = request.get("section");
		String alias = request.get("alias");
		if(sname != null && !sname.trim().equals("")){
			Section section = Section.createBlankSection(sname.trim(), alias);
			section.setManagers(localUser.name);
			localUser.save();
		}
		return redirect(routes.Manage.admin());
	}
	
	/**
	 * For section manager to add or delete editors
	 * @param name
	 * @return
	 */
	@Pattern(Application.USER_PERMISSION_SECTION_MANAGER)
	public static Result sectionUpdate(String name){
		User localUser = Application.getLocalUser(session());
		Section section = Section.getSectionByName(name);
		if(!localUser.masterSections.contains(section)){
			return redirect("/");
		}
		final DynamicForm request = Form.form().bindFromRequest();
		
		// Add Tags
		String tags = request.get("tags");
		String alias = request.get("alias");
		Tag tag = Tag.createTag(tags, alias);
		if(tag != null){
			section.tags.add(tag);
			section.save();
		}
		
		// Toggle managers, need both admin and manager permission
		String managers = request.get("manager");
		if(localUser.hasPermission(Application.USER_PERMISSION_ADMIN) && managers != null){
			section.setManagers(managers.split(","));
		}

		// Toggle Editors
		String editors = request.get("editor");
		if(editors != null){
			section.setEditors(editors.split(","));
		}
		
		return redirect(routes.Profiles.sectionsInfo(name));
	}
	
	
	
	private static boolean validateAdmin(String userMail){
		final User user = getLocalUser(session());
		if(userMail.equals(user.email)){
			return true;
		}else{
			return false;
		}
	}
	
	@Pattern(Application.USER_PERMISSION_ADMIN)
	public static Result admin(String userMail){
		if(validateAdmin(userMail)){
			List<User> adminList = User.find.where().eq("permissions.value",USER_PERMISSION_ADMIN).findList();
			List<User> editorList = User.find.where().eq("permissions.value", USER_PERMISSION_EDITOR).findList();
			return ok(admin.render(getLocalUser(session()), adminList, editorList,null));
		}
		else{
			return redirect(controllers.Application.index().toString());
		}
	}
	
	@Pattern(Application.USER_PERMISSION_ADMIN)
	public static Result adminPost(String userMail){
//		if(validateAdmin(userMail)){
//			String query = "find user where email=:email or name=:name";
//			final DynamicForm keys = Form.form().bindFromRequest();
//			final String email = keys.get("email");
//			final String name = keys.get("name");
//			final String sections = keys.get("sections");
//			
//			final List<User> target = User.find.setQuery(query)
//					.setParameter("email", email)
//					.setParameter("name", name).findList();
//			
//			if(sections != null){
//				Section.createSections(sections.split(","));
//			}
//			return ok(admin.render(getLocalUser(session()), null, null, target));
//		}
//		else{
//			return redirect(controllers.Application.index().toString());
//		}
		return TODO;
	}
	
	@Pattern(Application.USER_PERMISSION_ADMIN)
	public static Result editUser(Long id){
//		User target = User.find.where().eq("id", id).findUnique();
//		User admin = getLocalUser(session());
//		return ok(profile_change.render(admin, target));
		return TODO;
	}
	
	
}
