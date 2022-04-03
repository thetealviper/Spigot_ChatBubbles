package me.TheTealViper.chatbubbles.citizens;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import me.TheTealViper.chatbubbles.ChatBubbles;

@SuppressWarnings("deprecation")
//Should probably update all HD classes at some point to conform with new methods
public class CitizensHDChatbubble {
	
	private ChatBubbles plugin;
	private boolean requirePerm;
	private String seePerm;
	
	public CitizensHDChatbubble(ChatBubbles main) {
		this.plugin = main;
		this.requirePerm = plugin.getConfig().getBoolean("Citizens_Bubbles_Require_See_Permission");
		this.seePerm = plugin.getConfig().getString("Citizens_Bubbles_See_Permission");
	}
	
	public void createBubbleHD(LivingEntity p, String msg) {
		if(plugin.HDI.existingHolograms.containsKey(p.getUniqueId())) {
			for(Hologram h : plugin.HDI.existingHolograms.get(p.getUniqueId())) {
				if(!h.isDeleted())
					h.delete();
			}
		}
		final Hologram hologram = HologramsAPI.createHologram(plugin, p.getLocation().add(0.0, plugin.bubbleOffset, 0.0));
		List<Hologram> hList = new ArrayList<Hologram>();
		hList.add(hologram);
		plugin.HDI.existingHolograms.put(p.getUniqueId(), hList);
		hologram.getVisibilityManager().setVisibleByDefault(false);
		for(Player oP : Bukkit.getOnlinePlayers()){
			if(((plugin.seeOwnBubble) || (!plugin.seeOwnBubble && oP.getName() != p.getName())) 
					&& (oP.getWorld().getName().equals(p.getWorld().getName()) 
					&& oP.getLocation().distance(p.getLocation()) <= plugin.distance) 
					&& (!requirePerm || (requirePerm && oP.hasPermission(seePerm)))
					&& oP.canSee(p))
				hologram.getVisibilityManager().showTo(oP);
		}
		int lines = plugin.HDI.formatHologramLines(p, hologram, msg);

		new BukkitRunnable() {
			int ticksRun = 0;
			@Override
			public void run() {
				ticksRun++;
				if(!hologram.isDeleted())
					hologram.teleport(p.getLocation().add(0.0, plugin.bubbleOffset + .25 * lines, 0.0));
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
					p.getWorld().playSound(p.getLocation(), sound, volume, 1.0f);
				}catch(Exception e) {
					Bukkit.getServer().getConsoleSender().sendMessage("Something is wrong in your ChatBubble config.yml sound settings!");
					Bukkit.getServer().getConsoleSender().sendMessage("Please ensure that 'ChatBubble_Sound_Name' works in a '/playsound' command test.");
				}
			}
		}
	}
}
    