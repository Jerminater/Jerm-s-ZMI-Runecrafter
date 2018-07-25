package scripts.jZMI;

import java.awt.Color;
import java.awt.Graphics;
//import java.net.MalformedURLException;
//import java.net.URL;
import java.awt.Point;
import java.util.ArrayList;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Keyboard;
import org.tribot.api2007.Combat;
import org.tribot.api2007.Login;
import org.tribot.api2007.Login.STATE;
import org.tribot.api2007.Prayer.PRAYERS;
import org.tribot.api2007.Skills;
import org.tribot.api2007.WorldHopper;

import org.tribot.api2007.types.RSTile;
import org.tribot.api2007.types.RSVarBit;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Ending;
import org.tribot.script.interfaces.MinimapClicking;
import org.tribot.script.interfaces.MouseSplinePainting;
import org.tribot.script.interfaces.Painting;
import org.tribot.script.interfaces.Starting;

import scripts.api.framework.Task;
import scripts.api.framework.TaskSet;
import scripts.jZMI.utils.InstanceTracker;
import scripts.jZMI.utils.WalkingHandler;
import scripts.jZMI.data.Cons;
import scripts.jZMI.data.Vars;
//import scripts.jZMI.graphics.FXMLString;
//import scripts.jZMI.graphics.GUI;
import scripts.jZMI.graphics.PaintInfoThread;
import scripts.jZMI.tasks.Craft;
import scripts.jZMI.tasks.GoToBank;
import scripts.api.paint.FluffeesPaint;
import scripts.api.paint.PaintInfo;
import scripts.api.utilities.AMWorldHopper;
import scripts.api.utilities.Utilities;

@ScriptManifest(authors = { "Jerminater" }, category = "Runecrafting", name = "jZMI", version = 1.00, 
	description = "Will train Runecrafting at the ZMI alter", gameMode = 1)

public class JZMI extends Script implements MinimapClicking, MouseSplinePainting, Starting, Ending, Painting, PaintInfo {
		
	private final FluffeesPaint Paint = new FluffeesPaint(this, FluffeesPaint.PaintLocations.TOP_LEFT_PLAY_SCREEN,
			new Color[]{new Color(255, 251, 255)}, "Tahoma", new Color[]{new Color(255, 218, 185, 127)},
            new Color[]{new Color(139, 119, 101)}, 1, false, 5, 3, 0);
	//private URL stylesheet;
	//private GUI gui;
	private boolean paintThreadStarted;

	public void onStart() {
		// Initializing GUI
		/*println("Opening GUI");
		try {
			stylesheet = new URL("");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		gui = new GUI(FXMLString.get, stylesheet);
		gui.show();*/
		setRandomSolverState(false);
		new InstanceTracker((byte) 1).start();
	}
	
	@Override
	public void run() {
		//while (gui.isOpen())
		//	sleep(500);
		//making sure we are logged in
		while (Login.getLoginState() != STATE.INGAME)
			General.sleep(300, 500);
		
		// Initializing settings and vars
		Vars.simulateGUI();
		Vars.get().firstBank = false;
		SetSettings();
		
		// Start paint data tracking threads
		new PaintInfoThread(this).start();
		paintThreadStarted = true;
		
		// initialize tasks and loop through them
		TaskSet tasks = new TaskSet(new Craft(), new GoToBank());
	    while (!Vars.get().endCond) {
	    	sleep(100);
	       Task task = tasks.getValidTask();
	        if (task != null) {
	        	Vars.get().status = task.toString();
	            task.execute();
	        }
	    }	
	}
	
	// Gets settings corrected. Might add support for making sure inventory and magic tab are on correct keys, or just make it dynamic with varbits.
	private void SetSettings() {

		// Shift clicking redundancy
		if (RSVarBit.get(Cons.shiftBit).getValue() == 0)
			Utilities.shiftDropping(true);
				
		// Escape close
		if (RSVarBit.get(Cons.escapeBit).getValue() == 0)
			Utilities.escapeClose();
				
		Keyboard.pressKeys(27);
		
		// Making sure auto retaliate is off
		if (Combat.isAutoRetaliateOn())
			Combat.setAutoRetaliate(false);
		 
		// Setting quick prayers.
		if (Vars.get().usingPrayer)
			Utilities.setQuickPrayers(new PRAYERS[] {PRAYERS.RAPID_HEAL, PRAYERS.STEEL_SKIN});
							
		// Hopping to selected world
		General.println("setting payer");
		if (WorldHopper.getWorld() != Vars.get().chosenWorld)
			AMWorldHopper.hopWorlds(Vars.get().chosenWorld);
	}

	@Override
	public void onEnd() {
		General.println("Thanks for Runecrafting with me!");
	}

	@Override
    public void onPaint(Graphics g) {
		if (paintThreadStarted)
			Paint.paint(g);
	}
	
	@Override
    public String[] getPaintInfo() {
        return new String[] {"Jerm's ZMI V1.0", 
        					 "Runtime: " + Timing.msToString(Vars.get().runTime),
        					 "Status: " + Vars.get().status, 
        					 "Runecrafting level: " + Skills.getActualLevel(Skills.SKILLS.RUNECRAFTING)+ "(+" + Vars.get().levelsGained + ")" ,
        					 "RC xp gained: " + Vars.get().xpGained + "(" + Vars.get().xpPerHour + " xp/hr)",
        					 "TTL (xp): " + Timing.msToString(Vars.get().timeToLevel) + "(" + Skills.getXPToNextLevel(Skills.SKILLS.RUNECRAFTING) + ")",
        					 "Trips Made: " + Vars.get().tripsMade};
    }

	@Override
	public void paintMouseSpline(Graphics g, ArrayList<Point> list) {
		// TODO will add in future
	}

	@Override
	public void minimapClicked(RSTile tile) {
		WalkingHandler.tileClicked(tile);
	}
}
