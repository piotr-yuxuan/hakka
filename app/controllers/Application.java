package controllers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import logic.CausalRelation;
import logic.LogicController;
import logic.RandomScheduler.StopCriteria;
import logic.StdRandom;
import models.Action;
import models.Channel;
import models.IPI;
import models.InterfaceProxy;
import models.Recipe;
import models.Trigger;
import models.User;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;
import views.html.index;
import world.AutoCounter;
import world.AutoCounter.NewStepPassedThrough;
import world.AutoCounter.RandomCountDown;
import world.Lamp;
import world.PresenceDetector;
import akka.actor.ActorRef;

/**
 * <p>
 * At least you have to define all the mapper implied by the current recipes. If
 * you don't do so, a static message will be sent. Then, it means you also HAVE
 * TO override the default constructor for each message to define a default
 * state for each.
 * </p>
 */
public class Application extends Controller {

	private static InterfaceProxy ipi = new IPI();

	public static InterfaceProxy getInterfaceProxy() {
		return ipi;
	}

	public static String turnOnCheckbox = "";
	public static String turnOffCheckbox = "";

	private static User userLoggedIn;
	private static Recipe recipe; // used only for dynamic creation

	public static Result index() {
		Logger.info("\n\n\nHear me from Application");
		Channel channel;
		List<Trigger> triggers = new ArrayList<Trigger>();
		List<Action> actions = new ArrayList<Action>();

		channel = new Channel(triggers, actions, AutoCounter.class, "simple counter who can talk to itself");
		ActorRef countDown = LogicController.getSystemProxy().createActorOf(channel, "Simple counter");
		/*
		 * It returns always the same message because for this special kind
		 * actor there is no need for a conversion.
		 */
		UnaryOperator<Object> changeMessage = o -> o;
		/*
		 * Every time this actor issues a new trigger, saying
		 * "I've reached a new step", it's sent to any other causal relation
		 * starting from it. However, in this very case, this actor listens to
		 * itself so there is no need to define a complex message mapper.
		 */
		LogicController.getCommutator().addCausalRelation(countDown,//
				NewStepPassedThrough.class,//
				"Recette simpliste : monologue",//
				countDown,//
				changeMessage);
		/*
		 * And now we start a countdown
		 */
		Supplier<Duration> randomPeriodSupplier = () -> FiniteDuration.apply(StdRandom.uniform(5, 8), TimeUnit.SECONDS);
		LogicController.getCommutator().emitTriggerMessage(countDown, RandomCountDown.class,
				() -> new RandomCountDown(5, randomPeriodSupplier));

		channel = new Channel(triggers, actions, PresenceDetector.class, "Presence Detector");
		LogicController.getSystemProxy().createActorOf(channel);
		LogicController.getSystemProxy().createActorOf(channel, "Garden");
		LogicController.getSystemProxy().createActorOf(channel, null);
		ActorRef actor1 = LogicController.getSystemProxy().createActorOf(channel, "Hall_Detector");
		LogicController.getSystemProxy().createActorOf(channel, "Jenny's room detector");
		/* The name should raise exception but this error will be hidden. */
		ActorRef actor2 = LogicController.getSystemProxy().createActorOf(channel, "Jenny's room detector");

		channel = new Channel(triggers, actions, Lamp.class, "Basic lamp");
		/* Under these circumstances, this will be the static actor. */
		LogicController.getSystemProxy().createActorOf(channel);
		ActorRef actor3 = LogicController.getSystemProxy().createActorOf(channel);

		/**
		 * Now let's define some causal relations. Two of them are enough. Here
		 * is how to read the 4-uplets: (emitter, trigger, receiver,
		 * actionFunction).
		 * <ul>
		 * <li>(actor1, motionDetected, actor3, TurnState(true)</li>
		 * <li>(Jenny's room detector, motionDetected, actor3, TurnState(false)</li>
		 * </ul>
		 */
		UnaryOperator<Object> operatorOn = o -> {
			PresenceDetector.MotionDetected m = (PresenceDetector.MotionDetected) o;

			String colour;
			int random = (int) StdRandom.uniform(0, 6);
			switch (random) {
			case 0:
				colour = "blue";
				break;
			case 1:
				colour = "yellow";
				break;
			case 2:
				colour = "turquoise";
				break;
			case 3:
				colour = "brown";
				break;
			case 4:
				colour = "orange";
				break;
			case 5:
				colour = "purple";
				break;
			default:
				colour = null; // don't change
			}
			/**
			 * Notice it's not null then the actor will change this state
			 * accordingly.
			 * 
			 * Also notice we use the input message o to define the output
			 * message.
			 * 
			 */
			Double intensity = m.getQuantitéDeMouvement();
			Boolean state = true; // Notice it's TRUE
			return new Lamp.ChangeState(state, colour, intensity, null);
		};
		LogicController.getCommutator().addCausalRelation(//
				actor1,//
				PresenceDetector.MotionDetected.class,//
				"Première recette",//
				actor3,//
				operatorOn,//
				Arrays.asList("user", "garden"));

		/*
		 * Another causalRelation
		 */
		UnaryOperator<Object> operatorOff = o -> {
			/*
			 * Here we don't use any modality of input message to define the
			 * output.
			 */
			Boolean state = false; // Notice it's FALSE
			return new Lamp.ChangeState(state, null, null, null);
		};
		LogicController.getCommutator().addCausalRelation(
				LogicController.getSystemProxy().getActorByName("Jenny's room detector"),//
				PresenceDetector.MotionDetected.class,//
				"Deuxième recette",//
				actor3,//
				operatorOff,//
				Arrays.asList("user", "home"));

		/*
		 * Just an example how to trigger a message manually. This is automated
		 * by RandomScheduler.
		 */
		LogicController.getCommutator().emitTriggerMessage(actor1, PresenceDetector.MotionDetected.class,
				() -> new PresenceDetector.MotionDetected(0.5));

		Supplier<Object> supplier = () -> new PresenceDetector.MotionDetected(StdRandom.uniform());
		/*
		 * We can do anything we want upon a trigger raised.
		 */
		LogicController.getScheduler().addRandomIssue(Duration.Zero(),
				() -> Duration.create(StdRandom.uniform(5, 15), TimeUnit.SECONDS),
				StopCriteria.set(StopCriteria.NEVER, null),//
				() -> {
					// Raise trigger for first causal relation
				LogicController.getCommutator().emitTriggerMessage(//
						actor1,//
						PresenceDetector.MotionDetected.class,//
						supplier);
				// // Raise trigger for second causal relation
				// Application.getCommutator().emitTriggerMessage(//
				// Application.getSystemProxy().getActorByName("Jenny's room detector"),//
				// AllMessages.PresenceDetector.MotionDetected.class,//
				// supplier);
			});
		/**
		 * <p>
		 * As seen previously we can do anything we want, even to raise two
		 * triggers. But it wouldn't be in the same time : first because you
		 * write your code sequentially, then because it's very likely to be
		 * ordered either bu EventBusImpl either by the very akka system.
		 * </p>
		 * <p>
		 * Please note we hereby define
		 * </p>
		 */
		LogicController.getScheduler().addRandomIssue(Duration.Zero(),
				() -> Duration.create(StdRandom.uniform(5, 15), TimeUnit.SECONDS),
				StopCriteria.set(StopCriteria.OCCURENCE, 10),//
				() -> {
					// Raise trigger for second causal relation
				LogicController.getCommutator().emitTriggerMessage(//
						LogicController.getSystemProxy().getActorByName("Jenny's room detector"),//
						PresenceDetector.MotionDetected.class,//
						supplier);
			});

		/**
		 * Finally we filter the causal relations to only display those related
		 * to home and we display the result in a JFrame.
		 * 
		 * Data in files are handy then we store a copy in.
		 */
		Predicate<CausalRelation> predicate = w -> w.getLabel().stream().anyMatch(x -> x.equals("user"));
		String filepath = "../export.txt";
		File file = LogicController.getCommutator().exportCausalGraph(predicate, new File(filepath));

		String[] args = { filepath };
		// try {
		// (new Thread(() -> visual.ExportJFrame.main(args))).start();
		// } catch (Exception e) {
		// Logger.info("grave: no gephi");
		// }

		try {
			Runtime.getRuntime().exec("java -jar lib/Visual.jar " + filepath);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return ok(index.render());
	}

	public static Result loginForm() {
		return ok();

		// DynamicForm requestData = Form.form().bindFromRequest();
		//
		// String username = requestData.get("username");
		// String password = requestData.get("password");
		//
		// User user = User.authenticate(username, password);
		// if (user == null) {
		// return ok(index.render());
		// }
		//
		// else {
		// userLoggedIn = user;
		// if (userLoggedIn.getRole() == "administrator") {
		//
		// List<Channel> channelsList = Channel.find.all();
		// HashMap<Channel, List<Trigger>> triggersDic = new HashMap<Channel,
		// List<Trigger>>();
		// for (int i = 0; i < channelsList.size(); i++) {
		// triggersDic.put(channelsList.get(i),
		// channelsList.get(i).getTriggers());
		// }
		//
		// return ok(administratorView.render(channelsList, triggersDic));
		// } else {
		// Recipe r = new Recipe(null, null, null, null, null);
		// r.setName("Default recipe");
		// r.setActive(true);
		// r.getLog().add("Recipe created.");
		// r.getLog().add("Recipe activated on creation.");
		// r.setUser(userLoggedIn);
		// r.save();
		// userLoggedIn.save();
		// /*
		// * Recipe recipe2 = new Recipe();
		// * recipe2.setTitle("Second recipe"); recipe2.setId(2);
		// * recipe2.setUser(userLoggedIn); recipe2.setActive(true);
		// * recipe2.getLog().add("Recipe created.");
		// * recipe2.getLog().add("Recipe activated on creation.");
		// * recipe2.save();
		// */
		//
		// return ok(chooseView.render(userLoggedIn));
		// }
		//
		// }

	}

	public static Result administratorView() {
		return ok();
		// List<Channel> channelsList = Channel.find.all();
		// HashMap<Channel, List<Trigger>> triggersDic = new HashMap<Channel,
		// List<Trigger>>();
		// for (int i = 0; i < channelsList.size(); i++) {
		// triggersDic.put(channelsList.get(i),
		// channelsList.get(i).getTriggers());
		// }
		// return ok(administratorView.render(channelsList, triggersDic));
	}

	public static Result chooseView() {
		return ok();

		// DynamicForm requestData = Form.form().bindFromRequest();
		//
		// if (requestData.get("viewRecipesButton") != null) {
		// return ok(viewRecipes.render(userLoggedIn));
		// } else {
		// List<Channel> channelsList = Channel.find.all();
		// recipe = new Recipe(null, null, null, null, null);
		// return ok(chooseTriggerChannel.render(channelsList));
		// }

	}

	public static Result viewAdministratorLog() {
		return ok();
		// return ok(administratorLog.render());
	}

	public static Result submitForm() throws IOException {
		return ok();
		// Boolean lampOn = false;
		//
		// DynamicForm requestData = Form.form().bindFromRequest();
		//
		// turnOnCheckbox = requestData.get("turnOnCheckbox");
		// turnOffCheckbox = requestData.get("turnOffCheckbox");
		//
		// System.out.println("turn off checkbox: " + turnOffCheckbox);
		//
		// if (requestData.get("enterRoomButton") != null) {
		// // // Tell the detector that a human entered the room
		// // if (turnOnCheckbox != null) {
		// // AllActors.detector.tell(new AllMessages.EnterRoom(true),
		// // AllActors.human);
		// // } else {
		// // AllActors.detector.tell(new AllMessages.EnterRoom(false),
		// // AllActors.human);
		// // }
		//
		// try {
		// TimeUnit.MILLISECONDS.sleep(10);
		// } catch (InterruptedException e) {
		//
		// e.printStackTrace();
		// }
		//
		// // if (AllActors.Lamp.state.equals("ON"))
		// // lampOn = true;
		// // else
		// // lampOn = false;
		// System.out.println("Enter room button - LampOn is TRUE");
		//
		// } else if (requestData.get("exitRoomButton") != null) {
		// // Tell the detector that a human exited the room
		// // if (turnOffCheckbox != null) {
		// // AllActors.detector.tell(new AllMessages.ExitRoom(true),
		// // AllActors.human);
		// // } else {
		// // AllActors.detector.tell(new AllMessages.ExitRoom(false),
		// // AllActors.human);
		// // }
		//
		// try {
		// TimeUnit.MILLISECONDS.sleep(10);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// // if (AllActors.Lamp.state.equals("OFF"))
		// // lampOn = false;
		// // else
		// // lampOn = true;
		//
		// System.out.println("Exit room button - LampOn is FALSE");
		// }
		//
		// List<Channel> channelsList = Channel.find.all();
		// return ok(chooseTriggerChannel.render(channelsList));
	}

	public static Result chooseTrigger(Long channelId) {
		return ok();
		//
		// Channel channel = Channel.find.byId(channelId);
		//
		// recipe.setTriggerChannel(channel);
		//
		// return ok(chooseTrigger.render(channel));
	}

	public static Result completeTriggerFields(Long triggerId) {
		return ok();
		//
		// Trigger trigger = Trigger.find.byId(triggerId);
		//
		// DynamicForm requestData = Form.form().bindFromRequest();
		//
		// HashMap<Modality, String> triggerFields = new HashMap<Modality,
		// String>();
		// for (Modality f : trigger.getFields()) {
		// triggerFields.put(f, requestData.get(f.getName()));
		// }
		//
		// /**
		// * TODO should not be commented but understood.
		// */
		// // recipe.setTriggersMap(triggerFields);
		//
		// return ok(completeTriggerFields.render(trigger));
	}

	public static Result chooseActionChannel() {
		return ok();
		// List<Channel> channelsList = Channel.find.all();
		// return ok(chooseActionChannel.render(channelsList));
	}

	public static Result chooseAction(Long channelId) {
		return ok();
		// Channel channel = Channel.find.byId(channelId);
		//
		// recipe.setActionChannel(channel);
		//
		// return ok(chooseAction.render(channel));
	}

	public static Result completeActionFields(Long actionId) {
		return ok();
		//
		// Action action = Action.find.byId(actionId);
		//
		// DynamicForm requestData = Form.form().bindFromRequest();
		//
		// HashMap<Modality, String> actionFields = new HashMap<Modality,
		// String>();
		// for (Modality f : action.getFields()) {
		// actionFields.put(f, requestData.get(f.getName()));
		// }
		//
		// /**
		// * TODO should not be commented but corrected
		// */
		// // recipe.setActionsMap(actionFields);
		//
		// return ok(completeActionFields.render(action));
	}

	public static Result createRecipe() {
		return ok();
		// return ok(createRecipe.render(recipe));
	}

	public static Result viewRecipesAfterCreate() {
		return ok();
		// DynamicForm requestData = Form.form().bindFromRequest();
		//
		// recipe.setName(requestData.get("recipeTitle"));
		//
		// recipe.setActive(true);
		// recipe.getLog().add("Recipe created.");
		// recipe.getLog().add("Recipe activated on creation.");
		//
		// // List<Recipe> list = userLoggedIn.getRecipes();
		// // list.add(recipe);
		// // System.out.println("RECIPEEEEES:" + list.size());
		// // userLoggedIn.setRecipes(list)
		// // (userLoggedIn.getRecipes().add(recipe);
		// recipe.setUser(userLoggedIn);
		// recipe.save();
		// userLoggedIn.save();
		// return ok(viewRecipes.render(userLoggedIn));
	}

	public static Result viewRecipes() {
		return ok();
		// if (userLoggedIn != null)
		// return ok(viewRecipes.render(userLoggedIn));
		// else
		// return ok(index.render());
	}

	public static Result activateTrigger(Long triggerId) {
		return ok();

		// Ebean.find(Recipe.class)
		// .findList()
		// .parallelStream()
		// /* Now we have a stream, we filter on the id of TriggerChannel */
		// .filter(recipe -> recipe.getTriggerChannel().getId() == triggerId)
		// /* And now we send a message to each channel */
		// .forEach(
		// recipe -> {
		// /* This actor is the object which will be told */
		// try {
		// LogicController
		// .getSystemProxy()
		// .getOrCreateStaticActorFor(recipe.getActionChannel())
		// .tell(
		// /*
		// * Message to be told: we send the
		// * message of the first trigger found
		// */
		// recipe.getTriggerChannel().getTriggers().stream()
		// .filter(trigger -> trigger.getId() == triggerId).reduce((x, y) -> x)
		// .getClass().newInstance(),
		// /* Sender */
		// LogicController.getSystemProxy().getOrCreateStaticActorFor(
		// recipe.getTriggerChannel()));
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// });
		// return ok();
	}

	public static Result viewRecipeLog() {
		return ok();
		// /*
		// * //search the recipe and update it: DynamicForm requestData =
		// * Form.form().bindFromRequest(); String value =
		// * requestData.get("viewRecipesLog"); long recipeId; Recipe recipe;
		// * if(value!=null){ recipeId = Long.parseLong(value); recipe =
		// * Recipe.find.byId(recipeId); }else{ recipe = null; }
		// *
		// * if(userLoggedIn!=null && recipe != null) return
		// * ok(viewRecipeLog.render(userLoggedIn, recipe)); else return
		// * ok(index.render());
		// */
		// if (userLoggedIn != null) {
		// Recipe r = userLoggedIn.getRecipes().get(0);
		// return ok(viewRecipeLog.render(userLoggedIn, r));
		// } else
		// return ok(index.render());
	}

	public static Result activateRecipe() {
		return ok();
		// DynamicForm requestData = Form.form().bindFromRequest();
		// // long recipeId = Long.parseLong( requestData.get("RecipeOff") );
		// // search the recipe and update it:
		// Recipe r = userLoggedIn.getRecipes().get(0);
		// if (requestData.get("RecipeOff") != null && r.isActive()) {
		// r.setActive(false);
		// r.getLog().add("Recipe turned off.");
		// r.save();
		// } else if (requestData.get("RecipeOn") != null && r.isActive() ==
		// false) {
		// r.setActive(true);
		// r.getLog().add("Recipe turned on.");
		// r.save();
		// }
		// userLoggedIn.save();
		// return ok(viewRecipes.render(userLoggedIn));
	}

	public static Result userLogOut() {
		return ok();
		// DynamicForm requestData = Form.form().bindFromRequest();
		// if (requestData.get("LogOutButton") != null) {
		// userLoggedIn = null;
		// recipe = null;
		// return index();
		// }
		// if (requestData.get("HomeButton") != null) {
		//
		// if (userLoggedIn.getRole() == "administrator")
		// // return ok(administratorView.render(channelsList,
		// // triggersDic));
		// return ok();
		// else
		// return ok(chooseView.render(userLoggedIn));
		// } else
		// return index();
	}

}
