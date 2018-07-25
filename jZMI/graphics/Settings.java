package scripts.jZMI.graphics;

//import scripts.jZMI.data.Vars;

public class Settings {
	
	private final boolean setting1;
	
	public Settings( boolean setting) {
		this.setting1 = setting;
	}
	
	public boolean getSetting() {
		return this.setting1;
	}
	
	/*public void setVars() {
		Vars.get().settings = this.setting1;
	}*/
}
