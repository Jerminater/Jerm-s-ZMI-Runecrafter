package scripts.jZMI.utils;

import java.util.ArrayList;
import java.util.Arrays;

import org.tribot.api.Clicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Keyboard;
import org.tribot.api.input.Mouse;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.NPCChat;
import org.tribot.api2007.Options;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Camera;
import org.tribot.api2007.Game;
import org.tribot.api2007.GameTab.TABS;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSInterfaceMaster;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSVarBit;

import scripts.api.utilities.JBanking;
import scripts.api.utilities.JCamera;
import scripts.api.utilities.JConditions;
import scripts.api.utilities.RunePouch;
import scripts.api.utilities.Utilities;
import scripts.api.webwalker_logic.WebWalker;
import scripts.api.wrappers.InventWrapper;
import scripts.jZMI.data.Vars;
import scripts.jZMI.data.Cons;

public class JZMIUtils {
	
	// Vars for methods
	private static ArrayList<RSItem> fullPouches = new ArrayList<RSItem>();
	private static ArrayList<RSItem> emptyPouches = new ArrayList<RSItem>();
	private static ArrayList<RSItem> notEmptyPouches = new ArrayList<RSItem>();
	private static ArrayList<RSItem> partlyFullPouches = new ArrayList<RSItem>();
	private static RSInterface compassChild = Interfaces.get(548, 10, 1);
	
	/*
	 * Ran only once. If the user selects to setup the bank, this will run through all withdrawals, and settings making sure we are
	 * set to runecraft.
	*/
	public static void setupBankAndInvent() {
		
		// For testing. 
		Vars.simulateGUI();
		Vars.get().status = "Setting up Bank";
		
		// Making sure bank is open
		if (!Banking.isBankScreenOpen() && Banking.isInBank()) {
			JBanking.open(true);
		} else {
			WebWalker.walkToBank();
			if (!Banking.isBankScreenOpen() && Banking.isInBank())
				JBanking.open(true);
		}
		
		
		// Removing ability to set placeholders if enabled
		if (RSVarBit.get(3755).getValue() == 1) { // Varbit for enabling placeholders
			RSInterfaceChild[] bankChildren = Interfaces.getChildren(12);
			if (bankChildren != null)
				for (RSInterfaceChild c: bankChildren)
					if (c.getComponentName().contains("set placeholders")) {
						Clicking.click(c);
						break;
					}
		}
		
		// Need some inventory spots so this makes sure we have some
		if (InventWrapper.getAll().length > 15)
			JBanking.depositAll(false);
		
		// Withdrawing pouches
		if (Vars.get().usingPouches)
			for (String s: Vars.get().pouches)
				JBanking.withdraw(1, false, s);
		
		// Withdrawing Inventory runes
		if (Vars.get().usingInventRunes) 
			for (String s: Vars.get().runesInInvent) {
				RSItem[] r = Banking.find(s);
				if (r.length > 0)
					if (r[0].getStack() > 1)
						JBanking.withdraw(0, false, s);
			}
		
		// Withdrawing Rune Pouch and runes that go in it
		if (Vars.get().usingRunePouch) {
			if (RunePouch.getPouch() == null)
				JBanking.withdraw(1, false, "Rune pouch");
			for (String s: Vars.get().runesInPouch)
				if (RunePouch.getQuantity(s) < 16000) {
					RSItem[] r = Banking.find(s);
					if (r.length > 0)
						if (r[0].getStack() > 1)
							JBanking.withdraw(-1, false, s);
				}
		}
		
		// organizing inventory based on GUI specifications
		if (Banking.isBankScreenOpen())
			JBanking.close(true);
		
		fillPouchAndMoveItems();
		
		// setting placeholders for quick banking
		if (!Banking.isBankScreenOpen())
			JBanking.open(true);
		RSInterfaceChild[] children = Interfaces.getChildren(12);
		RSInterfaceChild menu = null;
		if (children.length > 0) {
			for (RSInterfaceChild c : children)
				if (c.getActions() != null) {
					if (c.getActions()[0].contains("Show menu")) {
						menu = c;
						c.click("Show menu");
						break;
					}
				}
			for (RSInterfaceChild c : children) {
				if (c.getActions() != null)
					if (c.getActions()[0].contains("all placeholders"))
						c.click("Release all placeholders");
				if (c.getActions() != null)
					if (c.getActions()[0].contains("Fill")) {
						c.click("Fill");
					}
			}
			if (menu != null)
				menu.click("Dismiss menu");
		}
		JBanking.close(false);
	}
	
