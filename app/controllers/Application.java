package controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.avaje.ebean.Ebean;

import messages.AllMessages;
import models.Action;
import models.AdminLog;
import models.Channel;
import models.Field;
import models.Log;
import models.Recipe;
import models.RecipeAkka;
import models.Trigger;
import models.User;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;
import actors.AllActors;
import views.html.*;

public class Application extends Controller {

	private static User userLoggedIn = null;

	public static User getUserLoggedIn() {
		return userLoggedIn;
	}

	private static Recipe recipe;

	public static Result index() {
		return ok(index.render());
	}

	public static Result loginForm() {
		// recipe=null;
		if (userLoggedIn != null) {
			if (userLoggedIn.getRole().equals("administrator"))
				return administratorView();
			else
				return ok(chooseView.render(userLoggedIn));
		}
		

		DynamicForm requestData = Form.form().bindFromRequest();

		String username = requestData.get("username");
		String password = requestData.get("password");

		User user = User.authenticate(username, password);
		if (user == null) {
			return ok(index.render());
		}

		else {
			userLoggedIn = user;
			System.out.println("MY USER:" + user);
			if (userLoggedIn.getRole().equals("administrator")) {

				List<Channel> channelsList = Channel.getAllChannels();
//				HashMap<Channel, List<Trigger>> triggersDic = new HashMap<Channel, List<Trigger>>();
//				for (int i = 0; i < channelsList.size(); i++) {
//					triggersDic.put(channelsList.get(i), channelsList.get(i)
//							.getTriggers());
//				}
				System.out.println("Im here");
				return ok(administratorView.render(channelsList));
			} else {
				System.out.println("Im not here");
				return ok(chooseView.render(userLoggedIn));

			}

		}

	}

	public static Result administratorView() {
		List<Channel> channelsList = Channel.getAllChannels();
		return ok(administratorView.render(channelsList));
	}

	public static Result chooseView() {

		DynamicForm requestData = Form.form().bindFromRequest();

		if (requestData.get("viewRecipesButton") != null) {
			System.out.println("VIEW RECIPES");
			return ok(viewRecipes.render(userLoggedIn));
		} else {
			recipe = new Recipe();
			recipe.setUser(userLoggedIn);
			List<Channel> channelsList = Channel.getAllChannels();
			return ok(chooseTriggerChannel.render(channelsList));
		}

	}

	public static Result viewAdministratorLog() {
		List<AdminLog> logs = AdminLog.getAllAdminLogs();
		return ok(administratorLog.render(logs));
	}

	public static Result submitForm() throws IOException {
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
		// // Tell the detector that a human entered the room
		// if (turnOnCheckbox != null) {
		// AllActors.detectorActor.tell(new AllMessages.EnterRoom(true),
		// AllActors.humanActor);
		// } else {
		// AllActors.detectorActor.tell(new AllMessages.EnterRoom(false),
		// AllActors.humanActor);
		// }
		//
		// try {
		// TimeUnit.MILLISECONDS.sleep(10);
		// } catch (InterruptedException e) {
		//
		// e.printStackTrace();
		// }
		//
		// if (AllActors.Lamp.state.equals("ON"))
		// lampOn = true;
		// else
		// lampOn = false;
		// System.out.println("Enter room button - LampOn is TRUE");
		//
		// } else if (requestData.get("exitRoomButton") != null) {
		// // Tell the detector that a human exited the room
		// if (turnOffCheckbox != null) {
		// AllActors.detectorActor.tell(new AllMessages.ExitRoom(true),
		// AllActors.humanActor);
		// } else {
		// AllActors.detectorActor.tell(new AllMessages.ExitRoom(false),
		// AllActors.humanActor);
		//
		// }
		// try {
		// TimeUnit.MILLISECONDS.sleep(10);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// if (AllActors.Lamp.state.equals("OFF"))
		// lampOn = false;
		// else
		// lampOn = true;
		//
		// System.out.println("Exit room button - LampOn is FALSE");
		// }
		//
		List<Channel> channelsList = Channel.getAllChannels();
		return ok(chooseTriggerChannel.render(channelsList));
	}

