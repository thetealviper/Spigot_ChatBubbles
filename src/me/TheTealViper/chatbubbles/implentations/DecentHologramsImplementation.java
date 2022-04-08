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

import com.massivecraft.factions.entity.MPlayer;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import me.TheTealViper.chatbubbles.ChatBubbles;
import me.TheTealViper.chatbubbles.placeholderShit;
import net.md_5.bungee.api.ChatColor;

public class DecentHologramsImplementation {
	public static ChatBubbles plugin;
	public Map<UUID, List<Hologram>> existingHolograms = new HashMap<UUID, List<Hologram>>();
	
	public void handleZero(String message, Player p){
		handleHologram(message, p, 0);
	}
	
	public void handleOne(String message, Player p){
		handleHologram(message, p, 1);
	}
	
	public void handleTwo(String message, Player p){
		handleHologram(message, p, 2);
	}
	
	public void handleThree(String message, Player p){
		handleHologram(message, p, 3);
	}
	
	public void handleFour(String message, Player p){
		handleHologram(message, p, 4);
	}
	
	public void handleFive(String message, Player p){
		handleHologram(message, p, 5);
	}
	
	public int formatHologramLines(LivingEntity e, Hologram hologram, String message){
		List<String> lineList = new ArrayList<String>();
		for(String formatLine : plugin.getConfig().getStringList("ChatBubble_Message_Format")){
			boolean addedToLine = false;
			if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
				formatLine = placeholderShit.formatString(e, formatLine);
			if(formatLine.contains("%chatbubble_message%")){
				addedToLine = true;
				message = ChatBubbles.makeColors(message);
				if(plugin.getConfig().getBoolean("ChatBubble_Strip_Formatting"))
					message = ChatColor.stripColor(message);
				
				//-----
				//----- Handle strings wrapping numerous lines -----
				//-----
				//To be completely honest, I coded up this disgusting monster at one point to functionality,
				//but I no longer understand how it works at all. Is it overly repetitive? I have no clue.
				//I was confident with it's functionality when I made it so I'm not touching it or thinking too hard.
				for(String s : message.split(" ")){
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
						message = message.replace(s, replacement);
					}
				}
				
				StringBuilder sb = new StringBuilder(formatLine.replace("%chatbubble_message%", message));
				int i = 0;
				while (i + plugin.length < sb.length() && (i = sb.lastIndexOf(" ", i + plugin.length)) != -1) {
				    sb.replace(i, i + 1, "\n");
				}
				for(String s : sb.toString().split("\\n")){
					if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
						s = placeholderShit.formatString(e, plugin.prefix + s + plugin.suffix);
						s = ChatBubbles.makeColors(s);
						lineList.add(s);
					} else {
						s = ChatBubbles.makeColors(plugin.prefix + s + plugin.suffix);
						lineList.add(s);
					}
				}
				// ^ Now text wraps lines. Tada.
			}
			if(!addedToLine) {
				//If a message is to be implanted, formatting will already have happened.
				//If no player message was formatted in on this line, then formatting must still happen.
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
		DHAPI.setHologramLines(hologram, lineList);
		//for(String s : lineList)
		//	hologram.appendTextLine(s);
		return lineList.size();
	}
	
	public void onQuit(UUID uuid) {
		existingHolograms.remove(uuid);
	}
	
