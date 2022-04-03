package me.TheTealViper.chatbubbles.implentations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.massivecraft.factions.entity.MPlayer;

import me.TheTealViper.chatbubbles.ChatBubbles;
import me.TheTealViper.chatbubbles.placeholderShit;
import net.md_5.bungee.api.ChatColor;

public class HolographicDisplaysImplementation {
	public static ChatBubbles plugin;
	public Map<UUID, List<Hologram>> existingHolograms = new HashMap<UUID, List<Hologram>>();

	public void handleZero(String message, Player p){
		boolean requirePerm = plugin.getConfig().getBoolean("ConfigZero_Require_Permissions");
		String usePerm = plugin.getConfig().getString("ConfigZero_Use_Permission");
		String seePerm = plugin.getConfig().getString("ConfigZero_See_Permission");
		if(requirePerm && !p.hasPermission(usePerm))
			return;
		if(!plugin.togglePF.getBoolean(p.getUniqueId().toString()))
			return;
		if(existingHolograms.containsKey(p.getUniqueId())) {
			for(Hologram h : existingHolograms.get(p.getUniqueId())) {
				if(!h.isDeleted())
					h.delete();
			}
		}
		final Hologram hologram = HologramsAPI.createHologram(plugin, p.getLocation().add(0.0, plugin.bubbleOffset, 0.0));
		List<Hologram> hList = new ArrayList<Hologram>();
		hList.add(hologram);
		existingHolograms.put(p.getUniqueId(), hList);
		hologram.getVisibilityManager().setVisibleByDefault(false);
		for(Player oP : Bukkit.getOnlinePlayers()){
			if(((plugin.seeOwnBubble) || (!plugin.seeOwnBubble && oP.getName() != p.getName())) 
					&& (oP.getWorld().getName().equals(p.getWorld().getName()) 
					&& oP.getLocation().distance(p.getLocation()) <= plugin.distance) 
					&& (!requirePerm || (requirePerm && oP.hasPermission(seePerm)))
					&& oP.canSee(p))
				hologram.getVisibilityManager().showTo(oP);
		}
		int lines = formatHologramLines(p, hologram, message);

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
					plugin.getServer().getConsoleSender().sendMessage("Something is wrong in your ChatBubble config.yml sound settings!");
					plugin.getServer().getConsoleSender().sendMessage("Please ensure that 'ChatBubble_Sound_Name' works in a '/playsound' command test.");
				}
			}
		}
	}
	
	public void handleOne(String message, Player p){
		boolean sendOriginal = plugin.getConfig().getBoolean("ChatBubble_Send_Original_Message");
		boolean requirePerm = plugin.getConfig().getBoolean("ConfigOne_Require_Permissions");
		String usePerm = plugin.getConfig().getString("ConfigOne_Use_Permission");
		String seePerm = plugin.getConfig().getString("ConfigOne_See_Permission");
		if(requirePerm && !p.hasPermission(usePerm))
			return;
		if(!plugin.togglePF.getBoolean(p.getUniqueId().toString()))
			return;
		if(existingHolograms.containsKey(p.getUniqueId())) {
			for(Hologram h : existingHolograms.get(p.getUniqueId())) {
				if(!h.isDeleted())
					h.delete();
			}
		}
		final Hologram hologram = HologramsAPI.createHologram(plugin, p.getLocation().add(0.0, plugin.bubbleOffset, 0.0));
		List<Hologram> hList = new ArrayList<Hologram>();
		hList.add(hologram);
		existingHolograms.put(p.getUniqueId(), hList);
		hologram.getVisibilityManager().setVisibleByDefault(false);
		for(Player oP : Bukkit.getOnlinePlayers()){
			if(((plugin.seeOwnBubble) || (!plugin.seeOwnBubble && oP.getName() != p.getName())) 
					&& (oP.getWorld().getName().equals(p.getWorld().getName()) 
					&& oP.getLocation().distance(p.getLocation()) <= plugin.distance) 
					&& (!requirePerm || (requirePerm && oP.hasPermission(seePerm)))
					&& oP.canSee(p))
				hologram.getVisibilityManager().showTo(oP);
		}
		int lines = formatHologramLines(p, hologram, message);
		if(sendOriginal)
			p.chat(message);

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
					plugin.getServer().getConsoleSender().sendMessage("Something is wrong in your ChatBubble config.yml sound settings!");
					plugin.getServer().getConsoleSender().sendMessage("Please ensure that 'ChatBubble_Sound_Name' works in a '/playsound' command test.");
				}
			}
		}
	}
	
	public void handleTwo(String message, Player p){
		boolean sendOriginal = plugin.getConfig().getBoolean("ChatBubble_Send_Original_Message");
		if(!plugin.togglePF.getBoolean(p.getUniqueId().toString()))
			return;
		if(existingHolograms.containsKey(p.getUniqueId())) {
			for(Hologram h : existingHolograms.get(p.getUniqueId())) {
				if(!h.isDeleted())
					h.delete();
			}
		}
		String permGroup = "";
		for(String testPerm : plugin.getConfig().getStringList("ConfigTwo_Permission_Groups")){
			if(p.hasPermission(testPerm)){
				permGroup = testPerm;
				break;
			}
		}
		if(permGroup.equals(""))
			return;
		final Hologram hologram = HologramsAPI.createHologram(plugin, p.getLocation().add(0.0, plugin.bubbleOffset, 0.0));
		List<Hologram> hList = new ArrayList<Hologram>();
		hList.add(hologram);
		existingHolograms.put(p.getUniqueId(), hList);
		hologram.getVisibilityManager().setVisibleByDefault(false);
		for(Player oP : Bukkit.getOnlinePlayers()){
			if(((plugin.seeOwnBubble) || (!plugin.seeOwnBubble && oP.getName() != p.getName())) 
					&& (oP.getWorld().getName().equals(p.getWorld().getName()) 
					&& oP.getLocation().distance(p.getLocation()) <= plugin.distance) 
					&& (oP.hasPermission(permGroup))
					&& oP.canSee(p))
				hologram.getVisibilityManager().showTo(oP);
		}
		int lines = formatHologramLines(p, hologram, message);
		if(sendOriginal)
			p.chat(message);

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
					plugin.getServer().getConsoleSender().sendMessage("Something is wrong in your ChatBubble config.yml sound settings!");
					plugin.getServer().getConsoleSender().sendMessage("Please ensure that 'ChatBubble_Sound_Name' works in a '/playsound' command test.");
				}
			}
		}
	}
	
	public void handleThree(String message, Player p){
		boolean sendOriginal = plugin.getConfig().getBoolean("ChatBubble_Send_Original_Message");
		if(existingHolograms.containsKey(p.getUniqueId())) {
			for(Hologram h : existingHolograms.get(p.getUniqueId())) {
				if(!h.isDeleted())
					h.delete();
			}
		}
		MPlayer mPlayer = MPlayer.get(p);
		String faction = mPlayer.getFactionName();
		final Hologram hologram = HologramsAPI.createHologram(plugin, p.getLocation().add(0.0, plugin.bubbleOffset, 0.0));
		List<Hologram> hList = new ArrayList<Hologram>();
		hList.add(hologram);
		existingHolograms.put(p.getUniqueId(), hList);
		hologram.getVisibilityManager().setVisibleByDefault(false);
		for(Player oP : Bukkit.getOnlinePlayers()){
			if(((plugin.seeOwnBubble) || (!plugin.seeOwnBubble && oP.getName() != p.getName())) 
					&& (oP.getWorld().getName().equals(p.getWorld().getName()) 
					&& oP.getLocation().distance(p.getLocation()) <= plugin.distance) 
					&& (MPlayer.get(oP).getFactionName().equals(faction))
					&& oP.canSee(p))
				hologram.getVisibilityManager().showTo(oP);
		}
		int lines = formatHologramLines(p, hologram, message);
		if(sendOriginal)
			p.chat(message);

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
					plugin.getServer().getConsoleSender().sendMessage("Something is wrong in your ChatBubble config.yml sound settings!");
					plugin.getServer().getConsoleSender().sendMessage("Please ensure that 'ChatBubble_Sound_Name' works in a '/playsound' command test.");
				}
			}
		}
	}
	
	public void handleFour(String message, Player p){
		if(!plugin.togglePF.getBoolean(p.getUniqueId().toString()))
			return;
		
		if(plugin.getConfig().getBoolean("ChatBubble_Play_Sound")) {
			String sound = plugin.getConfig().getString("ChatBubble_Sound_Name").toLowerCase();
			float volume = (float) plugin.getConfig().getDouble("ChatBubble_Sound_Volume");
			if(!sound.equals("")) {
				try {
					p.getWorld().playSound(p.getLocation(), sound, volume, 1.0f);
				}catch(Exception e) {
					plugin.getServer().getConsoleSender().sendMessage("Something is wrong in your ChatBubble config.yml sound settings!");
					plugin.getServer().getConsoleSender().sendMessage("Please ensure that 'ChatBubble_Sound_Name' works in a '/playsound' command test.");
				}
			}
		}
	}
	
	public void handleFive(String message, Player p){
		boolean sendOriginal = false;
		boolean requirePerm = plugin.getConfig().getBoolean("ConfigFive_Require_Permissions");
		String usePerm = plugin.getConfig().getString("ConfigFive_Use_Permission");
		String seePerm = plugin.getConfig().getString("ConfigFive_See_Permission");
		if(requirePerm && !p.hasPermission(usePerm))
			return;
		if(!plugin.togglePF.getBoolean(p.getUniqueId().toString()))
			return;
		if(existingHolograms.containsKey(p.getUniqueId())) {
			for(Hologram h : existingHolograms.get(p.getUniqueId())) {
				if(!h.isDeleted())
					h.delete();
			}
		}
		final Hologram hologram = HologramsAPI.createHologram(plugin, p.getLocation().add(0.0, plugin.bubbleOffset, 0.0));
		List<Hologram> hList = new ArrayList<Hologram>();
		hList.add(hologram);
		existingHolograms.put(p.getUniqueId(), hList);
		hologram.getVisibilityManager().setVisibleByDefault(false);
		for(Player oP : Bukkit.getOnlinePlayers()){
			if(((plugin.seeOwnBubble) || (!plugin.seeOwnBubble && oP.getName() != p.getName())) 
					&& (oP.getWorld().getName().equals(p.getWorld().getName()) 
					&& oP.getLocation().distance(p.getLocation()) <= plugin.distance) 
					&& (!requirePerm || (requirePerm && oP.hasPermission(seePerm)))
					&& oP.canSee(p))
				hologram.getVisibilityManager().showTo(oP);
		}
		int lines = formatHologramLines(p, hologram, message);
		if(sendOriginal)
			p.chat(message);

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
					plugin.getServer().getConsoleSender().sendMessage("Something is wrong in your ChatBubble config.yml sound settings!");
					plugin.getServer().getConsoleSender().sendMessage("Please ensure that 'ChatBubble_Sound_Name' works in a '/playsound' command test.");
				}
			}
		}
	}
	
	public int formatHologramLines(LivingEntity e, Hologram hologram, String message){
		List<String> lineList = new ArrayList<String>();
		for(String formatLine : plugin.getConfig().getStringList("ChatBubble_Message_Format")){
			boolean addedToLine = false;
			if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
				formatLine = placeholderShit.formatString(e, formatLine);
			if(formatLine.contains("%chatbubble_message%")){
				addedToLine = true;
				formatLine = formatLine.replace("%chatbubble_message%", message);
				
				for(String s : formatLine.split(" ")){
					if(s.length() > plugin.length){
						String insert = "-\n";
						int period = plugin.length - 1;
						StringBuilder builder = new StringBuilder(
						         s.length() + insert.length() * (s.length()/plugin.length)+1);

						    int index = 0;
						    String prefix = "";
						    while (index < s.length())
						    {
						        // Don't put the insert in the very first iteration.
						        // This is easier than appending it *after* each substring
						        builder.append(prefix);
						        prefix = insert;
						        builder.append(s.substring(index, 
						            Math.min(index + period, s.length())));
						        index += period;
						    }
						String replacement = builder.toString();
						formatLine = formatLine.replace(s, replacement);
						message = message.replace(s, replacement);
					}
				}
				
				StringBuilder sb = new StringBuilder(formatLine.replace(message, "") + message);
				int i = 0;
				while (i + plugin.length < sb.length() && (i = sb.lastIndexOf(" ", i + plugin.length)) != -1) {
				    sb.replace(i, i + 1, "\n");
				}
				for(String s : sb.toString().split("\\n")){
					if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
						s = ChatBubbles.makeColors(s);
						if(plugin.getConfig().getBoolean("ChatBubble_Strip_Formatting"))
							s = ChatColor.stripColor(s);
						s = placeholderShit.formatString(e, plugin.prefix + s + plugin.suffix);
						s = ChatBubbles.makeColors(s);
						lineList.add(s);
					} else {
						s = ChatBubbles.makeColors(s);
						if(plugin.getConfig().getBoolean("ChatBubble_Strip_Formatting"))
							s = ChatColor.stripColor(s);
						s = ChatBubbles.makeColors(plugin.prefix + s + plugin.suffix);
						lineList.add(s);
					}
				}
			}
			if(!addedToLine) {
				if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
					formatLine = ChatBubbles.makeColors(formatLine);
					if(plugin.getConfig().getBoolean("ChatBubble_Strip_Formatting"))
						formatLine = ChatColor.stripColor(formatLine);
					formatLine = placeholderShit.formatString(e, plugin.prefix + formatLine + plugin.suffix);
					formatLine = ChatBubbles.makeColors(formatLine);
					lineList.add(formatLine);
				}	else {
					formatLine = ChatBubbles.makeColors(formatLine);
					if(plugin.getConfig().getBoolean("ChatBubble_Strip_Formatting"))
						formatLine = ChatColor.stripColor(formatLine);
					formatLine = ChatBubbles.makeColors(plugin.prefix + formatLine + plugin.suffix);
					lineList.add(formatLine);
				}
			}
		}
		for(String s : lineList)
			hologram.appendTextLine(s);
		return lineList.size();
	}
	
	public void onQuit(UUID uuid) {
		existingHolograms.remove(uuid);
	}
	
}