	/*
	 * Will fill the rune pouch and move items to user defined inventory location.
	*/
	public static void fillPouchAndMoveItems() {
		if (!TABS.INVENTORY.isOpen())
			TABS.INVENTORY.open();
		
		// Placing all runes in pouch and placing into pouch if we have space.
		RSItem pouch = RunePouch.getPouch();
		if (pouch != null && Vars.get().usingRunePouch) {
			RSItem[] rune1 = Inventory.find(Vars.get().runesInPouch[0]);
			RSItem[] rune2 = Inventory.find(Vars.get().runesInPouch[1]);
			RSItem[] rune3 = Inventory.find(Vars.get().runesInPouch[2]);
			if (rune1.length > 0)
				if (RunePouch.getQuantity(Vars.get().runesInPouch[0]) < 16000)
					Utilities.useItemOnItem(rune1[0], pouch);
			if (rune2.length > 0)
				if (RunePouch.getQuantity(Vars.get().runesInPouch[1]) < 16000)
					Utilities.useItemOnItem(rune2[0], pouch);
			if (rune3.length > 0)
				if (RunePouch.getQuantity(Vars.get().runesInPouch[2]) < 16000)
					Utilities.useItemOnItem(rune3[0], pouch);
		}
		
		// Dragging pouches to correct inventory index from GUI
		if (Vars.get().usingPouches) {
			int i = 0;
			for (String s: Vars.get().pouches) {
				if (s != null) {
					pouch = Inventory.find(s)[0];
					if (pouch.getIndex() != ((Vars.get().pouchRows[i]-1)*4+(Vars.get().pouchColumns[i]-1))) {
						Utilities.dragItemToSlot(pouch, Vars.get().pouchRows[i], Vars.get().pouchColumns[i]);
					}
				}
				i++;
			}
		}
		
		// Dragging rune pouch to correct inventory index from GUI
		if (Vars.get().usingRunePouch) {
			RSItem p = RunePouch.getPouch();
			if (p.getIndex() != ((Vars.get().runePouchRow-1)*4 + (Vars.get().runePouchColumn)-1))
				Utilities.dragItemToSlot(p, Vars.get().runePouchRow, Vars.get().runePouchColumn);	
			if (Interfaces.get(190) != null)
				Keyboard.pressKeys(27);
		}

		// Dragging inventory runes to correct inventory index from GUI
		if (Vars.get().usingInventRunes){
			int i = 0;
			for (String s: Vars.get().runesInInvent) {
				if (s != null) {
					RSItem rune = Inventory.find(s)[0];
					if (rune != null)
						if (rune.getIndex() != ((Vars.get().inventRuneRow[i]-1)*4 + (Vars.get().inventRuneColumn[i]-1)))
							Utilities.dragItemToSlot(rune, Vars.get().inventRuneRow[i], Vars.get().inventRuneColumn[i]);
				}
				i++;
			}
		}
	}

