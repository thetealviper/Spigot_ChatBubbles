package me.TheTealViper.chatbubbles.citizens;

import org.bukkit.entity.LivingEntity;

import me.TheTealViper.chatbubbles.ChatBubbles;

public class CitizensDHChatbubble {
	
	private ChatBubbles plugin;
	
	public CitizensDHChatbubble(ChatBubbles main) {
		this.plugin = main;
	}
	
	public void createBubbleDH(LivingEntity le, String msg) {
		plugin.DHI.handleHologram(msg, le, -1);
	}
}
    