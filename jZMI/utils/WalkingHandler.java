package scripts.jZMI.utils;

import org.tribot.api2007.types.RSTile;

import scripts.jZMI.data.Cons;
import scripts.jZMI.data.Vars;

public class WalkingHandler {

	public static void tileClicked(RSTile tile) {
		
		// Detects when clicked tile near altar
		if (Cons.ALTAR.contains(tile))
			Vars.get().hoverAlter = true;
		
		// Might add some more here for player unique walking patterns.
	}
}