	/**
	 * Checks whether we have all full pouches or not.
	 * 
	 * @return true if all pouches are full.
	*/
	public static boolean hasFullPouches() {
		RSItem[] pouches = Inventory.find(Cons.ESSENCE_POUCHES_ALL);
		if (pouches.length == 0)
			return true;
		for (RSItem i:pouches) {
			switch (i.getDefinition().getName()) {
				case ("Small pouch"):
					
					RSVarBit sBit = RSVarBit.get(Cons.SMALLBITCOUNT);
					if (sBit != null)
						if (sBit.getValue() < 3)
							return false;
					break;
					
				case ("Medium pouch"):
					
					RSVarBit mBit = RSVarBit.get(Cons.MEDBITCOUNT);
					if (mBit != null)
						if (mBit.getValue() < 6)
							return false;
					break;
					
				case ("Large pouch"):
					
					RSVarBit lBit = RSVarBit.get(Cons.LARGEBITCOUNT);
					if (lBit != null)
						if (lBit.getValue() < 9)
							return false;
					break;
					
				case ("Giant pouch"):
					
					RSVarBit gBit = RSVarBit.get(Cons.GIANTBITCOUNT);
					if (gBit != null)
						if (gBit.getValue() < 12)
							return false;
					
					break;
			}
		}
		return true;
	}

	/**
	 * Checks to make sure we have run energy, quick prayer is on, camera is in the correct range, and inventory is setup correctly
	*/
	public static void performChecks() {
		
		// Setting camera to where we need it in case it was moved.
		if ((Camera.getCameraRotation() < 345) && (Camera.getCameraRotation() > 15) && compassChild != null) {
			Clicking.click(compassChild);
			General.sleep(400);
		}
		if (compassChild != null && Camera.getCameraAngle() < 90) {
			JCamera.setCameraAngle(100);
		}
		
		// No need for antiban, we will always run.
		if (!Game.isRunOn())
			Options.setRunEnabled(true);
		
		// Making sure we have clicked quick prayers in case prayer ran out on us or something.
		if (Vars.get().usingPrayer)
			Options.setQuickPrayersEnabled(true);
		
		fillPouchAndMoveItems();
	}

	/**
	 * Hovers the next action while crafting at the altar.
	*/
	public static void hoverNextActionAndWait() {
		
		// Wait till we start runecrafting
		Timing.waitCondition(JConditions.isAnimating(), 3000);
		
		// Thread hovers next action so that the timer can accurately time the action of crafting.
		new Thread(() -> {
			RSItem[] p = JZMIUtils.getPouches(0);
			if (p.length > 1)
				Arrays.sort(p, (p1,p2) -> p2.getDefinition().getName().compareTo(p1.getDefinition().getName())); // getDefinition can, but won't return null
	    	if (p != null && p.length > 0) {
	    		if (TABS.INVENTORY.isOpen())
	    			p[0].hover();
	    		else {
	    			TABS.INVENTORY.open();
	    			p[0].hover();
	    		}
	    	} else {
	    		if (!TABS.MAGIC.isOpen())
	    			TABS.MAGIC.open();
	    		RSInterfaceMaster parent = Interfaces.get(218);
	    		if (parent != null)
		    		for (RSInterfaceChild c: parent.getChildren()) {
		    			String compName = c.getComponentName();
		    			if (compName != null)
		    				if (compName.contains("Ourania Teleport")) {
		    					c.hover();
		    					break;
		    				}
		    		}
	    	}
		}).start();

		// Tweaked so that the player will tick craft essence about 40-60% of the time. Aimed for this number based on
		// how often I was able to tick craft.
		General.sleep(General.randomSD(1800, 2100, 1900, 100));

	}

	/**
	 * Will return the pouches of the state in arguments. 
	 * 
	 * @param state (-1 for empty, 0 for not empty, 1 for full, 2 for partly full)
	 * @return Desired RSItem array. Empty if none found.
	*/
	public static RSItem[] getPouches(int state) {
		RSItem[] pouches = Inventory.find(Cons.ESSENCE_POUCHES_GOOD);
		fullPouches.clear(); emptyPouches.clear(); notEmptyPouches.clear(); partlyFullPouches.clear();
		if (pouches.length <1) 
			return pouches;
		for (RSItem i:pouches) {
			switch (i.getDefinition().getName()) 
			{
			case ("Small pouch"):
				pouchPlacer(i,RSVarBit.get(Cons.SMALLBITCOUNT), 3);
				break;
			case ("Medium pouch"):
				pouchPlacer(i,RSVarBit.get(Cons.MEDBITCOUNT), 6);
				break;
			case ("Large pouch"):
				pouchPlacer(i,RSVarBit.get(Cons.LARGEBITCOUNT), 9);
				break;
			case ("Giant pouch"):
				pouchPlacer(i,RSVarBit.get(Cons.GIANTBITCOUNT), 12);
				break;
			}
		}
		
		if (state == -1) 
			return emptyPouches.toArray(new RSItem[emptyPouches.size()]);
		else if (state == 0)
			return notEmptyPouches.toArray(new RSItem[notEmptyPouches.size()]);
		else if (state == 1)
			return fullPouches.toArray(new RSItem[fullPouches.size()]);
		else if (state == 2)
			return partlyFullPouches.toArray(new RSItem[partlyFullPouches.size()]);
		else 
			return partlyFullPouches.toArray(new RSItem[partlyFullPouches.size()]);
	}
	
