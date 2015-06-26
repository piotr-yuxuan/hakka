package models;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import play.db.ebean.Model;

import com.avaje.ebean.Ebean;

public class Recipe extends Model {

	private static final long serialVersionUID = 1L;
	
	private Channel triggerChannel;
	private Trigger trigger;
	private Channel actionChannel;
	private Action action;
	/**
	 * Very important to be unique
	 */
	private String name;

	private String description;
	private boolean active;
	private List<String> log;
	private User user;
	private long id;

	public static Model.Finder<Long, Recipe> find = new Model.Finder<Long, Recipe>(Long.class, Recipe.class);

	public static List<Recipe> getAllRecipes() {
		return Ebean.find(Recipe.class).findList();
	}

	@Override
	public String toString() {
		return "Recipe [Id=" + id + ", user=" + user + ", name=" + name + ", thisChannel=" + triggerChannel
				+ ", thatChannel=" + actionChannel + "]";
	}

	public Recipe(Trigger trigger, Action action, User user, String name, String description) {
		super();
		this.triggerChannel = trigger.getChannel();
		this.trigger = trigger;
		this.actionChannel = action.getChannel();
		this.action = action;
		this.user = user;
		this.name = name;
		this.log = new LinkedList<String>();
	}

	@SuppressWarnings("unused")
	private void setId(long id) {
	}

	/*
	 * From here, generated methods
	 */

	public Channel getTriggerChannel() {
		return triggerChannel;
	}

	public void setTriggerChannel(Channel triggerChannel) {
		this.triggerChannel = triggerChannel;
	}

	public Trigger getTrigger() {
		return trigger;
	}

	public void setTrigger(Trigger trigger) {
		this.trigger = trigger;
	}

	public Channel getActionChannel() {
		return actionChannel;
	}

	public void setActionChannel(Channel actionChannel) {
		this.actionChannel = actionChannel;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public static Model.Finder<Long, Recipe> getFind() {
		return find;
	}

	public static void setFind(Model.Finder<Long, Recipe> find) {
		Recipe.find = find;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public List<String> getLog() {
		return log;
	}

	public long getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
