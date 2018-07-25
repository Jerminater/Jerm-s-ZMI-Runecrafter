package scripts.jZMI.tasks;


import java.util.Arrays;

import org.tribot.api.Clicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Keyboard;
import org.tribot.api.input.Mouse;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Combat;
import org.tribot.api2007.Game;
import org.tribot.api2007.GameTab.TABS;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Objects;
import org.tribot.api2007.Player;
import org.tribot.api2007.Skills.SKILLS;
import org.tribot.api2007.Walking;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;

import scripts.api.framework.Priority;
import scripts.api.framework.Task;
import scripts.api.timing07.Timing07;
import scripts.api.utilities.JBanking;
import scripts.api.utilities.JConditions;
import scripts.api.utilities.Utilities;
import scripts.api.webwalker_logic.WebWalker;
import scripts.api.wrappers.InventWrapper;
import scripts.jZMI.data.Cons;
import scripts.jZMI.data.Vars;
import scripts.jZMI.utils.Antiban;
import scripts.jZMI.utils.JZMIUtils;

public class GoToBank implements Task {

	@Override
	public String toString() {
	        return "Heading to the Bank...";
	}
	
	@Override
	public Priority priority() {
		return Priority.NONE;
	}

	@Override
	public boolean validate() {
		return true;
	}
	
	private State location;
	
	// TODO: Walking preferences for antiban
	@Override
	public void execute() {
		Vars.get().doneBanking = false;
		Vars.simulateGUI();
		location = getLocation();
		switch (location) {
		
			case BANK:
				
				// If the user selected to set up the bank for them, then the script will set up their bank here.
				if (Vars.get().firstBank) {
					JZMIUtils.setupBankAndInvent();
					Vars.get().firstBank = false;
					Vars.get().status = "Banking";
				}
				
				// getInv() will perform all banking actions neccesary.
				if (!Inventory.isFull() || !JZMIUtils.hasFullPouches()) 
					getInv();
				break;
				
			case TELE:
				
				// Clicks path to ladder and fills pouch with runes crafted. Failure to click will use WebWalking to get to Ladder.
				if (Walking.clickTileMM(Cons.PATH_TO_LADDER_CLICK.getRandomTile(), 1)) {
					
					// Will hover the map if Antiban says we should
					Antiban.get().setHoverAndMenuOpenBooleans();
					if (Antiban.get().isHovering())
						JZMIUtils.fillPouchAndMoveItems();
					
					// Will hover the map if Antiban says we should
					Antiban.get().setHoverAndMenuOpenBooleans();
					if (Antiban.get().isHovering())
						JZMIUtils.hoverMap();
					
				} else
					WebWalker.walkTo(getLadderWalkingTile());
				
				// Waits for tile to be clickable by alter and clicks when it is. Reaction is governed by JZMIUtils.hovermap(), hovers only when antiban says so.
				Antiban.get().setHoverAndMenuOpenBooleans();
				Timing07.waitCondition(() -> {
					if (Antiban.get().isHovering())
						JZMIUtils.hoverMap();
					return Walking.clickTileMM(shouldWePray() ? getAltarWalkingTile() : getLadderWalkingTile(),1);
				},6000L);
				
				// Will hover the map if Antiban says we should
				Antiban.get().setHoverAndMenuOpenBooleans();
				if (Antiban.get().isHovering())
					Clicking.hover(Player.getPosition().translate(General.random(-1, 1), -General.random(4, 5)));

				// Once the altar is visible (and therefore the ladder too), we will recharge prayer and enter ladder, or just go in ladder if we dont need to pray.
				Timing.waitCondition(JConditions.objectVisible("Chaos altar"), 4000);
				rechargePrayerOrClickLadder();
				break;
				
			case LADDER:
				
				// Clicking altar first if we are praying, clicking ladder if not.
				rechargePrayerOrClickLadder();
				break;
				
			case INTERMEDIATE:
				
				// Just in case something happens, we will walk to the ladder.
				WebWalker.walkTo(getLadderWalkingTile());
				break;
				
			default:
				
				// failesafe 
				WebWalker.walkTo(getLadderWalkingTile());
				break;
		}
	}
	
	private void rechargePrayerOrClickLadder() {
		
		// Pray if prayer points are lower than the pray at point specified (plus some randomness).
		if (Vars.get().usingPrayer && shouldWePray()) {
			RSObject[] prayerAltar = Objects.find(10, Filters.Objects.nameContains("Chaos altar"));
			if (prayerAltar.length > 0)
				Timing07.waitCondition(() -> {
					return Utilities.accurateClickObject(prayerAltar[0], true, "Pray-at");
				}, 4000L);
			Timing.waitCondition(JConditions.isAnimating(), 3000L);
		}
		
		// Clicks ladder
		RSObject[] ladder = Objects.find(10, Filters.Objects.nameContains("Ladder"));
		if (ladder.length > 0)
			Utilities.accurateClickObject(ladder[0], false, "Climb");
		Timing.waitCondition(JConditions.areaContains(Cons.BANK), 4000L);
	}