	/**
	 * Used in getPouches(). Will place pouches into correct ArrayList.
	*/
	private static void pouchPlacer(RSItem i, RSVarBit bit, int length) {
		if (bit != null)
			if (bit.getValue() == length) {
				fullPouches.add(i);
				notEmptyPouches.add(i);
			} else if (bit.getValue() == 0) {
				emptyPouches.add(i);
				partlyFullPouches.add(i);
			} else {
				notEmptyPouches.add(i);
				partlyFullPouches.add(i);
			}
	}
	
	/**
	 * Repairs Pouches while banking if they are broken.
	*/
	public static void repairPouches() {
		
		// Opening magic tab
		if (!TABS.MAGIC.isOpen())
			TABS.MAGIC.open();
		
		// Clicks the Dark Mage option on NPC Contact spell. If no interface exists for this, it will find it and cache it.
		if (Cons.DARKMAGEINTERFACE != null)
			Cons.DARKMAGEINTERFACE.click("Dark Mage");
		else {
			RSInterfaceChild[] children = Interfaces.getChildren(218);
			RSInterfaceChild child = null;
			if (children != null)
				for (RSInterfaceChild c: children) {
					String [] actions = c.getActions();
					if (actions != null)
						if (actions.toString().contains("Dark Mage"))
							child = c;	
				}
			if (child != null) {
				Cons.DARKMAGEINTERFACE = child;
				child.click("Dark Mage");
			}
		}
		
		// Waits till we start conversing
		Timing.waitCondition(JConditions.isConversing(), 7000L);

		// Not tested for if abyssal book is in bank.
		int i = 0;
		while (i > 100 || InventWrapper.inventContains(Cons.ESSENCE_POUCHES_BAD)) {
			General.sleep(100);
			if (NPCChat.getClickContinueInterface() != null)
				NPCChat.clickContinue(true);
			if (Interfaces.isInterfaceSubstantiated(219, 0, 2))
				Clicking.click(Interfaces.get(219, 0, 2));
			else if (Interfaces.isInterfaceSubstantiated(219, 0, 1))
				Clicking.click(Interfaces.get(219, 0, 1));
			i++;
		}
				
		// Moves pouch back if it was moved during repair
		fillPouchAndMoveItems();
	}

	// Will hover the map near the bottom if called
	public static void hoverMap() {
		
		// Sleep in case this is in a condition loop. Creates an active movement on the map like a human would.
		General.sleep(General.randomSD(50, 1200, 700, 300));
		RSInterfaceChild map = Interfaces.get(548, 8);
		
		// Will generate some pseudo random numbers to hover on the map. Mouse speed is decreased as likely a player is only slightly moving the mouse when doing this.
		if (map != null) {
			int yPos = (int) (map.getAbsoluteBounds().getY()+map.getHeight()) - 20;
			int xPos = (int) map.getAbsoluteBounds().getCenterX();
			Mouse.move(xPos + General.randomSD(-20, 20, 0, 3), yPos + General.randomSD(0, 20, 5, 3));
			if (Mouse.getSpeed() != 20)
				Mouse.setSpeed(20);
		}
		Mouse.setSpeed(100);
	}
}
