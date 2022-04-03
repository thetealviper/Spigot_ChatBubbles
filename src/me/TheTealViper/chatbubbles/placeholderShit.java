package me.TheTealViper.chatbubbles;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;

public class placeholderShit {
	public static String formatString(LivingEntity e, String s){
		if(e instanceof Player)
			return PlaceholderAPI.setPlaceholders((Player)e, s);
		if(e instanceof OfflinePlayer)
			return PlaceholderAPI.setPlaceholders((OfflinePlayer)e, s);
		return PlaceholderAPI.setPlaceholders(null, s);
	}
}
