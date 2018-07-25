package scripts.jZMI.graphics;

import org.tribot.api.Timing;
import org.tribot.api2007.Skills;
import org.tribot.script.Script;

import scripts.jZMI.data.Vars;

public class PaintInfoThread extends Thread {
	
	private long startTime, startXP, startLevel;
	private Script script;
	
	private boolean running;
	
	public PaintInfoThread(Script script) {
		this.script = script;
		this.startTime = Timing.currentTimeMillis();
		this.startXP = Skills.getXP(Skills.SKILLS.RUNECRAFTING);
		this.startLevel = Skills.getActualLevel(Skills.SKILLS.RUNECRAFTING);
		running = true;
	}
	
	@Override
	public void run() {
		while (running) {
			Vars.get().runTime = Timing.currentTimeMillis() - this.startTime;
			Vars.get().xpGained = Skills.getXP(Skills.SKILLS.RUNECRAFTING) - this.startXP;
			Vars.get().levelsGained = Skills.getActualLevel(Skills.SKILLS.RUNECRAFTING) - this.startLevel;
			
			if (Vars.get().runTime < 1)
				Vars.get().xpPerHour = 1;
			else
				Vars.get().xpPerHour = (Vars.get().xpGained*3600000)/Vars.get().runTime;
			
			if (Vars.get().xpPerHour != 0)
				Vars.get().timeToLevel = ((long)Skills.getXPToNextLevel(Skills.SKILLS.RUNECRAFTING)*(60*1000*60))/(Vars.get().xpPerHour);
			else
				Vars.get().timeToLevel = 0;
			

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			running = this.script.isActive();
		}
	}
}