	public void handleHologram(String message, LivingEntity le, int configMode) {
		//-----
		//----- Declare Vars -----
		//-----
		boolean /*sendOriginal = false,*/ isSoundOnly = false, requirePerm = false, citizensShowToAll = false;
		String permGroup = null, usePerm = null, seePerm = null, factionName = null;
		
		//-----
		//----- Initialize Vars -----
		//-----
		switch (configMode) {
		case -1:
			//This case is for Citizens NPCs
			//sendOriginal = false; //Setting this false here prevents a potential double message because the NPC chat event isn't cancelled unlike the players'
			isSoundOnly = false; //Only true for configMode = 4
			requirePerm = false;
			citizensShowToAll = true; //Only applicable for configMode = -1
			permGroup = null; //Only applicable for configMode = 2
			usePerm = null;
			seePerm = null;
			factionName = null; //Only applicable for configMode = 3
			break;
		case 0:
			//sendOriginal = plugin.getConfig().getBoolean("ChatBubble_Send_Original_Message"); //Must be handled within ChatListenerPrototype bc infinite recursion
			isSoundOnly = false; //Only true for configMode = 4
			requirePerm = plugin.getConfig().getBoolean("ConfigZero_Require_Permissions");
			citizensShowToAll = false; //Only applicable for configMode = -1
			permGroup = null; //Only applicable for configMode = 2
			usePerm = plugin.getConfig().getString("ConfigZero_Use_Permission");
			seePerm = plugin.getConfig().getString("ConfigZero_See_Permission");
			factionName = null; //Only applicable for configMode = 3
			break;
		case 1:
			//sendOriginal = plugin.getConfig().getBoolean("ChatBubble_Send_Original_Message"); //Must be handled within ChatListenerPrototype bc infinite recursion
			isSoundOnly = false; //Only true for configMode = 4
			requirePerm = plugin.getConfig().getBoolean("ConfigOne_Require_Permissions");
			citizensShowToAll = false; //Only applicable for configMode = -1
			permGroup = null; //Only applicable for configMode = 2
			usePerm = plugin.getConfig().getString("ConfigOne_Use_Permission");
			seePerm = plugin.getConfig().getString("ConfigOne_See_Permission");
			factionName = null; //Only applicable for configMode = 3
			break;
		case 2:
			//sendOriginal = plugin.getConfig().getBoolean("ChatBubble_Send_Original_Message"); //Must be handled within ChatListenerPrototype bc infinite recursion
			isSoundOnly = false; //Only true for configMode = 4
			requirePerm = false; //Permission group overrides this
			citizensShowToAll = false; //Only applicable for configMode = -1
			permGroup = ""; //null means this is skipped, "" means error & return without creating hologram
			usePerm = null;
			seePerm = null;
			factionName = null; //Only applicable for configMode = 3
			break;
		case 3:
			//sendOriginal = plugin.getConfig().getBoolean("ChatBubble_Send_Original_Message"); //Must be handled within ChatListenerPrototype bc infinite recursion
			isSoundOnly = false; //Only true for configMode = 4
			requirePerm = false;
			citizensShowToAll = false; //Only applicable for configMode = -1
			permGroup = null; //Only applicable for configMode = 2
			usePerm = null;
			seePerm = null;
			factionName = ""; //null means this is skipped, "" means error & return without creating hologram
			break;
		case 4:
			//sendOriginal = plugin.getConfig().getBoolean("ChatBubble_Send_Original_Message"); //Must be handled within ChatListenerPrototype bc infinite recursion
			isSoundOnly = true; //Only true for configMode = 4
			requirePerm = false;
			citizensShowToAll = false; //Only applicable for configMode = -1
			permGroup = null; //Only applicable for configMode = 2
			usePerm = null;
			seePerm = null;
			factionName = null; //Only applicable for configMode = 3
			break;
		case 5:
			//sendOriginal = plugin.getConfig().getBoolean("ChatBubble_Send_Original_Message"); //Must be handled within ChatListenerPrototype bc infinite recursion
			isSoundOnly = false; //Only true for configMode = 4
			requirePerm = plugin.getConfig().getBoolean("ConfigFive_Require_Permissions");
			citizensShowToAll = false; //Only applicable for configMode = -1
			permGroup = null; //Only applicable for configMode = 2
			usePerm = plugin.getConfig().getString("ConfigFive_Use_Permission");
			seePerm = plugin.getConfig().getString("ConfigFive_See_Permission");
			factionName = null; //Only applicable for configMode = 3
			break;
		default:
			//Copy of case 0
			//sendOriginal = plugin.getConfig().getBoolean("ChatBubble_Send_Original_Message"); //Must be handled within ChatListenerPrototype bc infinite recursion
			isSoundOnly = false; //Only true for configMode = 4
			requirePerm = plugin.getConfig().getBoolean("ConfigZero_Require_Permissions");
			citizensShowToAll = false; //Only applicable for configMode = -1
			permGroup = null; //Only applicable for configMode = 2
			usePerm = plugin.getConfig().getString("ConfigZero_Use_Permission");
			seePerm = plugin.getConfig().getString("ConfigZero_See_Permission");
			factionName = null; //Only applicable for configMode = 3
			break;
		}
		
		//-----
		//----- Begin Hologram Creation Checks -----
		//-----
		//Handle original (player) message in chat since every config mode potentially needs it. Put before any "return"s because event is cancelled so this is the only way messages can go through.
		//if(sendOriginal && le instanceof Player) ((Player) le).chat(message); //Must be handled within ChatListenerPrototype bc infinite recursion
		//If require perm and player does not have the required perm to create the hologram
		if(requirePerm && !le.hasPermission(usePerm)) return;
		//Override (delete) pre-existing holograms
		if(existingHolograms.containsKey(le.getUniqueId())) {
			for(Hologram h : existingHolograms.get(le.getUniqueId())) {
				if(h.isEnabled()) {
					h.disable();
					h.delete();
				}
			}
		}
		//Handle sound creation since every config mode potentially has sound
		if(plugin.getConfig().getBoolean("ChatBubble_Play_Sound")) {
			String sound = plugin.getConfig().getString("ChatBubble_Sound_Name").toLowerCase();
			float volume = (float) plugin.getConfig().getDouble("ChatBubble_Sound_Volume");
			if(!sound.equals("")) {
				try {
					le.getWorld().playSound(le.getLocation(), sound, volume, 1.0f);
				}catch(Exception e) {
					plugin.getServer().getConsoleSender().sendMessage("Something is wrong in your ChatBubble config.yml sound settings!");
					plugin.getServer().getConsoleSender().sendMessage("Please ensure that 'ChatBubble_Sound_Name' works in a '/playsound' command test.");
				}
			}
		}
		//Config Mode 3 - If isSoundOnly, we are done here
		if (isSoundOnly) return;
		
		//-----
		//----- Create/Manage Hologram -----
		//-----
		//Create hologram and input into database
		final Hologram hologram = DHAPI.createHologram(System.currentTimeMillis() + "", le.getLocation().add(0.0, plugin.bubbleOffset, 0.0));
		hologram.enable();
		List<Hologram> hList = new ArrayList<Hologram>();
		hList.add(hologram);
		existingHolograms.put(le.getUniqueId(), hList);
		//Hide hologram by default (exception for Citizens)
		if (!citizensShowToAll) hologram.hideAll();
		//Config Mode 2 permGroup Error Check - If a blank string then player doesn't have one and shouldn't make a hologram in mode 2
		if (permGroup != null && permGroup == "") return;
		//Handle visibility logic
		for(Player oP : Bukkit.getOnlinePlayers()){
			if(((plugin.seeOwnBubble) || (!plugin.seeOwnBubble && oP.getName() != le.getName())) //Players can see their own bubble, or they can't but they are different players : Config Mode ALL
					&& (oP.getWorld().getName().equals(le.getWorld().getName()) //Players are in the same world : Config Mode ALL
					&& (oP.getLocation().distance(le.getLocation()) <= plugin.distance)) //Players are within range of eachother : Config Mode ALL
					&& (!requirePerm || (requirePerm && oP.hasPermission(seePerm))) //A requirement isn't required to see, or is and the player has it : Config Mode 0,1,5
					&& (permGroup == null || oP.hasPermission(permGroup)) //A permission group isn't intended, or it is and the player has it : Config Mode 2
					&& (factionName == null || MPlayer.get(oP).getFactionName().equals(factionName)) //A faction isn't intended, or it is and the player is in it : Config Mode 3
					&& (le instanceof Player && oP.canSee((Player) le))) //Players corporeal bodies are able to see eachother : Config Mode ALL
				hologram.show(oP, 0);
				//hologram.getVisibilityManager().showTo(oP);
		}
		//Maintain hologram position and kill when time comes
		int lines = formatHologramLines(le, hologram, message);
		new BukkitRunnable() {
			int ticksRun = 0;
			@Override
			public void run() {
				ticksRun++;
				if(hologram.isEnabled())
					DHAPI.moveHologram(hologram, le.getLocation().add(0.0, plugin.bubbleOffset + .25 * lines, 0.0));
				if (ticksRun > plugin.life) {
					hologram.disable();
					hologram.delete();
					cancel();
				}
		}}.runTaskTimer(plugin, 1L, 1L);
	}
	
}
