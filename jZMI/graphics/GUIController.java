package scripts.jZMI.graphics;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

import org.tribot.api.General;

import com.allatori.annotations.DoNotRename;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import scripts.jZMI.data.Vars;
import scripts.jZMI.graphics.GUI;

@DoNotRename
public class GUIController implements Initializable {

	private GUI gui;

	// Inits
	@FXML @DoNotRename private Button startScriptButton, loadButton, saveButton;

	@FXML @DoNotRename private ComboBox<String> availableSettingsBox;
	
	@FXML @DoNotRename private TextField saveSettingsName;
	
	@FXML @DoNotRename private String settingsName;

	@FXML @DoNotRename private File directory = new File(GUIController.class.getProtectionDomain().getCodeSource().getLocation().getPath() + "settings");
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		//Put anything that you need the GUI to do when it is loaded.
		settingsName = getSaveFile();
		loadSettings();
	}
	
	// All events go here

	
	@FXML @DoNotRename public void startScript() {
		General.println("Enjoy Jerm's ZMI Runecrafter");
		saveSettings();
		setSettings();
		gui.close();
	}
	
	@FXML @DoNotRename public void saveSettings() {
		if (!directory.exists())
			directory.mkdirs();
		Properties prop = new Properties();
		OutputStream output = null;
		String saveFilePath = directory.getAbsolutePath() + "\\" + General.getTRiBotUsername();

		try {

			output = new FileOutputStream(saveFilePath);

			// set the properties value
			prop.setProperty("database", "localhost");
			prop.setProperty("dbuser", "mkyong");
			prop.setProperty("dbpassword", "password");

			// save properties to project root folder
			prop.store(output, null);
			General.println("Settings saved successfully.");
		} catch (IOException io) {
			General.println("Error attempting to save settings.");
			io.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					General.println("failed to close output stream");
					e.printStackTrace();
				}
			}

		}
    }
	
	@FXML @DoNotRename public void loadSettings() {
		Properties prop = new Properties();
		InputStream input = null;
		String settingsFileName = settingsName;
		String settingsFilePath = directory.getAbsolutePath() + "\\" + settingsFileName;
		
		if (settingsFileName != "No Saved Settings Found") {
			try {
		
				input = new FileInputStream(settingsFilePath);
		
				// load a properties file
				prop.load(input);
		
				// get the property value and print it out
				System.out.println(prop.getProperty("database"));
				System.out.println(prop.getProperty("dbuser"));
				System.out.println(prop.getProperty("dbpassword"));
		
				General.println("Settings loaded sucessfully.");
			} catch (IOException ex) {
				General.println("Error loading settings.");
				ex.printStackTrace();
			} finally {
				if (input != null) {
					try {
						input.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		else
			General.println("Could not find any settings.");
	}
	
	private void setSettings() {
		Vars.get().settings = new Settings(true);
	}
	
	@FXML @DoNotRename private String getSaveFile() {
		String settings = new String();
		if (!directory.exists())
			return "No Saved Settings Found";
		else {
			for (File f : directory.listFiles()) {
				if (f.getName().equals(settingsName)) 
					settings = f.getName();
			}
			return settings;
		}
	}
	
	public void setGUI(GUI gui) {
        this.gui = gui;
    }

    public GUI getGUI() {
        return this.gui;
    }
}
