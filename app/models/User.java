package models;

import javax.persistence.Entity;

import play.db.ebean.Model;

//@Entity
public class User extends Model {

	private static final long serialVersionUID = 1L;

	private final String name;

	private final String pwdHash;

	public static enum Role {
		User, Admin
	};

	private final Role role;

	public static Model.Finder<Long, User> find = new Model.Finder<Long, User>(Long.class, User.class);

	public User(String name, String pwdHash, Role role) {
		this.name = name;
		this.pwdHash = pwdHash;
		this.role = role;
	}

	public String getName() {
		return name;
	}

	public String getPwdHash() {
		return pwdHash;
	}

	public Role getRole() {
		return role;
	}

	@Override
	public String toString() {
		return "User [username=" + name + ", password=" + pwdHash + ", role=" + role + "]";
	}

	public static User authenticate(String username, String password) {
		if (find.where().eq("username", username).eq("password", password).findList().size() > 0)
			return find.where().eq("username", username).eq("password", password).findList().get(0);

		return null;
	}

}
