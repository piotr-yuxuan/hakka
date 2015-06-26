package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.db.ebean.Model;

//@Entity
public class Trigger extends Model {

	private static final long serialVersionUID = 1L;

	public static Model.Finder<Long, Trigger> find = new Finder<Long, Trigger>(Long.class, Trigger.class);

	private final Class<?> clazz;
	/**
	 * Important to be able to get it from this class because it can simplify
	 * Recipe contructor.
	 */
	// @ManyToOne(cascade = CascadeType.ALL)
	protected Channel channel;
	// @SuppressWarnings("rawtypes")
	// @OneToMany(mappedBy = "trigger", cascade = CascadeType.ALL)
	protected List<Modality> fields;

	@Id
	protected long id;
	protected String description;

	public Trigger(@SuppressWarnings("rawtypes") List<Modality> fields, Channel channel, Class<?> clazz,
			String description) {
		this.fields = fields;
		this.clazz = clazz;
		this.channel = channel;
		this.description = description;
	}

	/**
	 * id should be final, but if so the constructor should set it. This trick
	 * avoids it.
	 * 
	 * @param id
	 */
	@SuppressWarnings("unused")
	private void setId(long id) {
	}

	/*
	 * Below, generated methods.
	 */

	public Class<?> getClazz() {
		return clazz;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Modality> getModalities() {
		return fields;
	}

	public void setFields(List<Modality> fields) {
		this.fields = fields;
	}
}
