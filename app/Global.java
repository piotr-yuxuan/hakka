import java.util.Arrays;
import java.util.List;

import models.Action;
import models.Modality;
import models.Trigger;
import play.GlobalSettings;
import play.Logger;
import play.db.ebean.Model;

import com.avaje.ebean.Ebean;

import controllers.Application;

/**
 * To understand how an instance of this class works, better to first give a
 * look to onStart().
 */
public class Global extends GlobalSettings {

	{
		Logger.info("\n\n\nHear me from static Global");
	}

	/*
	 * TODO First a script must populate the database. We faint it here
	 * programmatically but it should be performed directly on the DB.
	 */
	public void beforeStart(Application app) {
		/*
		 * La flemme. TODO pour toi lectrice ou lecteur ! Prend ta liberté en
		 * main et définit selon ton gré les channels qui colleront le mieux aux
		 * acteurs définis dans AllActors :) !
		 */

		Logger.info("\n\n\nHear me from Global");
		// DBerase();
		// DBpopulate();

	}

	private void generateStaticCausalityFromRecipes() {
		// Ebean.find(Recipe.class)
		// .findList()
		// .forEach(
		// recipe -> LogicController.getCommutator().addCausalRelation(
		// LogicController.getSystemProxy().getOrCreateStaticActorFor(recipe.getTriggerChannel()),
		// recipe.getTrigger().getClazz(),
		// recipe.getDescription(),
		// LogicController.getSystemProxy().getOrCreateStaticActorFor(recipe.getActionChannel()),
		// LogicController.getMessageMap().getMapper(recipe.getTrigger().getClazz(),
		// recipe.getAction().getClazz())));
	}

	private void generateCausalityFromRecipes() {
	}

	public void onStart(Application app) {

		// Logger.info("\n\n\nHear me from Global");
		//
		// /*
		// * First, to make it clear about functions defined on the fly, this is
		// * such a function that can multiply an integer by two:
		// */
		// @SuppressWarnings("unused")
		// Function<Integer, Integer> multiplyByTwo0 = n -> n * 2;
		// /*
		// * It's the same but the semantic is more precise to change for an
		// unary
		// * operator:
		// */
		// @SuppressWarnings("unused")
		// IntUnaryOperator multiplyByTwo1 = n -> n * 2;
		//
		// int thisWillBeFour = multiplyByTwo0.apply(2);
		// List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
		// int result = list.stream().filter(x -> x >
		// 7).map(multiplyByTwo0).reduce(Integer::sum).get();
		// boolean thisWillBeTrue = (result == 2 * (8 + 9));
		//
		 generateStaticCausalityFromRecipes();
		 generateCausalityFromRecipes();
	}

	public void onStop(Application app) {
		Logger.info("Application shutdown...");
	}

	/**
	 * Shouldn't it be avoid?
	 */
	@Deprecated
	private void DBerase() {
		List<Class> classes = Arrays.asList(Modality.class, Trigger.class, Action.class);
		classes.forEach(y -> Ebean.find(y).findList().forEach(x -> ((Model) x).delete()));
	}