	public static Result chooseTrigger(Long channelId) {
		Channel channel = Channel.find.byId(channelId);
		recipe.setTriggerChannel(channel);
		channel.save();
		return ok(chooseTrigger.render(channel));
	}

	public static Result completeTriggerFields(Long triggerId) {

		Trigger trigger = Trigger.find.byId(triggerId);
		recipe.setTrigger(trigger);
		DynamicForm requestData = Form.form().bindFromRequest();
		System.out.println("My trigger: " + trigger.getName());

		// HashMap<Field, String> triggerFields = new HashMap<Field, String>();
		// for (Field f : trigger.getFields()) {
		// triggerFields.put(f, requestData.get(f.getName()));
		// }

		String fieldName = trigger.getFieldName();
		if (fieldName != null && !fieldName.equals("")) {
			String triggerFieldValue = requestData.get(fieldName);
			Field f = new Field(fieldName, triggerFieldValue);
			recipe.setTriggerField(f);
		}

		return ok(completeTriggerFields.render(trigger));
	}

	public static Result chooseActionChannel() {
		List<Channel> channelsList = Channel.getAllChannels();
		return ok(chooseActionChannel.render(channelsList));
	}

	public static Result chooseAction(Long channelId) {
		Channel channel = Channel.find.byId(channelId);
		recipe.setActionChannel(channel);
		channel.save();

		return ok(chooseAction.render(channel));
	}

	public static Result completeActionFields(Long actionId) {

		Action action = Action.find.byId(actionId);

		DynamicForm requestData = Form.form().bindFromRequest();

		recipe.setAction(action);
		// HashMap<Field, String> actionFields = new HashMap<Field, String>();
		// for (Field f : action.getFields()) {
		// actionFields.put(f, requestData.get(f.getName()));
		// }

		String fieldName = action.getFieldName();
		if (fieldName != null && !fieldName.equals("")) {
			String actionFieldValue = requestData.get(fieldName);
			Field f = new Field(fieldName, actionFieldValue);
			recipe.setActionField(f);
		}

		return ok(completeActionFields.render(action));
	}

	public static Result createRecipe() {
		return ok(createRecipe.render(recipe));
	}

	public static Result viewRecipesAfterCreate() {
		DynamicForm requestData = Form.form().bindFromRequest();

		System.out.println("my recipe title: " + requestData.get("recipeTitle"));
		recipe.setTitle(requestData.get("recipeTitle"));
		recipe.setActive(true);
		
		Log l1 = new Log("Recipe creation", new Date());
		l1.setRecipe(recipe);
		//l1.save();
		//Log l2 = new Log("Recipe activated on creation");
		//l2.setRecipe(recipe);
		//l2.save();
		recipe.getLog().add(l1);
		//recipe.getLog().add(l2);
		
		recipe.save();
		userLoggedIn.getRecipes().add(recipe);
		//recipe.setUser(userLoggedIn);
		
		System.out.println("User's list size: " + userLoggedIn.getRecipes().size());
		RecipeAkka.recipesMap.put(recipe.getId(), recipe.getRecipeAkka());

		// System.out.println("Recipe created: " + recipe);

		// Channel trigger = recipe.getTriggerChannel();
		// List<Recipe> recipesTrigger = trigger.getRecipes();
		// recipesTrigger.add(recipe);
		// trigger.save();
		//
		// Channel action = recipe.getActionChannel();
		// List<Recipe> recipesAction = action.getRecipes();
		// recipesAction.add(recipe);
		// action.save();
		//
		// System.out.println("Showing trigger channel: " +
		// recipe.getTriggerChannel());
		// Channel c = recipe.getTriggerChannel();
		// for (Recipe r : c.getRecipes()) {
		// System.out.println("recipe title: " + r.getTitle());
		// }
		//
		//
		// List<Recipe> list = userLoggedIn.getRecipes();
		// // for (Recipe r : list) {
		// // System.out.println("Showing trigger channel: " +
		// recipe.getTriggerChannel().getName());
		// // }
		// System.out.println("Saving recipe: " + list);
		// // list.add(recipe);
		// //
		// // userLoggedIn.setRecipes(list);
		// userLoggedIn.save();

		return ok(viewRecipes.render(userLoggedIn));
	}

