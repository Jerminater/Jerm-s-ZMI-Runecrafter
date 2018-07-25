package scripts.jZMI.tasks;

import java.awt.event.KeyEvent;
import java.util.Arrays;

import org.tribot.api.Clicking;
import org.tribot.api.General;
import org.tribot.api.input.Keyboard;
import org.tribot.api.input.Mouse;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Magic;
import org.tribot.api2007.Objects;
import org.tribot.api2007.Player;
import org.tribot.api2007.Walking;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;

import scripts.api.framework.Priority;
import scripts.api.framework.Task;
import scripts.api.timing07.Timing07;
import scripts.jZMI.data.Cons;
import scripts.jZMI.data.Vars;
import scripts.jZMI.utils.Antiban;
import scripts.jZMI.utils.JZMIUtils;
import scripts.api.webwalker_logic.WebWalker;
import scripts.api.wrappers.InventWrapper;

public class Craft implements Task {

	@Override
	public String toString() {
	        return "Heading to the Alter...";
	}
	
	@Override
	public Priority priority() {
		return Priority.HIGH;
	}

	@Override
	public boolean validate() {
		return (((JZMIUtils.hasFullPouches() && Inventory.isFull()) || 
				Cons.ALTAR.contains(Player.getPosition())) &&
				Cons.DUNGEON.contains(Player.getPosition())) || Vars.get().doneBanking;
	}
	
	private State location;
	private RSObject[] zmiAltar;
	private RSTile[] path = {
		    new RSTile(3013, 5626, 0),
		    new RSTile(3015, 5616, 0),
		    new RSTile(3015, 5616, 0),
		    new RSTile(3015, 5609, 0),
		    new RSTile(3015, 5599, 0),
		    new RSTile(3017, 5589, 0),
		    new RSTile(3019, 5581, 0),
		    new RSTile(3027, 5578, 0),
		    new RSTile(3035, 5580, 0),
		    new RSTile(3045, 5579, 0),
		    new RSTile(3045, 5579, 0),
		    new RSTile(3055, 5579, 0),
		    new RSTile(3058, 5579, 0)};
	private boolean quickCraft;
	
	@Override
	public void execute() {
		Vars.get().doneBanking = false;
		location = getLocation();
		switch (location) {
		case BANK:
			// TODO: antiban
			
			// Clicks away from the bank using the minimap after we are done banking. 
			Walking.clickTileMM(new RSArea(new RSTile(3013, 5612, 0), new RSTile(3017, 5615, 0)).getRandomTile(), 1);

			// Verifies all settings are in order
			JZMIUtils.performChecks();
			
			// Heads to Altar using pathfinding
			goToAltar();
			break;
			
		case INTERMEDIATE:
			
			// Heads to Altar using pathfinding
			goToAltar();
			break;
			
		case ALTAR:
			
			// Heads to Altar using pathfinding
			startRunecrafting();
			break;
			
		default:
			
			// Failsafe. Might need to change this if WebWalker is not supported in the dungeon.
	        General.println("Something went wrong, resetting."); // debug
	        WebWalker.walkToBank();
	        break;
		}
	}
	
	private void startRunecrafting() {
		
		// Should find the altar based on the conditions to enter this method, but if it doesn't it will try going back to the altar.
		zmiAltar = Objects.find(20, "Runecrafting altar");
		RSObject altar;
		if (zmiAltar.length > 0)
			altar = zmiAltar[0];
		else {
			goToAltar();
			return;
		}
		
		// Just in case the altar is found and not on the screen. It should always be on the screen based on conditions.
		if (!altar.isOnScreen()) {
			General.println("Not seeing the Altar, contact Jerm if error persists."); //debug
			goToAltar();
			return;
		}
		
		// Loops through the process of crafting essense. Very dynamic in figuring out the best way to withdraw ess and craft.
		while (JZMIUtils.getPouches(0).length > 0 || InventWrapper.inventContains(Cons.PURE_ESSENCE) || Cons.ALTAR.contains(Player.getPosition())) {
			General.sleep(20);
			
			/* 
			 * if: We have essence or quick crafting after withdrawing essense
			 * 		-> Click altar
			 * 		-> hover next action we should perform
			 * 		-> wait for some off ms till we are ready for next action.
			 * else if: Inventory is not full, and we have some none empty pouches
			 * 		-> get essence from pouches
			 * 		-> activate quick crafting
			 * else if: inventory does not contain essense and we have all empty pouches
			 * 		-> teleport to Ourania
			 */
			if (InventWrapper.inventContains(Cons.PURE_ESSENCE) || quickCraft) {
				Timing07.waitCondition(() -> {return Clicking.click("Craft-rune", altar.getAllTiles()[General.random(0, 3)]);}, 2000L);
				JZMIUtils.hoverNextActionAndWait();
				Mouse.setSpeed(100);
				quickCraft = false;
			} else if (!Inventory.isFull() && JZMIUtils.getPouches(0).length > 0) {
				getEss();
				quickCraft = true;
			} else if (!InventWrapper.inventContains(Cons.PURE_ESSENCE) && JZMIUtils.getPouches(0).length == 0)
				teleportOurania();
		}
	}

