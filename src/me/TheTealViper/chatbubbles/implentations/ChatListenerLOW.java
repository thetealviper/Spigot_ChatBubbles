package me.TheTealViper.chatbubbles.implentations;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.TheTealViper.chatbubbles.ChatBubbles;

public class ChatListenerLOW implements Listener {
	public static ChatBubbles plugin;
	
	@EventHandler(priority = EventPriority.LOW)
	public void onChat(AsyncPlayerChatEvent e){
		if(e.isCancelled() || e.getPlayer().getGameMode().name().equals(GameMode.SPECTATOR.name()))
			return;
		switch (plugin.getConfig().getInt("ChatBubble_Configuration_Mode")){
		case 0:
			if(!plugin.getConfig().getBoolean("ChatBubble_Send_Original_Message"))
				e.setCancelled(true);
			plugin.handleZero(e.getMessage(), e.getPlayer());
			break;
		case 1:
			//This is handled in the command event
			break;
		case 2:
			if(!plugin.getConfig().getBoolean("ChatBubble_Send_Original_Message"))
				e.setCancelled(true);
			plugin.handleTwo(e.getMessage(), e.getPlayer());
			break;
		case 3:
			if(Bukkit.getServer().getPluginManager().getPlugin("Factions") != null)
				plugin.handleThree(e.getMessage(), e.getPlayer());
			else
				plugin.getServer().getConsoleSender().sendMessage("ChatBubbles is set to configuration mode 3 but Factions can't be found!");
			break;
		case 4:
			plugin.handleFour(e.getMessage(), e.getPlayer());
			break;
		default:
			break;
		}
	}
	
}
