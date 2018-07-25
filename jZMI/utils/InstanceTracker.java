package scripts.jZMI.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.tribot.api.General;
import org.tribot.util.Util;

import scripts.jZMI.data.Vars;

/**
 * 
 * @author Einstein
 *
 */
public class InstanceTracker extends Thread {

	private static final File INSTANCE_REGISTRY = new File(Util.getWorkingDirectory().getAbsolutePath() + "/instance_registry.txt");
	public byte MAX_INSTANCES = 1;
	private static final int DELAY = 3000;
	
	private long instanceID;
	private ArrayList<Long> lines = new ArrayList<Long>();
	private String line;

	public InstanceTracker(byte instances) {
		this.MAX_INSTANCES = instances;
		instanceID = General.randomLong(100000000, 900000000);
		try (PrintWriter pw = new PrintWriter(INSTANCE_REGISTRY);) {
			/* Delete old register values. Otherwise, old garbage data could prevent the user from accessing the script ("ghost instances") */
		} catch (FileNotFoundException e) {
		
		}
	}

	@Override
	public void run() {
		while (true) {
			
			General.sleep(DELAY);
			lines.clear();
			
			try (BufferedReader reader = new BufferedReader(new FileReader(INSTANCE_REGISTRY));
					BufferedWriter writer = new BufferedWriter(new FileWriter(INSTANCE_REGISTRY, true));) {

				// Read the file and store the data in an ArrayList for future usage.
				while ((line = reader.readLine()) != null)
					lines.add(Long.parseLong(line));
				
				// Register current instance ID if it's not already registered.
				register: {
					for (int i = 0; i < lines.size(); i++)
						if (lines.get(i).equals(instanceID))
							/* The current instance is already registered. Break out of the code.*/
							break register;
					/* The current instance is not registered! Register it:  */
					writer.write(Long.toString(instanceID) + System.getProperty("line.separator"));
				}

				// If the user surpassed the maximum number of instances, crash the script.
				if (lines.size() > MAX_INSTANCES) {
					/* Note: if the instance ID has just been added, the instance count will be read correctly on the next iteration of the loop. */
					General.println(General.getTRiBotUsername() + ": you have exceeded the maximum number of instances("+ MAX_INSTANCES + "). The script will now exit.");
					Vars.get().endCond = true;
				}

			} catch (NumberFormatException e) {
				/** User attempted to hack the system. */
				System.exit(0);
			} catch (Exception e) {

			}
		}
	}
	
}