	public static Result viewRecipes() {
		// NOT BEING USED
		return ok(viewRecipes.render(userLoggedIn));
	}

	public static Result activateTrigger(Long triggerId) {

		List<Recipe> recipesList = Ebean.find(Recipe.class).findList();

		for (int i = 0; i < recipesList.size(); i++) {
			if (recipesList.get(i).getTriggerChannel().getId() == triggerId) {
				Trigger trigger = null;
				for (int j = 0; j < recipesList.get(i).getTriggerChannel()
						.getTriggers().size(); j++) {
					if (recipesList.get(i).getTriggerChannel().getTriggers()
							.get(j).getId() == triggerId) {
						trigger = recipesList.get(i).getTriggerChannel()
								.getTriggers().get(j);
					}
				}
			}
		}

		return ok();
	}

	public static Result chooseActivationType() {

		DynamicForm requestData = Form.form().bindFromRequest();

		List<Recipe> recipesList = Ebean.find(Recipe.class).findList();

		Long triggerId = Long.parseLong(requestData
				.get("trigger_chosen_to_activate_id"));

		if (requestData.get("activateTriggerManuallyButton") != null) {
			return ok(administratorActivateManually.render(triggerId));
		} else if (requestData.get("activateTriggerPeriodicallyButton") != null) {
			return ok(administratorActivatePeriodically.render(triggerId));
		} else
			return ok(administratorActivateRandomly.render(triggerId));
	}

	public static Result viewRecipeLog() {
		DynamicForm requestData = Form.form().bindFromRequest();
		if (userLoggedIn != null && requestData.get("viewRecipesLog") != null) {
			long recipeId = Long.parseLong(requestData.get("viewRecipesLog"));
			Recipe r = userLoggedIn.getRecipesById(recipeId);
			return ok(viewRecipeLog.render(userLoggedIn, r));
		} else
			return ok(index.render());
	}
	
	public static Result deleteRecipe() {
		DynamicForm requestData = Form.form().bindFromRequest();
		if (userLoggedIn != null && requestData.get("deleteRecipe") != null) {
			long recipeId = Long.parseLong(requestData.get("deleteRecipe"));
			System.out.println("Delete recipe: " + recipeId);
			Recipe r = userLoggedIn.getRecipesById(recipeId);
			userLoggedIn.getRecipes().remove(r);
			r.delete();
			RecipeAkka.recipesMap.remove(r.getId()); 
		}
		
		return ok(viewRecipes.render(userLoggedIn));
	}

	public static Result activateRecipe() {
		DynamicForm requestData = Form.form().bindFromRequest();
		if (userLoggedIn != null){
			if (requestData.get("RecipeOff") != null) {	
				long recipeId = Long.parseLong( requestData.get("RecipeOff") );
				Recipe r = userLoggedIn.getRecipesById(recipeId);
				if(r.getActive()){
					r.setActive(false);
					r.getLog().add(new Log("Recipe turned off.", new Date()));
					r.save();
				}
			}else if (requestData.get("RecipeOn") != null) {	
				long recipeId = Long.parseLong( requestData.get("RecipeOn") );
				Recipe r = userLoggedIn.getRecipesById(recipeId);
				if(r.getActive()==false){
					r.setActive(true);
					r.getLog().add(new Log("Recipe turned on.", new Date()));
					r.save();
				}
			}
			userLoggedIn.save();
		}
		return ok(viewRecipes.render(userLoggedIn));
	}

	public static Result userLogOut() {
		DynamicForm requestData = Form.form().bindFromRequest();
//		if (requestData.get("LogOutButton") != null) {
			userLoggedIn = null;
			recipe = null;
			
//		}
//		if (requestData.get("HomeButton") != null) {
//			recipe = null;
//			if (userLoggedIn.getRole() == "administrator")
//				return administratorView();
//			else
//				return ok(chooseView.render(userLoggedIn));
//		}
		if (requestData.get("AdminLog") != null) {
			return viewAdministratorLog();
		} 
		return index();
	}

}