	// Will teleport to Ourania and wait till we arrive. Also includes failsafe for if button got stuck emptying pouches.
	private void teleportOurania() {
		
		// Waiting till we succesfully click the spell
		Timing07.waitCondition(() -> {
			Magic.selectSpell("Ourania Teleport");
			General.sleep(200);
			return (Player.getAnimation() != -1);
			}, 5000L);
		
		// Waiting till we teleport
		Timing07.waitCondition(() -> {
			return (Cons.TELE.contains(Player.getPosition()));
			}, 5000L);
		
		// in case it got stuck in thread pressing button
		Keyboard.sendRelease(KeyEvent.CHAR_UNDEFINED, 16);
		
		// For Paint
		Vars.get().tripsMade++;
	}

	private void getEss() {
		
		// Based on some mouse tracking speeds. Speeds greatly increased when withdrawing essense.
		Mouse.setSpeed(General.randomSD(150, 250, 200, 25));
		
		// Luckily pouches in alphabetical order are also in size order.
		RSItem[] pouches = JZMIUtils.getPouches(1);
		Arrays.sort(pouches, (i1,i2) -> i2.getDefinition().getName().compareTo(i1.getDefinition().getName()));
		
		// holds shift
		Keyboard.sendPress(KeyEvent.CHAR_UNDEFINED, 16);
		
		// Clicks pouches from smallest to largest. Will add support for small -> Giant, med ->large at a later date
		if (pouches.length > 0)
			pouches[0].click("Empty");
		if (pouches.length > 1)
			pouches[1].click("Empty");
		
		// Makes it less bot-like to stop pressing while mouse moves to altar
		new Thread(() -> {
	    	try {
	    		Thread.sleep(General.randomSD(300, 200));
	    	} catch (InterruptedException e) {
	    		e.printStackTrace();
	    	}
	    	Keyboard.sendRelease(KeyEvent.CHAR_UNDEFINED, 16);
	           }).start();
		
		// Failsafe
		Keyboard.sendRelease(KeyEvent.CHAR_UNDEFINED, 16);
	}

	// Would like to rewrite this to a custom pathwalking algorithm that looks more human like.
	private void goToAltar() {
		
		// Randomizing all but the last tile in the path. Might randomize last tile by 1 tile or so, but I always click the same one.
		RSTile[] randPath = Walking.randomizePath(path, General.random(1, 3), General.random(1, 3));
		randPath[randPath.length-1] = new RSTile(3058, 5579, 0);
		
		// Walking the path until we click to the altar.
		Walking.walkPath(randPath, new Condition() {
			@Override
			public boolean active() {
				return Vars.get().hoverAlter;
			}}, 500);
		
		// Initialize
		Vars.get().hoverAlter = false;
		
		// TODO: add functionality to this bit like: closes inventory sometimes when on resizeable.
		// Hover screen where altar should appear. If antiban says so.
		Antiban.get().setHoverAndMenuOpenBooleans();
		if (Antiban.get().isHovering())
			Player.getPosition().translate(General.random(6, 8), 0).hover();

		// Waits till altar is on screen. If it fails to show up in 5 seconds of walking, it will attempt to craft anyways. 
		// If that fails, will return to this method and go to altar
		Timing07.waitCondition(() -> {
			RSObject[] altar = Objects.find(20, "Runecrafting altar");
			return (altar != null) ? altar[0].isOnScreen() : false;
			}, 5000L);
		
		// Start crafting
		startRunecrafting();
	}	
	
	// Gets the current location of the player within the dungeon
	private State getLocation() {
		if (Cons.BANK.contains(Player.getPosition()))
			return State.BANK;
		else if (Cons.ALTAR.contains(Player.getPosition()))
			return State.ALTAR;
		else
			return State.INTERMEDIATE;
	}			
}
