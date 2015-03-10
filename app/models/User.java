package models;

import be.objectify.deadbolt.core.models.Permission;
import be.objectify.deadbolt.core.models.Role;
import be.objectify.deadbolt.core.models.Subject;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthUser;
import com.feth.play.module.pa.user.AuthUser;
import com.feth.play.module.pa.user.AuthUserIdentity;
import com.feth.play.module.pa.user.EmailIdentity;
import com.feth.play.module.pa.user.NameIdentity;
import com.feth.play.module.pa.user.FirstLastNameIdentity;

import common.modelsIdentity.UserIdentity;
import controllers.Application;
import models.TokenAction.Type;
import play.Logger;
import play.data.DynamicForm;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.db.ebean.Model.*;

import javax.persistence.*;

import java.util.*;

/**
 * Initial version based on work by Steve Chaloner (steve@objectify.be) for
 * Deadbolt2
 */
@Entity
@Table(name = "users")
public class User extends Model implements UserIdentity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String DEFAULT_ADMIN = "dipterix.wang@gmail.com";
	@Id
	public Long id;

	@Constraints.Email
	// if you make this unique, keep in mind that users *must* merge/link their
	// accounts then on signup with additional providers
	// @Column(unique = true)
	public String email;

	public String name;
	
	public String firstName;
	
	public String lastName;

	@Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date lastLogin;

	public boolean active;

	public boolean emailValidated;

	@ManyToMany
	public List<SecurityRole> roles;

	@OneToMany(cascade = CascadeType.ALL)
	public List<LinkedAccount> linkedAccounts;

	@ManyToMany
	public List<UserPermission> permissions;
	
	@ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name="editor_section")
	public List<Section> editSections;
	
	@ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name="manager_section")
	public List<Section> masterSections;
	

