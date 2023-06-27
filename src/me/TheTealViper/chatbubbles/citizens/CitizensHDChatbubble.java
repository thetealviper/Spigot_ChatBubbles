package me.TheTealViper.chatbubbles.citizens;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.TheTealViper.chatbubbles.ChatBubbles;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import me.filoghost.holographicdisplays.api.hologram.VisibilitySettings.Visibility;

public class CitizensHDChatbubble {
	
	private ChatBubbles plugin;
	private boolean requirePerm;
	private String seePerm;
	
	public CitizensHDChatbubble(ChatBubbles main) {
		this.plugin = main;
		this.requirePerm = plugin.getConfig().getBoolean("Citizens_Bubbles_Require_See_Permission");
		this.seePerm = plugin.getConfig().getString("Citizens_Bubbles_See_Permission");
	}
	
	public void createBubbleHD(LivingEntity le, String msg) {
		if(plugin.HDI.existingHolograms.containsKey(le.getUniqueId())) {
			for(Hologram h : plugin.HDI.existingHolograms.get(le.getUniqueId())) {
				if(!h.isDeleted())
					h.delete();
			}
		}
		HolographicDisplaysAPI HDAPI = HolographicDisplaysAPI.get(plugin);
		final Hologram hologram = HDAPI.createHologram(le.getLocation().add(0.0, plugin.bubbleOffset, 0.0));
		List<Hologram> hList = new ArrayList<Hologram>();
		hList.add(hologram);
		plugin.HDI.existingHolograms.put(le.getUniqueId(), hList);
		hologram.getVisibilitySettings().setGlobalVisibility(Visibility.HIDDEN);
		for(Player oP : Bukkit.getOnlinePlayers()){
			if(((plugin.seeOwnBubble) || (!plugin.seeOwnBubble && oP.getName() != le.getName())) 
					&& (oP.getWorld().getName().equals(le.getWorld().getName()) 
					&& oP.getLocation().distance(le.getLocation()) <= plugin.distance) 
					&& (!requirePerm || (requirePerm && oP.hasPermission(seePerm)))
					&& oP.canSee(le))
				hologram.getVisibilitySettings().setIndividualVisibility(oP, Visibility.VISIBLE);
		}
		int lines = plugin.HDI.formatHologramLines(le, hologram, msg);

		new BukkitRunnable() {
			int ticksRun = 0;
			@Override
			public void run() {
				ticksRun++;
				if(!hologram.isDeleted())
					hologram.setPosition(le.getLocation().add(0.0, plugin.bubbleOffset + .25 * lines, 0.0));
				if (ticksRun > plugin.life) {
					hologram.delete();
					cancel();
				}
		}}.runTaskTimer(plugin, 1L, 1L);
		
		if(plugin.getConfig().getBoolean("ChatBubble_Play_Sound")) {
			String sound = plugin.getConfig().getString("ChatBubble_Sound_Name").toLowerCase();
			float volume = (float) plugin.getConfig().getDouble("ChatBubble_Sound_Volume");
			if(!sound.equals("")) {
				try {
					le.getWorld().playSound(le.getLocation(), sound, volume, 1.0f);
				}catch(Exception e) {
					Bukkit.getServer().getConsoleSender().sendMessage("Something is wrong in your ChatBubble config.yml sound settings!");
					Bukkit.getServer().getConsoleSender().sendMessage("Please ensure that 'ChatBubble_Sound_Name' works in a '/playsound' command test.");
				}
			}
		}
	}
}
    