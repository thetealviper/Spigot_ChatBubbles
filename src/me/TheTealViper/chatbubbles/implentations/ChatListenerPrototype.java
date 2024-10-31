package me.TheTealViper.chatbubbles.implentations;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.TheTealViper.chatbubbles.ChatBubbles;
import me.TheTealViper.chatbubbles.thirdparty.RegexpGenerator;

public class ChatListenerPrototype {
	private static List<Pattern> BlacklistRegexList = new ArrayList<Pattern>();

	public static void RegisterBlacklist (ChatBubbles plugin) {
		RegexpGenerator RG = new RegexpGenerator();
		for (String word : plugin.getConfig().getStringList("ChatBubble_Filter_List")) {
			String regexStr = RG.generateRegexp(word);
			BlacklistRegexList.add(Pattern.compile(regexStr));
		}
	}

	public static void onChat(ChatBubbles plugin, AsyncPlayerChatEvent e) {
		if(e.isCancelled() || e.getPlayer().getGameMode().name().equals(GameMode.SPECTATOR.name()))
			return;
		//Handle message overrides
		String messageOverride = e.getMessage();
		if (plugin.getConfig().getBoolean("ChatBubble_Enable_Filtering"))
			messageOverride = replaceBlacklist(e.getMessage());
		if (plugin.getConfig().contains("ChatBubble_Length_Limit") && plugin.getConfig().getInt("ChatBubble_Length_Limit") > 0 && messageOverride.length() > plugin.getConfig().getInt("ChatBubble_Length_Limit")) {
			messageOverride = messageOverride.substring(0, plugin.getConfig().getInt("ChatBubble_Length_Limit")) + plugin.getConfig().getString("ChatBubble_Length_Suffix");
		}
		if (messageOverride.length() == 0)
			return;
		//Handle message
		switch (plugin.getConfig().getInt("ChatBubble_Configuration_Mode")){
			case 0:
				//If player has manually toggled to disable the hologram functionality
				if(!plugin.togglePF.getBoolean(e.getPlayer().getUniqueId().toString())) return;
				if(!plugin.getConfig().getBoolean("ChatBubble_Send_Original_Message")) { //This MUST be handled here. If handled by manual player.chat() leads to infinite recursion onPlayerChatEvent
					e.setCancelled(true);
					plugin.getLogger().info("[ChatBubbles Cancelled Message]<" + e.getPlayer().getDisplayName() + ">" + e.getMessage()); //If we cancel the message, still send it to log for mod reasons
				}
				plugin.handleZero(messageOverride, e.getPlayer());
				break;
			case 1:
				//This is handled in the command event
				break;
			case 2:
				//If player has manually toggled to disable the hologram functionality
				if(!plugin.togglePF.getBoolean(e.getPlayer().getUniqueId().toString())) return;
				if(!plugin.getConfig().getBoolean("ChatBubble_Send_Original_Message")) { //This MUST be handled here. If handled by manual player.chat() leads to infinite recursion onPlayerChatEvent
					e.setCancelled(true);
					plugin.getLogger().info("[ChatBubbles Cancelled Message]<" + e.getPlayer().getDisplayName() + ">" + e.getMessage()); //If we cancel the message, still send it to log for mod reasons
				}
				plugin.handleTwo(messageOverride, e.getPlayer());
				break;
			case 3:
				if(Bukkit.getServer().getPluginManager().getPlugin("Factions") != null) {
					//If player has manually toggled to disable the hologram functionality
					if(!plugin.togglePF.getBoolean(e.getPlayer().getUniqueId().toString())) return;
					if(!plugin.getConfig().getBoolean("ChatBubble_Send_Original_Message")) { //This MUST be handled here. If handled by manual player.chat() leads to infinite recursion onPlayerChatEvent
						e.setCancelled(true);
						plugin.getLogger().info("[ChatBubbles Cancelled Message]<" + e.getPlayer().getDisplayName() + ">" + e.getMessage()); //If we cancel the message, still send it to log for mod reasons
					}
					plugin.handleThree(messageOverride, e.getPlayer());
				}else{
					plugin.getServer().getConsoleSender().sendMessage("ChatBubbles is set to configuration mode 3 but Factions can't be found!");
				}
				break;
			case 4:
				//If player has manually toggled to disable the hologram functionality
				if(!plugin.togglePF.getBoolean(e.getPlayer().getUniqueId().toString())) return;
				if(!plugin.getConfig().getBoolean("ChatBubble_Send_Original_Message")) { //This MUST be handled here. If handled by manual player.chat() leads to infinite recursion onPlayerChatEvent
					e.setCancelled(true);
					plugin.getLogger().info("[ChatBubbles Cancelled Message]<" + e.getPlayer().getDisplayName() + ">" + e.getMessage()); //If we cancel the message, still send it to log for mod reasons
				}
				plugin.handleFour(messageOverride, e.getPlayer());
				break;
			case 5:
				// Find out if message starts with config.yml prefixes, and if so which one
				List<String> prefixes = plugin.getConfig().getStringList("ConfigFive_Prefix_Characters");
				String foundPrefix = "";
				for(String prefix : prefixes) {
					if(!foundPrefix.equals("")) continue;
					if(messageOverride.startsWith(prefix)) {
						foundPrefix = prefix;
					}
				}
				//If message starts with a prefix
				if(!foundPrefix.equals("")) {
					//If player has manually toggled to disable the hologram functionality
					if(!plugin.togglePF.getBoolean(e.getPlayer().getUniqueId().toString())) return;
					if(!plugin.getConfig().getBoolean("ChatBubble_Send_Original_Message")) { //This MUST be handled here. If handled by manual player.chat() leads to infinite recursion onPlayerChatEvent
						e.setCancelled(true);
						plugin.getLogger().info("[ChatBubbles Cancelled Message]<" + e.getPlayer().getDisplayName() + ">" + e.getMessage()); //If we cancel the message, still send it to log for mod reasons
					}
					e.setMessage(e.getMessage().substring(foundPrefix.length())); //This is uniquely necessary to this config mode 5
					messageOverride = messageOverride.substring(foundPrefix.length());
					plugin.handleFive(messageOverride, e.getPlayer());
				}
				break;
			case 6:
				List<String> prefixList = plugin.getConfig().getStringList("ConfigSix_Prefix_Characters");
				// Check if message is prefixed to not be in ChatBubble
				String prefix = "";
				for(String pref : prefixList) {
					if(!prefix.equals("")) continue;
					if(messageOverride.startsWith(pref)) {
						prefix = pref;
					}
				}
				// If the message wasn't prefixed to be ignored, send the ChatBubble
				if(!prefix.equals("")) e.setMessage(e.getMessage().substring(prefix.length())); //This is necessary to remove prefix from message in chat
				else {
					//If player has manually toggled to disable the hologram functionality
					if (!plugin.togglePF.getBoolean(e.getPlayer().getUniqueId().toString())) return;
					// Cancel the regular message from sending since not prefixed
					e.setCancelled(true);
					plugin.getLogger().info("[ChatBubbles Cancelled Message]<" + e.getPlayer().getDisplayName() + ">" + e.getMessage()); //If we cancel the message, still send it to log for mod reasons
					plugin.handleSix(messageOverride, e.getPlayer());
				}
				break;
			default:
				break;
		}
	}



	public static String replaceBlacklist (String message) {
		String lowercaseString = message.toLowerCase();
		String modifiedString = "";
		int checkpointIndex = 0;
		for (Pattern p : BlacklistRegexList) {
			Matcher m = p.matcher(lowercaseString);
			while (m.find() && checkpointIndex < message.length()) {
				int findStart = m.start();
				int findEnd = m.end();
				modifiedString += message.substring(checkpointIndex, findStart);
				checkpointIndex = findEnd;
			}
		}
		modifiedString += message.substring(checkpointIndex, message.length());
		return modifiedString;
	}

}