//	@ManyToMany
	@OneToOne(targetEntity=UserProfile.class)
	@JoinColumn(name="PROFILE_ID")
	public UserProfile profile;

	@Override
	public String getIdentifier()
	{
		return Long.toString(id);
	}

	@Override
	public List<? extends Role> getRoles() {
		return roles;
	}

	@Override
	public List<? extends Permission> getPermissions() {
		return permissions;
	}

	/*
	 * Preset methods in Java Play Authentication module
	 */
	public static boolean existsByAuthUserIdentity(
			final AuthUserIdentity identity) {
		final ExpressionList<User> exp;
		if (identity instanceof UsernamePasswordAuthUser) {
			exp = getUsernamePasswordAuthUserFind((UsernamePasswordAuthUser) identity);
		} else {
			exp = getAuthUserFind(identity);
		}
		return exp.findRowCount() > 0;
	}

	private static ExpressionList<User> getAuthUserFind(
			final AuthUserIdentity identity) {
		return find.where().eq("active", true)
				.eq("linkedAccounts.providerUserId", identity.getId())
				.eq("linkedAccounts.providerKey", identity.getProvider());
	}

	public static User findByAuthUserIdentity(final AuthUserIdentity identity) {
		if (identity == null) {
			return null;
		}
		if (identity instanceof UsernamePasswordAuthUser) {
			return findByUsernamePasswordIdentity((UsernamePasswordAuthUser) identity);
		} else {
			return getAuthUserFind(identity).findUnique();
		}
	}

	public static User findByUsernamePasswordIdentity(
			final UsernamePasswordAuthUser identity) {
		return getUsernamePasswordAuthUserFind(identity).findUnique();
	}

	private static ExpressionList<User> getUsernamePasswordAuthUserFind(
			final UsernamePasswordAuthUser identity) {
		return getEmailUserFind(identity.getEmail()).eq(
				"linkedAccounts.providerKey", identity.getProvider());
	}

	public void merge(final User otherUser) {
		for (final LinkedAccount acc : otherUser.linkedAccounts) {
			this.linkedAccounts.add(LinkedAccount.create(acc));
		}
		// do all other merging stuff here - like resources, etc.

		// deactivate the merged user that got added to this one
		otherUser.active = false;
		Ebean.save(Arrays.asList(new User[] { otherUser, this }));
	}
	


	public static void merge(final AuthUser oldUser, final AuthUser newUser) {
		User.findByAuthUserIdentity(oldUser).merge(
				User.findByAuthUserIdentity(newUser));
	}

	public Set<String> getProviders() {
		final Set<String> providerKeys = new HashSet<String>(
				linkedAccounts.size());
		for (final LinkedAccount acc : linkedAccounts) {
			providerKeys.add(acc.providerKey);
		}
		return providerKeys;
	}

	public static void addLinkedAccount(final AuthUser oldUser,
			final AuthUser newUser) {
		final User u = User.findByAuthUserIdentity(oldUser);
		u.linkedAccounts.add(LinkedAccount.create(newUser));
		u.save();
	}

	
	/**
	 * Save changes to user
	 * @param user
	 */
	private static void save(final User user){
		user.save();
		user.saveManyToManyAssociations("roles");
		user.saveManyToManyAssociations("permissions");
		user.saveManyToManyAssociations("editSections");
		user.saveManyToManyAssociations("masterSections");
	}
	
	@Override
	public String toString(){
		return this.name;
	}
	
	/*
	 * CRUD: create a new user
	 */
	/**
	 * create a new authenticated user
	 * @param authUser
	 * @param isValidated
	 * @return
	 */
	public static User newUser(final AuthUser authUser, final boolean isValidated){
		final User user = new User();
		user.roles = Collections.singletonList(SecurityRole
				.findByRoleName(controllers.Application.USER_ROLE));
		user.permissions = new ArrayList<UserPermission>();
		user.permissions.add(UserPermission.findByValue(controllers.Application.USER_PERMISSION_NORMAL));
		user.editSections = new ArrayList<Section>();
		user.masterSections = new ArrayList<Section>();
		user.active = true;
		user.lastLogin = new Date();
		user.linkedAccounts = Collections.singletonList(LinkedAccount
				.create(authUser));
		user.profile = UserProfile.createUserProfile();

		if (authUser instanceof EmailIdentity) {
			final EmailIdentity identity = (EmailIdentity) authUser;
			// Remember, even when getting them from FB & Co., emails should be
			// verified within the application as a security breach there might
			// break your security as well!
			user.email = identity.getEmail();
			user.emailValidated = isValidated;
			if(user.email.equals(DEFAULT_ADMIN)){
				//TODO add admin permission
				user.permissions.add(UserPermission.findByValue(controllers.Application.USER_PERMISSION_ADMIN));
				Logger.info("Admin Created");
			}
		}

		if (authUser instanceof NameIdentity) {
			final NameIdentity identity = (NameIdentity) authUser;
			final String name = identity.getName();
			if (name != null) {
				user.name = name;
			}
		}
		
		if (authUser instanceof FirstLastNameIdentity) {
		  final FirstLastNameIdentity identity = (FirstLastNameIdentity) authUser;
		  final String firstName = identity.getFirstName();
		  final String lastName = identity.getLastName();
		  if (firstName != null) {
		    user.firstName = firstName;
		  }
		  if (lastName != null) {
		    user.lastName = lastName;
		  }
		}
		
		return user;
	}
	
	public static User create(final AuthUser authUser) {
		final User user = newUser(authUser, false);
		save(user);
		return user;
	}
	
	/*
	 * don't validate on first register!!!
	 */
	public static User createOnRegister(final AuthUser authUser) {
		final User user = newUser(authUser, true);
		verify(user);
		return user;
	}
	

	public static void setLastLoginDate(final AuthUser knownUser) {
		final User u = User.findByAuthUserIdentity(knownUser);
		u.lastLogin = new Date();
		u.save();
	}


	/*
	 * CRUD: Update
	 */
	public static void verify(final User unverified) {
		// You might want to wrap this into a transaction
		unverified.emailValidated = true;
		unverified.save();
		TokenAction.deleteByUser(unverified, Type.EMAIL_VERIFICATION);
	}

	public void changePassword(final UsernamePasswordAuthUser authUser,
			final boolean create) {
		LinkedAccount a = this.getAccountByProvider(authUser.getProvider());
		if (a == null) {
			if (create) {
				a = LinkedAccount.create(authUser);
				a.user = this;
			} else {
				throw new RuntimeException(
						"Account not enabled for password usage");
			}
		}
		a.providerUserId = authUser.getHashedPassword();
		a.save();
	}

	
	public void resetPassword(final UsernamePasswordAuthUser authUser,
			final boolean create) {
		// You might want to wrap this into a transaction
		this.changePassword(authUser, create);
		TokenAction.deleteByUser(this, Type.PASSWORD_RESET);
	}
	

	/*
	 * CRUD: Read
	 */
	public static User findByEmail(final String email) {
		return getEmailUserFind(email).findUnique();
	}

	private static ExpressionList<User> getEmailUserFind(final String email) {
		return find.where().eq("active", true).eq("email", email);
	}

	public LinkedAccount getAccountByProvider(final String providerKey) {
		return LinkedAccount.findByProviderKey(this, providerKey);
	}
	
	@Override
	public boolean hasPermission(String per){
		for(UserPermission p:this.permissions){
			if(p.value.equals(per)){
				return true;
			}
		}
		return false;
	}
	
	public void togglePermission(String... permissions){
		for(String permission:permissions){
			UserPermission p = UserPermission.find.where().eq("value", permission.trim()).findUnique();
			if(p != null){
				if(this.permissions.contains(p)){
					this.permissions.remove(p);
				}
				else{
					this.permissions.add(p);
				}
			}
		}
		this.save();
	}
	
	public void addPermission(String... permissions){
		for(String permission:permissions){
			UserPermission p = UserPermission.find.where().eq("value", permission.trim()).findUnique();
			if(p != null){
				if(!this.permissions.contains(p)){
					this.permissions.add(p);
				}
			}
		}
		this.save();
	}
	
	public void removePermission(String... permissions){
		for(String permission:permissions){
			UserPermission p = UserPermission.find.where().eq("value", permission.trim()).findUnique();
			if(p != null){
				if(this.permissions.contains(p)){
					this.permissions.remove(p);
				}
			}
		}
		this.save();
	}
	
	@Override
	public Comment createComment(DynamicForm f){
		Comment comm = new Comment();
		comm.email = this.email;
		comm.name = f.get("name");
		comm.content = f.get("content");
		comm.save();
		return comm;
	}
	
	public static final Finder<Long, User> find = new Finder<Long, User>(
			Long.class, User.class);


	public static User temporaryUser(String email, String name) {
		User user = new User();
		user.email = email;
		user.name = name;
		new TemproraryUser(email, name);
		return user;
	}

	@Override
	public void focus(final User user) {
		this.profile.addFocus(user);
		user.profile.addFans(this);
	}
	
	@Override
	public void loseFocus(final User user){
		this.profile.delFocus(user);
	}
	
	@Override
	public void recvNotification(final Notification notice){
		this.profile.recvNotification(notice);
	}
	
	@Override
	public List<Article> listEditedArticles(int page){
		 return Article.getArticlesByPage(page, this);
	}

	public static User findByName(String name) {
		return find.where().eq("name", name).findUnique();
	}
	
	public boolean isManageTag(Tag tag){
		if(this.hasPermission(Application.USER_PERMISSION_SECTION_MANAGER)){
			for(Section section:this.masterSections){
				if(section.tags.contains(tag)){
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean isManageSection(Section section){
		if(this.hasPermission(Application.USER_PERMISSION_SECTION_MANAGER)){
			if(this.masterSections.contains(section)){
				return true;
			}
		}
		return false;
	}
}