	// Returns the current location of the player
	private State getLocation() {
		if (Cons.BANK.contains(Player.getPosition()))
			return State.BANK;
		else if (Cons.TELE.contains(Player.getPosition()))
			return State.TELE;
		else if (Cons.LADDER.contains(Player.getPosition()))
			return State.LADDER;
		else
			return State.INTERMEDIATE;
	}
	
	// Randomizes path a bit back to bank.
	private RSTile getLadderWalkingTile() {
		RSTile ladderTile = Cons.LADDER_WALKING_TILE;
		ladderTile.translate(General.random(1, 2), General.random(-1, 1));
		return ladderTile;
	}
	
	// Randomizes path a bit back to bank.
	private RSTile getAltarWalkingTile() {
		RSTile altarTile = Cons.PRAYER_ALTAR_WALKING_TILE;
		altarTile.translate(General.random(-1,1), General.random(0,1));
		return altarTile;
	}
	
	// User selects points to pray at, this adds a bit of randomness
	private boolean shouldWePray() {
		return SKILLS.PRAYER.getCurrentLevel() < (Vars.get().prayAt + General.randomSD(-10, 10, 5)) ? true : false;
	}
	
	// Some vars used in the method
	RSItem[] pouch;
	// Used to exit bank even if essence has not appeard yet in inventory
	boolean quickDraw; 
	// Will assist in adding a delay to eating food if we just drank a potion.
	private boolean justDrank;
	
	// Gets the essense, and drinks potions/food in necessary.
	private void getInv() {
		
		if (!Banking.isBankScreenOpen() && JBanking.isInBank())
			JBanking.open(true);
		
		// Tracked my own mouse movements when banking, and mouse speed increases substantially. This reflects that. (Also in place when crafting runes).
		Mouse.setSpeed(General.randomSD(150, 250, 180, 30));
		int i = 0;
		while ((!Inventory.isFull() || !JZMIUtils.hasFullPouches()) || i == 20)
		{ 
			General.sleep(General.randomSD(250, 450, 50));
			if (InventWrapper.inventContains(Cons.PURE_ESSENCE) && !JZMIUtils.hasFullPouches() && Inventory.isFull() || quickDraw == true) 
			{
				// Making sure we are not in the bank interface, and the inventory tab is open
				if (Banking.isBankScreenOpen())
					Keyboard.pressKeys(27);
				if (!TABS.INVENTORY.isOpen())
					Keyboard.pressKeys(27);
				
				// Pouch filling
				RSItem[] pouches = JZMIUtils.getPouches(2);
				Arrays.sort(pouches, (i1,i2) -> i2.getDefinition().getName().compareTo(i1.getDefinition().getName()));
				for (RSItem p : pouches)
					if (p != null)
						Clicking.click(p);
				
				// Potion drinking
				RSItem[] potion = Inventory.find(Filters.Items.idEquals(Vars.get().energyPotionID));
				if (potion.length > 0) {
					Clicking.click(potion);
					justDrank = true;
				}
				
				// Food eating
				RSItem[] food = Inventory.find(Filters.Items.idEquals(Vars.get().foodID));
				if (food.length > 0) {
					if (justDrank) {
						Clicking.hover(food);
						General.sleep(General.randomSD(1500, 2000, 200));
					}
					Clicking.click(food);
					justDrank = false;
				}
				
				// Pouch repairing
				if (InventWrapper.inventContains(Cons.ESSENCE_POUCHES_BAD))
					JZMIUtils.repairPouches();
				
				 // reset after a quick bank
				quickDraw = false;
			} 
			else if (!Inventory.isFull() || !JZMIUtils.hasFullPouches()) 
			{
				if (!Banking.isBankScreenOpen())
					JBanking.open(true);
				
				if (Inventory.find(Filters.Items.nameNotContains(Cons.GOODITEMS)).length > Vars.get().runesInInvent.length) 
					JBanking.depositAll(false);
				
				// Withdraw energy potion if needed based on user entered run limit (change to antiban method maybe).
				// TODO: add user entered data var and possibly antiban run energy methods
				if (Game.getRunEnergy() < 60)
					JBanking.withdraw(1, false, Filters.Items.idEquals(Vars.get().energyPotionID));
				
				// Withdraw food from bank based on user selected food.
				// TODO: add user entered data var, and antiban eat percentage
				if (Combat.getHP() < 60)
					JBanking.withdraw(1, false, Filters.Items.idEquals(Vars.get().foodID));

				// Withdrawing essence and setting quickDraw to true so that we can fill pouches quickly.
				if (JBanking.withdraw(0, false, Filters.Items.idEquals(Cons.PURE_ESSENCE)));
					quickDraw = true;
			}
		i++;
		}
		quickDraw = false;
		// true so we can run to ZMI altar before essence appears in inventory, saves a small amount of time.
		Vars.get().doneBanking = true; 
	}
}
