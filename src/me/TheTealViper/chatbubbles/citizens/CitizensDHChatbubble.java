package me.TheTealViper.chatbubbles.citizens;

import org.bukkit.entity.LivingEntity;

import me.TheTealViper.chatbubbles.ChatBubbles;

public class CitizensDHChatbubble {
	
	private ChatBubbles plugin;
	
	public CitizensDHChatbubble(ChatBubbles main) {
		this.plugin = main;
	}
	
	public void createBubbleDH(LivingEntity le, String msg) {
		createBubbleDH(le, msg, "");
	}
	public void createBubbleDH(LivingEntity le, String msg, String soundOverride) {
		plugin.DHI.handleHologram(msg, le, -1, soundOverride);
	}
}
    