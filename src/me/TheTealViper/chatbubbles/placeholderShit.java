package me.TheTealViper.chatbubbles;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;

public class placeholderShit {
	public static String formatString(Player p, String s){
		return PlaceholderAPI.setPlaceholders(p, s);
	}
}