	/**
	 * Shouldn't it be performed by a script once for all?
	 */
	@Deprecated
	private void DBpopulate() {// Users
		// User user1 = new User("1", "1", "user", "home1");
		// user1.save();
		//
		// User user2 = new User("2", "2", "administrator", "home1");
		// user2.save();
		//
		// // We can't have human channel : it's not a channel as define as
		// object
		// // from IoT.
		//
		// Channel manythings, garage;
		//
		// /*
		// * Triggers and Actions are needed only for the interface. If we keep
		// * programmatically working on causality, we don't need them.
		// */
		// List<Trigger> triggers = new ArrayList<Trigger>();
		// List<Action> actions = new ArrayList<Action>();
		//
		// (new Channel(triggers, actions, Manythings.class,
		// "Manythings")).save();
		// (new Channel(triggers, actions, Lamp.class, "Manythings")).save();
		// (new Channel(triggers, actions, PresenceDetector.class,
		// "Manythings")).save();

		// // PRESENCE DETECTOR CHANNEL
		// Channel detector = new Channel("Detector", "Detects humans",
		// AllActors.detector);
		// detector.save();
		//
		// // Trigger detectorTrigger1 = new Trigger("Presence Trigger",
		// // "Trigger description", AllMessages.DetectionOn.class);
		// Trigger detectorTrigger1 = new Trigger("Presence Trigger",
		// "Trigger description");
		// detector.getTriggers().add(detectorTrigger1);
		// detectorTrigger1.setChannel(detector);
		// detectorTrigger1.save();
		//
		// // Trigger detectorTrigger2 = new Trigger("Non presence Trigger",
		// // AllMessages.DetectionOff.class);
		// Trigger detectorTrigger2 = new Trigger("Non presence Trigger");
		// detector.getTriggers().add(detectorTrigger2);
		// detectorTrigger2.setChannel(detector);
		// detectorTrigger2.save();
		//
		// detector.save();
		//
		// // LUMINOSITY DETECTOR CHANNEL
		// Channel luminosityDetector = new Channel("Luminosity detector",
		// "Detects luminosity",
		// AllActors.luminosityDetector);
		// luminosityDetector.save();
		//
		// // Keep in mind to change null pointer for messages
		// // Trigger detectorTrigger12 = new Trigger("Light Trigger",
		// // "Trigger description 00000", null);
		// Trigger detectorTrigger12 = new Trigger("Light Trigger",
		// "Trigger description 00000");
		// luminosityDetector.getTriggers().add(detectorTrigger12);
		//
		// detectorTrigger12.save();
		//
		// List<Field> fields = new ArrayList<Field>();
		// for (int i = 0; i < 2; i++) {
		// Field field = new Field((new String("Field name " + i)), new
		// String("Field description " + i));
		// field.setTrigger(detectorTrigger12);
		// field.save();
		// fields.add(field);
		//
		// }
		//
		// detectorTrigger12.setChannel(detector);
		// detectorTrigger12.save();
		//
		// Trigger detectorTrigger13 = new Trigger("Non light Trigger", null);
		// luminosityDetector.getTriggers().add(detectorTrigger13);
		// detectorTrigger13.setChannel(luminosityDetector);
		// detectorTrigger13.save();
		//
		// // Trigger detectorTrigger14 = new Trigger("Light Trigger1",
		// // "Trigger description", null);
		// Trigger detectorTrigger14 = new Trigger("Light Trigger1",
		// "Trigger description");
		// luminosityDetector.getTriggers().add(detectorTrigger14);
		// detectorTrigger14.save();
		// detectorTrigger14.setChannel(detector);
		//
		// // Trigger detectorTrigger15 = new Trigger("Light Trigger2",
		// // "Trigger description", null);
		// Trigger detectorTrigger15 = new Trigger("Light Trigger2",
		// "Trigger description");
		// luminosityDetector.getTriggers().add(detectorTrigger15);
		// detectorTrigger15.save();
		// detectorTrigger15.setChannel(detector);
		//
		// // Trigger detectorTrigger16 = new Trigger("Light Trigger3",
		// // "Trigger description", null);
		// Trigger detectorTrigger16 = new Trigger("Light Trigger3",
		// "Trigger description");
		// luminosityDetector.getTriggers().add(detectorTrigger16);
		// detectorTrigger16.save();
		// detectorTrigger16.setChannel(detector);
		//
		// // Trigger detectorTrigger17 = new Trigger("Light Trigger",
		// // "Trigger description", null);
		// Trigger detectorTrigger17 = new Trigger("Light Trigger",
		// "Trigger description");
		// luminosityDetector.getTriggers().add(detectorTrigger17);
		// detectorTrigger17.save();
		// detectorTrigger17.setChannel(detector);
		//
		// // Trigger detectorTrigger18 = new Trigger("Light Trigger",
		// // "Trigger description", null);
		// Trigger detectorTrigger18 = new Trigger("Light Trigger",
		// "Trigger description");
		// luminosityDetector.getTriggers().add(detectorTrigger18);
		// detectorTrigger18.save();
		// detectorTrigger18.setChannel(detector);
		//
		// luminosityDetector.save();
		//
		// // LAMP CHANNEL
		// Channel lamp = new Channel("Lamp", "I am a Lamp", AllActors.lamp);
		// lamp.save();
		//
		// Action lampAction1 = new Action("Turn on lamp");
		// lamp.getActions().add(lampAction1);
		// lampAction1.setChannel(lamp);
		// lampAction1.save();
		//
		// Action lampAction2 = new Action("Turn off lamp");
		// lamp.getActions().add(lampAction2);
		// lampAction2.setChannel(lamp);
		// lampAction2.save();
		//
		// lamp.save();
	}
}