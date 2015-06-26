package models;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import play.db.ebean.Model;
import akka.actor.UntypedActor;

/**
 * Model is an abstraction of Actor. Because it takes a class reference, one
 * could have subtypes of this class. By the way, the best abstraction would be
 * to link a model to an interface which some actors would implements. It would
 * allow something like multiple inheritance. As an actor would implements
 * several interfaces, it could be sent different messages. The main issue with
 * this idea is an actor only receive message by onReceive() and the message
 * sending protocol doesn't imply any other method.
 */
// @Entity
public class Channel extends Model {

	private static final long serialVersionUID = 1L;

	// @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL)
	private List<Trigger> triggers;
	// @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL)
	private List<Action> actions;

	// @Id
	private long id;
	/**
	 * Actor class
	 */
	private Class<? extends UntypedActor> clazz;
	private String description;

	public final static Model.Finder<Long, Channel> find = new Model.Finder<Long, Channel>(Long.class, Channel.class);

	@Override
	public String toString() {
		return "Channel [id=" + id + ", name=" + clazz.getSimpleName() + ", description=" + description + ", triggers="
				+ triggers + ", actions=" + actions + "]";
	}

	public Channel(List<Trigger> triggers, List<Action> actions, Class<? extends UntypedActor> clazz, String description) {
		this.triggers = triggers;
		this.actions = actions;
		this.clazz = clazz;
		this.description = description;
	}

	@SuppressWarnings("unused")
	private void setId(long id) {
	}

	/*
	 * Below, automatically generated methods.
	 */

	public List<Trigger> getTriggers() {
		return triggers;
	}

	public void setTriggers(List<Trigger> triggers) {
		this.triggers = triggers;
	}

	public List<Action> getActions() {
		return actions;
	}

	public void setActions(List<Action> actions) {
		this.actions = actions;
	}

	public Class<? extends UntypedActor> getClazz() {
		return clazz;
	}

	/**
	 * This allows a recipe to be an abstraction
	 */
	public void setName(Class<? extends UntypedActor> clazz) {
		this.clazz = clazz;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public long getId() {
		return id;
	}
}
