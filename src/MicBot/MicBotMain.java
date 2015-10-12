package MicBot;

import org.jibble.pircbot.*;

public class MicBotMain {

	public static void main(String[] args) throws Exception {
		//start up the bot
		MicBot bot = new MicBot();
		
		//enable debugging output.
		bot.setVerbose(true);
		
		// Connect to the IRC server.
		bot.connect("irc.twitch.tv", 6667, "oauth:");
		bot.sendRawLine("CAP REQ :twitch.tv/membership");
		
		//Join a channel.
		bot.botJoinChannel("#");
	}

}
