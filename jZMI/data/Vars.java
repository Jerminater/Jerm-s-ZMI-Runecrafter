package scripts.jZMI.data;

import java.util.ArrayList;

import org.tribot.api2007.Prayer.PRAYERS;

import scripts.jZMI.graphics.Settings;

public class Vars {
		
	// instance stuff
	private static Vars vars;

	public boolean usingPrayer;
		
	public static Vars get() {
		return vars == null? vars = new Vars() : vars;
	}
			
	public static void reset() {
		vars = new Vars();
	}
	
	public Settings settings;
	
	public String[] pouches = new String[4];
	public String[] runesInInvent = new String[1]; 
	public String[] runesInPouch = new String[3];
	
	public int[] pouchRows = new int[4]; 
	public int[] pouchColumns = new int[4]; 
	public int[] inventRuneRow = new int[4]; 
	public int[] inventRuneColumn = new int[4];
	
	public int runePouchRow; 
	public int runePouchColumn;
	
	public boolean usingPouches, usingRunePouch, usingInventRunes;
	public boolean endCond = false; 
	public boolean filledPouches = true;
	public boolean firstBank;
	public String status = "Initializing";
	public long runTime = 99999;
	public long waitTime;
	//public String curPouch;
	public boolean doneBanking;
	
	public boolean smallPouchFull, medPouchFull, largePouchFull, giantPouchFull;
	public boolean hoverAlter, shouldPray;

	public int numPouches;

	public int prayAt;
	public int eatAt;

	public int zmiID, chosenWorld;

	public int energyPotionID, foodID;

	public long xpGained, levelsGained, xpPerHour, tripsMade, timeToLevel;

	public ArrayList<PRAYERS> quickPrayers = new ArrayList<PRAYERS>();

    public void setAccountSettings(Settings Settings) {
    	Vars.get().settings = new Settings(true);
    }
	
	// for quick testing
	public static void simulateGUI() {
		Vars.get().usingPouches = true;
		Vars.get().pouchRows[0] = 4;
		Vars.get().pouchRows[1] = 3;
		Vars.get().pouchRows[2] = 3;
		
		Vars.get().pouchColumns[0] = 1;
		Vars.get().pouchColumns[1] = 1;
		Vars.get().pouchColumns[2] = 2;
		
		Vars.get().pouches[0] = "Small pouch";
		Vars.get().pouches[1] = "Medium Pouch";
		Vars.get().pouches[2] = "Large Pouch";
		
		Vars.get().usingInventRunes = true;
		Vars.get().runesInInvent[0] = "Air rune";
		Vars.get().inventRuneRow[0] = 3;
		Vars.get().inventRuneColumn[0] = 3;
		
		Vars.get().usingRunePouch = true;
		Vars.get().runesInPouch[0] = "Law rune";
		Vars.get().runesInPouch[1] = "Astral rune";
		Vars.get().runesInPouch[2] = "Cosmic rune";
		Vars.get().runePouchRow = 4;
		Vars.get().runePouchColumn = 2;
		Vars.get().numPouches = 3;
		
		Vars.get().energyPotionID = 3022;
		Vars.get().foodID = 7946;
		Vars.get().eatAt = 60;
		
		Vars.get().usingPrayer = true;
		Vars.get().prayAt = 50;
		Vars.get().quickPrayers.add(PRAYERS.PROTECT_FROM_MISSILES);
		Vars.get().quickPrayers.add(PRAYERS.RAPID_HEAL);
		
		Vars.get().chosenWorld = 327;
	}
}
