import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import models.Action;
import models.Channel;
import models.Field;
import models.Log;
import models.Recipe;
import models.RecipeAkka;
import models.Scheduler;
import models.Scheduler.CancellableRef;
import models.Scheduler.RandomPeriodFactory;
import models.Scheduler.StopCriteria;
import models.StdRandom;
import models.Trigger;
import models.User;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import scala.concurrent.duration.Duration;
import actors.AllActors;

import com.avaje.ebean.Ebean;

import controllers.SystemController;

public class Global extends GlobalSettings {

	public void onStart(Application app) {
		// this map will containt the mapper from normal recipe to RecipeAkka
		RecipeAkka.recipesMap = new HashMap<Long, RecipeAkka>();

		System.out.println("test");
		System.out.println("TESTING THE DATABASE LOAD");

		// if (Ebean.find(Recipe.class).findRowCount() == 0) {

		List<Recipe> recipesList = Ebean.find(Recipe.class).findList();
		// channelsList.removeAll(channelsList);
		for (Recipe r : recipesList) {
			System.out.println(r);
			r.delete();
		}

		List<Field> fieldsList = Ebean.find(Field.class).findList();
		// channelsList.removeAll(channelsList);
		for (Field f : fieldsList) {
			System.out.println("deleting field...: " + f);
			f.delete();
		}

		List<Trigger> triggersList = Ebean.find(Trigger.class).findList();
		// triggersList.removeAll(triggersList);
		for (Trigger t : triggersList) {
			System.out.println(t.getName());
			t.delete();
		}

		List<Action> actionsList = Ebean.find(Action.class).findList();
		// triggersList.removeAll(actionsList);
		for (Action a : actionsList) {
			System.out.println(a.getName());
			a.delete();
		}

		List<Channel> channelsList = Ebean.find(Channel.class).findList();
		// channelsList.removeAll(channelsList);
		for (Channel c : channelsList) {
			System.out.println(c);
			c.delete();
		}

		List<User> usersList = Ebean.find(User.class).findList();
		// channelsList.removeAll(channelsList);
		for (User u : usersList) {
			System.out.println(u);
			u.delete();
		}

		Logger.info("Init Data");

		// Users
		User user1 = new User("1", "1", "user", "home1");
		user1.save();

		User user11 = new User("11", "11", "user", "home1");
		user11.save();

		User user111 = new User("11", "11", "user", "home1");
		user111.save();

		User user2 = new User("2", "2", "user", "home2");
		user2.save();

		User user22 = new User("22", "22", "user", "home2");
		user22.save();

		User user222 = new User("222", "222", "user", "home2");
		user222.save();

		User admin = new User("admin", "admin", "administrator", "admin1");
		admin.save();

		// HUMAN CHANNEL
		Channel human = new Channel("Human", "Can enter or exit room");
		human.setLogo("https://d3rnbxvnd0hlox.cloudfront.net/images/channels/1845421835/icons/regular.png");
		human.save();

		Action humanEnterRoomAction = new Action("Enter room");
		human.getActions().add(humanEnterRoomAction);
		humanEnterRoomAction.setChannel(human);
		humanEnterRoomAction.save();

		Action humanExitRoomAction = new Action("Exit room");
		human.getActions().add(humanExitRoomAction);
		humanExitRoomAction.setChannel(human);
		humanExitRoomAction.save();

		human.save();

		// PRESENCE DETECTOR CHANNEL
		Channel detector = new Channel("Detector", "Detects humans");
		detector.setLogo("https://d3rnbxvnd0hlox.cloudfront.net/images/channels/85/icons/regular.png");
		detector.save();

		// Trigger detectorTrigger1 = new Trigger("Presence Trigger",
		// "Trigger description", AllMessages.DetectionOn.class);
		Trigger detectorTrigger1 = new Trigger("Detection On", "Trigger description");
		// detector.getTriggers().add(detectorTrigger1);
		detectorTrigger1.setChannel(detector);
		detectorTrigger1.save();
		// Field f = new Field("My Field", "My description");
		// f.setTrigger(detectorTrigger1);
		// f.save();
		// detectorTrigger1.setField(f);
		detectorTrigger1.save();

		// // Trigger detectorTrigger2 = new Trigger("Non presence Trigger",
		// AllMessages.DetectionOff.class);
		// Trigger detectorTrigger2 = new Trigger("Non presence Trigger");
		// // detector.getTriggers().add(detectorTrigger2);
		// detectorTrigger2.setChannel(detector);
		// detectorTrigger2.save();
		//
		// detector.save();

		// LUMINOSITY DETECTOR CHANNEL
		Channel luminosityDetector = new Channel("Luminosity detector", "Detects luminosity");
		luminosityDetector.setLogo("https://d3rnbxvnd0hlox.cloudfront.net/images/channels/85/icons/regular.png");
		luminosityDetector.save();

		// Keep in mind to change null pointer for messages
		// Trigger detectorTrigger12 = new Trigger("Light Trigger",
		// "Trigger description 00000", null);
		Trigger detectorTrigger12 = new Trigger("Light Trigger", "Trigger description 00000");
		detectorTrigger12.setChannel(luminosityDetector);
		detectorTrigger12.save();

		detectorTrigger12.save();

		Trigger detectorTrigger13 = new Trigger("Non light Trigger", null);
		detectorTrigger13.setChannel(luminosityDetector);
		detectorTrigger13.save();

		// Trigger detectorTrigger14 = new Trigger("Light Trigger1",
		// "Trigger description", null);
		Trigger detectorTrigger14 = new Trigger("Light Trigger1", "Trigger description");
		// luminosityDetector.getTriggers().add(detectorTrigger14);
		detectorTrigger14.setChannel(detector);
		detectorTrigger14.save();

		// Trigger detectorTrigger15 = new Trigger("Light Trigger2",
		// "Trigger description", null);
		Trigger detectorTrigger15 = new Trigger("Light Trigger2", "Trigger description");
		// luminosityDetector.getTriggers().add(detectorTrigger15);
		detectorTrigger15.setChannel(detector);
		detectorTrigger15.save();

		// Trigger detectorTrigger16 = new Trigger("Light Trigger3",
		// "Trigger description", null);
		Trigger detectorTrigger16 = new Trigger("Light Trigger3", "Trigger description");
		// luminosityDetector.getTriggers().add(detectorTrigger16);
		detectorTrigger16.setChannel(detector);
		detectorTrigger16.save();

		// Trigger detectorTrigger17 = new Trigger("Light Trigger",
		// "Trigger description", null);
		Trigger detectorTrigger17 = new Trigger("Light Trigger4", "Trigger description");
		// luminosityDetector.getTriggers().add(detectorTrigger17);
		detectorTrigger17.setChannel(detector);
		detectorTrigger17.save();

		//
		Trigger detectorTrigger18 = new Trigger("Light Trigger5", "Trigger description");
		// luminosityDetector.getTriggers().add(detectorTrigger18);
		detectorTrigger18.setChannel(detector);
		detectorTrigger18.save();

		detector.save();
		luminosityDetector.save();

		detector.save();

		// LAMP CHANNEL
		Channel lamp = new Channel("Lamp", "I am a Lamp");
		lamp.setLogo("https://d3rnbxvnd0hlox.cloudfront.net/images/channels/637201122/icons/regular.png");
		lamp.save();

		Action lampAction1 = new Action("Turn on lamp");
		lampAction1.setFieldName("Lamp Color");
		lamp.getActions().add(lampAction1);
		lampAction1.setChannel(lamp);
		lampAction1.save();

		Action lampAction2 = new Action("Turn off lamp");
		lamp.getActions().add(lampAction2);
		lampAction2.setChannel(lamp);
		lampAction2.save();

		lamp.save();

		// Creating Recipes => the recipe akkas will be created
		Recipe rec = new Recipe();
		rec.setTitle("Recipe1");
		rec.setUser(user1);
		rec.setTriggerChannel(detector);
		rec.setTrigger(detectorTrigger1);
		Field f1 = new Field("toto", "tata");
		f1.save();
		rec.setTriggerField(f1);

		rec.setActionChannel(lamp);
		rec.setAction(lampAction2);
		Field f2 = new Field("lamp color", "red");
		f2.save();
		rec.setActionField(f2);
		rec.setActive(true);
		rec.setLog(new ArrayList<Log>());
		rec.save();

		for (Channel c : Ebean.find(Channel.class).findList()) {
			System.out.println(c);
		}
		// } else {
		/**
		 * In this case we already have recipes on our database But those
		 * recipes won't have their equivalent in akka So we should iterate on
		 * all the recipes that we have and create the equivalent recipeAkka for
		 * each
		 */

		// Create actor router for all the user groups that we have
		SystemController.getSystemControllerInstance().createActorRouterMap(User.getAllUserGroups());
		System.out.println("UserGroup - Router Map: "
				+ SystemController.getSystemControllerInstance().getUserGroupActorRouterMap());

		// CREATE AKKA RECIPES WITH ACTOR FOR ALL RECIPES
		for (Recipe r : Ebean.find(Recipe.class).findList()) {
			System.out.println("Creating akka recipe from recipe...");
			RecipeAkka.recipesMap.put(r.getId(), r.createRecipeAkkaFromRecipe());
		}

		/*
		 * This is how we send a message.
		 */
		Recipe recipe = Ebean.find(Recipe.class).findList().get(0);
		// We just declare something well use later on.
		CancellableRef cancellableRef = null;
		if (recipe == null) {
			Logger.info("No recipes are available.");
		} else {
			SystemController.userGroupActorRouterMap.get(recipe.getUser().getUserGroup()).tell(
					RecipeAkka.recipesMap.get(recipe.getId()).getTriggerMessage(),
					RecipeAkka.recipesMap.get(recipe.getId()).getTriggerChannelActor());

			/*
			 * This is the simple way to set up a random message issue. Activate
			 * it every random period between 10 and 15 seconds, and never
			 * stops. First we implement an object whose method will give us a
			 * new random duration each time it's invoked. Then, we pass this
			 * implementation to the scheduler.
			 */
			RandomPeriodFactory randomPeriodFactory = new RandomPeriodFactory() {
				@Override
				public Duration getPeriod() {
					return Duration.create(StdRandom.uniform(10, 15), TimeUnit.SECONDS);
				}
			};
			cancellableRef = SystemController.scheduler.periodicallyActivate(randomPeriodFactory,
					Scheduler.StopCriteria.set(StopCriteria.NEVER, null), recipe);
		}

		/*
		 * We can do anything we want upon a trigger raising. Here, at most 5
		 * seconds after the previous event we'll do something. We've chosen to
		 * activate a random recipe and to say hello to the logger. After 15
		 * times, we stop it and go to sleep.
		 * 
		 * The object eventRunnable is an anonymous implementation of the
		 * interface Runnable. We pass this object to the scheduler. Thus, each
		 * time a issue is raised, we start this thread.
		 */
		RandomPeriodFactory randomPeriodFactory = new RandomPeriodFactory() {
			@Override
			public Duration getPeriod() {
				return Duration.create(StdRandom.uniform(5), TimeUnit.SECONDS);
			}
		};
		Runnable eventRunnable = new Runnable() {
			@Override
			public void run() {
				// Get all the recipes.
				List<Recipe> recipes = Ebean.find(Recipe.class).findList();
				// Randomly pick one of them up.
				Recipe r = recipes.get(StdRandom.uniform(recipes.size()));
				// If the recipe is null, do nothing but warn.
				if (r == null) {
					Logger.info("I've been willing to activate a random recipe but unfortunately there are no available one.");
					return;
				}
				// Activate it.
				SystemController.userGroupActorRouterMap.get(r.getUser().getUserGroup()).tell(
						RecipeAkka.recipesMap.get(r.getId()).getTriggerMessage(),
						RecipeAkka.recipesMap.get(r.getId()).getTriggerChannelActor());
				// Say hello to beloved logger.
				Logger.info("Hi, I'm a random event, giving you a random number: " + StdRandom.uniform());
			}
		};
		SystemController.scheduler.addRandomIssue(Duration.Zero(), randomPeriodFactory,
				StopCriteria.set(StopCriteria.OCCURENCE, 15), eventRunnable);

		/*
		 * So we are having two ways to activate a recipe. Let's cancel the
		 * never finishing first one:
		 */
		cancellableRef.cancel();
	}

	public void onStop(Application app) {
		Logger.info("Application shutdown...");

	}
}
