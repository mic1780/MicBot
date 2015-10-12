package MicBot.Addons;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import MicBot.MicBot;
import MicBot.Command;
import org.jibble.pircbot.*;

public class AdminCommands {
	public AdminCommands() {}
	
	MicBot core;
	
	public AdminCommands(MicBot core) {
		this.core = core;
		core.subscribe("onCommand", new AddCommand());
		core.subscribe("onCommand", new DoCommand());
	}
	
	private class AddCommand implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			String[] args = e.getActionCommand().split("``");
			String channel = args[0];
			String sender = args[1];
			String msg = args[2];
			
			if (! core.isMod(channel, sender))
				return;
			
			args = msg.substring(1).split(" ", 3);
			if (args[0].equalsIgnoreCase("addcom") == false)
				return;
			
//			for (int i = 0; i < core.commandMap.get(channel).size(); i++) {
			if (core.commandMap.get(channel).containsKey(args[1])) {
				core.sendMessage(channel, args[1] + " is already a command.");
				return;
			}
			
			Command cmd = new Command(args[1].toLowerCase(), 0, args[2]);
			core.commandMap.get(channel).put(args[1].toLowerCase(), cmd);
			core.sendMessage(channel, "Command added successfully.");
		}
	}
	
	private class DoCommand implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			String[] args = e.getActionCommand().split("``");
			String channel = args[0];
			String sender = args[1];
			String msg = args[2];
			
			if (! core.isMod(channel, sender))
				return;
			
			if (core.commandMap.get(channel).containsKey(msg.split(" ")[0].toLowerCase()) == false)
				return;
			
			Command cmd = core.commandMap.get(channel).get(msg.split(" ")[0].toLowerCase());
			core.sendMessage(channel, cmd.output);
		}
	}
}
