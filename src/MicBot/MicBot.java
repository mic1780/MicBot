package MicBot;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.jibble.pircbot.*;

public class MicBot extends PircBot {
	
	//Define constants for the files we will be using
	
	
	public final static boolean STANDALONE = true;
	public final static String TRIGGER = "!";
	public ArrayList<String> modList = new ArrayList<String>();
	public ArrayList<String> channelMap = new ArrayList<String>();
	public static HashMap<String, HashMap<String, Command>> commandMap = new HashMap<String, HashMap<String, Command>>();
	public static HashMap<String, ArrayList<ActionListener>> listenerMap = new HashMap<String, ArrayList<ActionListener>>();
	
	public MicBot() {
		//enter the name of the twitch user you want this bot to use.
		this.setName("");
		
		listenerMap.put("onBotJoin", new ArrayList<ActionListener>());
		listenerMap.put("onBotLeave", new ArrayList<ActionListener>());
		listenerMap.put("onCommand", new ArrayList<ActionListener>());
		listenerMap.put("onMessage", new ArrayList<ActionListener>());
		
		loadAddons();
		
	}
	
	public void subscribe(String event, ActionListener act) {
		try {
			listenerMap.get(event).add(act);
		} catch (NullPointerException e) {
			System.out.println(event + " is not a valid listener. Subscribe failed.");
			e.printStackTrace();
		}
	}
	
	public void trigger(String event, String[] arguments) {
		try {
			String args = "";
			for (String arg : arguments) {
				args +=	arg + "``";
			}//END FOR LOOP
			args = args.substring(0, Math.max(args.length() - 2, 0));
			
			for (ActionListener listener : listenerMap.get(event))
				listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, args));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void onMessage(String channel, String sender,
			String login, String hostname, String message) {
		if (STANDALONE == false) {
			if (channel.equalsIgnoreCase("#mic_bot")) {
				if (message.equalsIgnoreCase("!join")) {
					botJoinChannel("#" + sender);
					sendMessage(channel, "Bot has joined channel '" + sender + "'. Make sure to make it a moderator so it can function properly.");
					return;
				}//END IF
			}//END IF
		}//END IF
		if (message.equalsIgnoreCase(TRIGGER + "bot"))
			sendMessage(channel, "Currently running Mic_Bot.");
		else if (message.startsWith(TRIGGER))
			trigger("onCommand", new String[]{channel, sender, message});
	}
	
	protected void onUserMode(String targetNick, String sourceNick,
			String sourceLogin, String sourceHostname, String mode) {
		if (mode.contains("+o")) {
			String channel = mode.toLowerCase().split(" ")[0];
			String user = mode.toLowerCase().split(" ")[2];
//			System.out.println(mode);
			if (modList.contains(user.toLowerCase() + channel) == false)
				modList.add(user.toLowerCase() + channel);
		}//END IF
	}

	public static HashMap<String, Object> objectMap = new HashMap<String, Object>();
	private void loadAddons() {
		ArrayList<Class> addons = new ArrayList<Class>();
		addons.addAll(getAddons());
		int len = addons.size();
		
		for (int i = 0; i < len; i++) {
			System.out.println("Loading addon: " + addons.get(i).getName());
			try {
				objectMap.put(addons.get(i).getName().toLowerCase(), MicBot.class.getClassLoader().loadClass(addons.get(i).getName()).getConstructor(MicBot.class).newInstance(this));
			} catch (InstantiationException e) {
				System.out.println("Instantiation Exception: Failed to load addon " + addons.get(i).getName());
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				System.out.println("Illegal Access Exception: Failed to load addon " + addons.get(i).getName());
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				System.out.println("Invocation Target Exception: Failed to load addon " + addons.get(i).getName());
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				System.out.println("No Such Method Exception: Failed to load addon " + addons.get(i).getName());
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				System.out.println("Class Not Found Exception: Failed to load addon " + addons.get(i).getName());
				e.printStackTrace();
			}
		}
	}
	
	private List<Class> getAddons() {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		assert classLoader != null;
		Enumeration<URL> resources = null;
		try {
			resources = classLoader.getResources("MicBot/Addons");
		} catch (IOException e) {
			e.printStackTrace();
		}
		List<File> dirs = new ArrayList<File>();
		while (resources.hasMoreElements()) {
			URL res = resources.nextElement();
			dirs.add(new File(res.getFile()));
		}//END WHILE LOOP
		
		List<Class> classes = new ArrayList<Class>();
		for (File dir : dirs) {
			File[] files = dir.listFiles();
			for (File fi : files) {
				if (fi.isDirectory())
					continue;
				try {
					if (fi.getName().endsWith(".class") && fi.getName().contains("$") == false) {
						classes.add(Class.forName("MicBot.Addons." + fi.getName().substring(0, fi.getName().length() - 6)));
					}//END IF
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}//END FOR LOOP
		}//END FOR LOOP
		
		return classes;
	}
	
	public boolean botJoinChannel(String name) {
		if (channelMap.contains(name.toLowerCase()))
			return false;
		channelMap.add(name.toLowerCase());
		if (commandMap.containsKey(name.toLowerCase()) == false)
			commandMap.put(name.toLowerCase(), new HashMap<String, Command>());
		this.joinChannel(name.toLowerCase());
		if (STANDALONE == false)
			sendMessage(name, "Hi! I'm Mic_Bot! I am a chat moderation bot currently in development by Mic1780!");
		trigger("onBotJoin", new String[]{name.toLowerCase()});
		return true;
	}
	
	public boolean botLeaveChannel(String name) {
		if (! channelMap.contains(name.toLowerCase()))
			return false;
		
		partChannel(name);
		channelMap.remove(name.toLowerCase());
		trigger("onBotLeave", new String[]{name.toLowerCase()});
		return true;
	}
	
	public boolean isMod(String channel, String user) {
		return modList.contains(user.toLowerCase() + channel);
	}
	
}
