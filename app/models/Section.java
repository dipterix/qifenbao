package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import controllers.Application;
import play.Logger;
import play.data.DynamicForm;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;
import play.db.ebean.Model.*;

/**
 * Defines Section, which contains tags
 * Sections are usually not visible to the public
 * @author Zhengjia
 *
 */
@Entity
public class Section extends Model{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	public Long id;
	
	@Required
	/**
	 * Must be a-z, A-Z, 0-9
	 */
	public String name = "";
	
	@Required
	/**
	 * Can contains Chinese
	 */
	public String alias = "";
	
	@ManyToMany
	/**
	 * section contains many tags, which are treated as classifiers of articles
	 */
	public List<Tag> tags = new ArrayList<Tag>();
	
	
	/**
	 * Public, create one section as long as the section name is not NULL nor ""
	 * @param name	name of section
	 * @return the section if succeeded, else return null
	 */
	public static Section createBlankSection(String name, String alias){
		Section sec = find.where().eq("name", name).findUnique();
		if(sec == null){
			sec = new Section();
			sec.name = name;
			if(alias == null || alias.equals("")){
				sec.alias = name;
			}
			else{
				sec.alias = alias;
			}
			if(sec.name != null && !sec.name.trim().equals("")){
				save(sec);
			}
			else{
				return null;
			}
		}
		return sec;
	}
	
	public static Section save(Section section) {
		try{
			section.save();
			section.saveManyToManyAssociations("tags");
		}catch(Exception e){
			Logger.error("[Section Error]: Error in Saving...");
		}
		return section;
	}

	/**
	 * Judge if the section contains tag
	 * @param name	tag name
	 * @return	true if contains, otherwise false
	 */
	public boolean containsTag(String name){
		for(Tag tag:this.tags){
			if(tag.name.equals(name.trim())){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Ebean finder for Section
	 */
	public static Finder<Long, Section> find = new Finder<Long, Section>(Long.class, Section.class);
	
	
	/**
	 * Get editor for this section
	 * @return
	 */
	public List<User> getEditors(){
		List<User> e = new ArrayList<User>();
		e.addAll(User.find.where().eq("editSections", this).findList());
		return e;
	} 
	
	/**
	 * Get manager for the section
	 * @return
	 */
	public List<User> getManagers(){
		List<User> e = new ArrayList<User>();
		e.addAll(User.find.where().eq("masterSections", this).findList());
		return e;
	}
	
	/**
	 * Set editors, two cases: <br />
	 * 1. if editor not present, add this guy and grant him permission as an editor <br/>
	 * 2. if editor exists, the manager wants to kick this guy out.<br/>
	 * NOTE, if this guy is manager, then you cannot kick this guy out, run setManagers() first
	 * @param names
	 */
	public void setEditors(String... names){
		if(names != null){
			for(String name:names){
				User u = User.findByName(name.trim());
				if(u != null){
					if(!u.editSections.contains(this)){
						u.editSections.add(this);
						u.addPermission(Application.USER_PERMISSION_EDITOR);
					}
					else{
						// if not manager, kick
						if(!u.masterSections.contains(this)){
							u.editSections.remove(this);
							// check permission
							if(u.editSections.size() == 0){
								u.removePermission(Application.USER_PERMISSION_EDITOR);
							}
						}
					}
					u.save();
				}
			}
		}
	}
	
	/**
	 * Similar to setEditors(), make sure run this prior to setEditors in administrator tool<br/>
	 * NOTE, in remove mode, only remove manager status, keep editor instead.
	 * @param names
	 */
	public void setManagers(String... names){
		if(names != null){
			for(String name:names){
				User u = User.findByName(name.trim());
				if(u != null){
					if(!u.masterSections.contains(this)){
						if(!u.editSections.contains(this)){
							u.editSections.add(this);
						}
						u.masterSections.add(this);
						u.addPermission(Application.USER_PERMISSION_EDITOR);
						u.addPermission(Application.USER_PERMISSION_SECTION_MANAGER);
					}
					else{
						u.masterSections.remove(this);
						if(u.masterSections.size() == 0){
							u.removePermission(Application.USER_PERMISSION_SECTION_MANAGER);
						}
						if(u.editSections.size() == 0){
							u.removePermission(Application.USER_PERMISSION_EDITOR);
						}
					}
					u.save();
				}
			}
		}
	}
	
	public static Section getSectionByName(final String name){
		return find.where().eq("name", name).findUnique();
	}
}
