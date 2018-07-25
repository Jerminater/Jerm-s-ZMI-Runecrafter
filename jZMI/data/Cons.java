package scripts.jZMI.data;

import org.tribot.api2007.Interfaces;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSTile;

public class Cons {

	/*
	 * VarBits for the pouches to detect if they contain ess or not. 
	 * 
	 * If bit returns a 2, pouch has essence. 0 if not.
	 * 
	 * Count returns count of essence in pouch.
	 */ 
	public final static int SMALLBIT = 2088;
	public final static int MEDBIT = 2089;
	public final static int LARGEBIT = 2090; 
	public final static int GIANTBIT = 2091; //I dont have rc level for this or giant, so these are guesses.
	
	public final static int SMALLBITCOUNT = 603;
	public final static int MEDBITCOUNT = 604;
	public final static int LARGEBITCOUNT = 605; 
	public final static int GIANTBITCOUNT = 606;
	
	// Escape to close bit
	public final static int ESCAPEBIT = 4681;
	
	// Shift clicking bit
	public final static int SHIFTBIT = 5542;
	
	public static RSInterfaceChild DARKMAGEINTERFACE = Interfaces.get(218, 103);
	
	public final static String[] GOODITEMS = {"Pouch","pouch","Pure"};
	
	// Tiles
	public final static RSTile ZMI_ALTAR = new RSTile(3058, 5579, 0);
	public final static RSTile LADDER_WALKING_TILE = new RSTile(2452, 3232, 0);
	public final static RSTile PRAYER_ALTAR_WALKING_TILE = new RSTile(2454, 3232, 0);

	//Items
	public final static int[] ESSENCE_POUCHES_GOOD = new int[] {5514, 5512, 5510, 5509};
	public final static int[] ESSENCE_POUCHES_BAD = new int[] {5515, 5513, 5511};
	public final static int[] ESSENCE_POUCHES_ALL = new int[] {5515, 5513, 5511, 5514, 5512, 5510, 5509};
	public final static int PURE_ESSENCE = 7936;
	
	// Areas
	public final static RSArea DUNGEON = new RSArea(new RSTile(3008, 5631, 0), new RSTile(3071, 5568, 0));
	public final static RSArea TELE = new RSArea(new RSTile(2463, 3250, 0), new RSTile(2472, 3241, 0));
	public final static RSArea BANK = new RSArea(new RSTile[] {new RSTile(3008, 5619, 0),
		    new RSTile(3008, 5626, 0), new RSTile(3014, 5631, 0),
		    new RSTile(3023, 5631, 0), new RSTile(3023, 5620, 0)});
	public final static RSArea LADDER_CLICK = new RSArea(new RSTile[] {new RSTile(2452, 3231, 0),
		    new RSTile(2453, 3233, 0),new RSTile(2456, 3233, 0),
		    new RSTile(2455, 3231, 0)});
	public final static RSArea LADDER = new RSArea(new RSTile(2451, 3234, 0),  new RSTile(2458, 3228, 0));
	public final static RSArea PATH_TO_LADDER_CLICK = new RSArea(new RSTile[] {new RSTile(2457, 3239, 0),
		    new RSTile(2454, 3242, 0),new RSTile(2457, 3244, 0),
		    new RSTile(2458, 3240, 0)});
	public final static RSArea PATH_TO_LADDER = new RSArea(new RSTile(2451, 3245, 0), new RSTile(2457, 3229, 0));
	public final static RSArea ALTAR = new RSArea( new RSTile(3052, 5590, 0), new RSTile(3067, 5572, 0));
}

