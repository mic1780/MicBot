package MicBot;

public class Command {
	public String command;
	public int permission = 0;
	public String output = "";
	
	public Command() { }
	
	public Command(String command, int permission, String output) {
		this.command = command;
		this.permission = permission;
		this.output = output;
	}
}
