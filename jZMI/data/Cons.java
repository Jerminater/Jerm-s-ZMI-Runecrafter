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
	public static int smallBit = 2088;
	public static int medBit = 2089;
	public static int largeBit = 2090; 
	public static int giantBit = 2091; //I dont have rc level for this or giant, so these are guesses.
	
	public static int smallBitCount = 603;
	public static int medBitCount = 604;
	public static int largeBitCount = 605; 
	public static int giantBitCount = 606;
	
	// escape to close bit
	public static int escapeBit = 4681;
	
	// Shift clicking bit
	public static int shiftBit = 5542;
	
	public static RSInterfaceChild darkMageInterface = Interfaces.get(218, 103);
	
	public static String[] goodItems = {"Pouch","pouch","Pure"};
	
	public enum Tiles {
		
		ZMI_ALTAR(new RSTile(3058, 5579, 0)),
		LADDER_WALKING_TILE(new RSTile(2452, 3232, 0)),
		PRAYER_ALTAR_WALKING_TILE(new RSTile(2454, 3232, 0));

		private final RSTile tile;
		
		private Tiles(RSTile tile) {
			this.tile = tile;
		}
		
		public RSTile getTile() {
			return tile;
		}
	}
	
	public enum Items {
		//largest to smallest
		ESSENCE_POUCHES_GOOD(new int[] {5514, 5512, 5510, 5509}),
		ESSENCE_POUCHES_BAD(new int[] {5515, 5513, 5511}),
		ESSENCE_POUCHES_ALL(new int[] {5515, 5513, 5511, 5514, 5512, 5510, 5509}),
		PURE_ESSENCE(new int[] {7936});
		
		private final int[] items;
		
		private Items(int[] items) {
			this.items = items;
		}
		
		public int[] getIDs() {
			return items;
		}
	}
	
	public enum Areas {
		
		DUNGEON(new RSArea(new RSTile(3008, 5631, 0), new RSTile(3071, 5568, 0))),
		TELE(new RSArea(new RSTile(2463, 3250, 0), new RSTile(2472, 3241, 0))),
		BANK(new RSArea(new RSTile[] {new RSTile(3008, 5619, 0),
			    new RSTile(3008, 5626, 0), new RSTile(3014, 5631, 0),
			    new RSTile(3023, 5631, 0), new RSTile(3023, 5620, 0)})),
		LADDER_CLICK(new RSArea(new RSTile[] {new RSTile(2452, 3231, 0),
			    new RSTile(2453, 3233, 0),new RSTile(2456, 3233, 0),
			    new RSTile(2455, 3231, 0)})),
		LADDER(new RSArea(new RSTile(2451, 3234, 0),  new RSTile(2458, 3228, 0))),
		PATH_TO_LADDER_CLICK(new RSArea(new RSTile[] {new RSTile(2457, 3239, 0),
			    new RSTile(2454, 3242, 0),new RSTile(2457, 3244, 0),
			    new RSTile(2458, 3240, 0)})),
		PATH_TO_LADDER(new RSArea(new RSTile(2451, 3245, 0), new RSTile(2457, 3229, 0))),
		ALTAR(new RSArea( new RSTile(3052, 5590, 0), new RSTile(3067, 5572, 0)));
		private final RSArea areas;
		
		private Areas(RSArea areas) {
			this.areas = areas;
		}
		
		public RSArea getArea() {
			return areas;
		}
	}
}

