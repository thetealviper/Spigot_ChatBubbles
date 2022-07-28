package me.TheTealViper.chatbubbles;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import me.TheTealViper.chatbubbles.citizens.ChatBubbleTrait;
import me.TheTealViper.chatbubbles.implentations.ChatListenerHIGH;
import me.TheTealViper.chatbubbles.implentations.ChatListenerHIGHEST;
import me.TheTealViper.chatbubbles.implentations.ChatListenerLOW;
import me.TheTealViper.chatbubbles.implentations.ChatListenerLOWEST;
import me.TheTealViper.chatbubbles.implentations.ChatListenerMONITOR;
import me.TheTealViper.chatbubbles.implentations.ChatListenerNORMAL;
import me.TheTealViper.chatbubbles.implentations.DecentHologramsImplementation;
import me.TheTealViper.chatbubbles.implentations.HolographicDisplaysImplementation;
import me.TheTealViper.chatbubbles.utils.EnableShit;
import me.TheTealViper.chatbubbles.utils.PluginFile;
import net.md_5.bungee.api.ChatColor;

public class ChatBubbles extends JavaPlugin implements Listener{
	public int life = -1, distance = -1, length = -1;
	public String prefix = "", suffix = "";
	public boolean seeOwnBubble = false;
	public double bubbleOffset = 2.5;
	public PluginFile togglePF;
	public boolean foundHolographicDisplays = false;
	public boolean foundDecentHolograms = false;
	private boolean useTrait = false;
	public HolographicDisplaysImplementation HDI;
	public DecentHologramsImplementation DHI;
	public static EventPriority eventPriority;
	private ChatBubbleTrait trait;
	
	public void onEnable(){
		if(Bukkit.getServer().getPluginManager().getPlugin("DecentHolograms") != null) {
			foundDecentHolograms = true;
			DHI = new DecentHologramsImplementation();
		}else if(Bukkit.getServer().getPluginManager().getPlugin("HolographicDisplays") != null) {
			foundHolographicDisplays = true;
			HDI = new HolographicDisplaysImplementation();
		}else {
			getServer().getConsoleSender().sendMessage(ChatColor.RED + "ChatBubbles can't activate because you have neither HolographicDisplays nor DecentHolograms installed!!!");
			return;
		}
		EnableShit.handleOnEnable(this, this, "49387");
		HolographicDisplaysImplementation.plugin = this;
		DecentHologramsImplementation.plugin = this;
		initVars();
		togglePF = new PluginFile(this, "toggleData");
		if(getServer().getPluginManager().getPlugin("Citizens") != null) {
			if(getServer().getPluginManager().getPlugin("Citizens").isEnabled() == true && this.useTrait) {
				Bukkit.getServer().getConsoleSender().sendMessage("[ChatBubbles] Citizens found and trait chatbubble enabled");
				net.citizensnpcs.api.CitizensAPI.getTraitFactory().registerTrait(net.citizensnpcs.api.trait.TraitInfo.create(ChatBubbleTrait.class).withName("chatbubble"));
				trait = new ChatBubbleTrait();
				//trait.plugin = this; //Can't do it this way because constructor uses it and I don't know if adding args to constructor will break two lines up trait registration
			}					
		}
	}
	
	private void initVars() {
		life = getConfig().getInt("ChatBubble_Life");
		distance = getConfig().getInt("ChatBubble_Viewing_Distance");
		length = getConfig().getInt("ChatBubble_Maximum_Line_Length");
		prefix = getConfig().getString("ChatBubble_Message_Prefix");
		if(prefix == null)
			prefix = "";
		suffix = getConfig().getString("ChatBubble_Message_Suffix");
		if(suffix == null)
			suffix = "";
		seeOwnBubble = getConfig().getBoolean("ChatBubble_See_Own_Bubbles");
		bubbleOffset = getConfig().getDouble("ChatBubble_Height_Offset");
		useTrait = getConfig().getBoolean("Use_ChatBubble_Trait_Citizens");
		switch(getConfig().getString("ChatBubble_EventPriority").toUpperCase()) {
		case "HIGH":
			eventPriority = EventPriority.HIGH;
			ChatListenerHIGH.plugin = this;
			Bukkit.getPluginManager().registerEvents(new ChatListenerHIGH(), this);
			break;
		case "HIGHEST":
			eventPriority = EventPriority.HIGHEST;
			ChatListenerHIGHEST.plugin = this;
			Bukkit.getPluginManager().registerEvents(new ChatListenerHIGHEST(), this);
			break;
		case "LOW":
			eventPriority = EventPriority.LOW;
			ChatListenerLOW.plugin = this;
			Bukkit.getPluginManager().registerEvents(new ChatListenerLOW(), this);
			break;
		case "LOWEST":
			eventPriority = EventPriority.LOWEST;
			ChatListenerLOWEST.plugin = this;
			Bukkit.getPluginManager().registerEvents(new ChatListenerLOWEST(), this);
			break;
		case "MONITOR":
			eventPriority = EventPriority.MONITOR;
			ChatListenerMONITOR.plugin = this;
			Bukkit.getPluginManager().registerEvents(new ChatListenerMONITOR(), this);
			break;
		case "NORMAL":
			eventPriority = EventPriority.NORMAL;
			ChatListenerNORMAL.plugin = this;
			Bukkit.getPluginManager().registerEvents(new ChatListenerNORMAL(), this);
			break;
		}
	}
	
	public void onDisable(){
		if(getServer().getPluginManager().getPlugin("Citizens") != null) {
			if(getServer().getPluginManager().getPlugin("Citizens").isEnabled() && this.useTrait) {
				trait.onDisable();
			}
		}
		getServer().getConsoleSender().sendMessage(makeColors("ChatBubbles from TheTealViper shutting down. Bshzzzzzz"));
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(sender instanceof Player) {
			Player p = (Player) sender;
			if(label.equalsIgnoreCase("chatbubble") || label.equalsIgnoreCase("cb")){
				if(args.length == 0){
					return false;
				}else{
					String message = "";
					for(int i = 0;i < args.length;i++)
						if(i == args.length - 1)
							message += args[i];
						else
							message += args[i] + " ";
					handleOne(message, p);
				}
			}
			if((label.equalsIgnoreCase("chatbubblereload") || label.equalsIgnoreCase("cbreload")) && p.hasPermission("chatbubbles.reload")){
				reloadConfig();
				initVars();
				p.sendMessage("Reloaded Successfully");
			}
			if((label.equalsIgnoreCase("chatbubbletoggle") || label.equalsIgnoreCase("cbtoggle") || label.equalsIgnoreCase("cbt")) && p.hasPermission("chatbubbles.toggle")) {
				boolean currentState = togglePF.getBoolean(p.getUniqueId().toString());
				if(currentState)
					p.sendMessage("ChatBubbles toggled off!");
				else
					p.sendMessage("ChatBubbles toggled on!");
				togglePF.set(p.getUniqueId().toString(), !currentState);
				togglePF.save();
			}
		}
		return false;
	}
	
//	@EventHandler
//	public void onChat(com.palmergames.bukkit.TownyChat.events.AsyncChatHookEvent e) {
//		e.getChannel().
//	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if(!togglePF.contains(p.getUniqueId().toString())) {
			togglePF.set(p.getUniqueId().toString(), true);
			togglePF.save();
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e){
		if(foundHolographicDisplays)
			HDI.onQuit(e.getPlayer().getUniqueId());
		else if(foundDecentHolograms)
			DHI.onQuit(e.getPlayer().getUniqueId());
	}
	
	public void handleZero(String message, Player p){
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {public void run() {
			if(foundHolographicDisplays)
				HDI.handleZero(message, p);
			else if(foundDecentHolograms)
				DHI.handleZero(message, p);
		}}, 0);
	}
	
	public void handleOne(String message, Player p){
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {public void run() {
			if(foundHolographicDisplays)
				HDI.handleOne(message, p);
			else if(foundDecentHolograms)
				DHI.handleOne(message, p);
		}}, 0);
	}
	
	public void handleTwo(String message, Player p){
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {public void run() {
			if(foundHolographicDisplays)
				HDI.handleTwo(message, p);
			else if(foundDecentHolograms)
				DHI.handleTwo(message, p);
		}}, 0);
	}
	
	public void handleThree(String message, Player p){
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {public void run() {
			if(foundHolographicDisplays)
				HDI.handleThree(message, p);
			else if(foundDecentHolograms)
				DHI.handleThree(message, p);
		}}, 0);
	}
	
	public void handleFour(String message, Player p){
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {public void run() {
			if(foundHolographicDisplays)
				HDI.handleFour(message, p);
			else if(foundDecentHolograms)
				DHI.handleFour(message, p);
		}}, 0);
	}
	
	public void handleFive(String message, Player p){
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {public void run() {
			if(foundHolographicDisplays)
				HDI.handleFive(message, p);
			else if(foundDecentHolograms)
				DHI.handleFive(message, p);
		}}, 0);
	}
	
//------------------------Utilities--------------------------------
	
	public final static Pattern HEXPAT = Pattern.compile("&#[a-fA-F0-9]{6}");
	public static String makeColors(String s){
		//Handle standard basic colors
		while(s.contains("&0"))s = s.replace("&0", ChatColor.BLACK + "");
		while(s.contains("&1"))s = s.replace("&1", ChatColor.DARK_BLUE + "");
		while(s.contains("&2"))s = s.replace("&2", ChatColor.DARK_GREEN + "");
		while(s.contains("&3"))s = s.replace("&3", ChatColor.DARK_AQUA + "");
		while(s.contains("&4"))s = s.replace("&4", ChatColor.DARK_RED + "");
		while(s.contains("&5"))s = s.replace("&5", ChatColor.DARK_PURPLE + "");
		while(s.contains("&6"))s = s.replace("&6", ChatColor.GOLD + "");
		while(s.contains("&7"))s = s.replace("&7", ChatColor.GRAY + "");
		while(s.contains("&8"))s = s.replace("&8", ChatColor.DARK_GRAY + "");
		while(s.contains("&9"))s = s.replace("&9", ChatColor.BLUE + "");
		while(s.contains("&a"))s = s.replace("&a", ChatColor.GREEN + "");
		while(s.contains("&b"))s = s.replace("&b", ChatColor.AQUA + "");
		while(s.contains("&c"))s = s.replace("&c", ChatColor.RED + "");
		while(s.contains("&d"))s = s.replace("&d", ChatColor.LIGHT_PURPLE + "");
		while(s.contains("&e"))s = s.replace("&e", ChatColor.YELLOW + "");
		while(s.contains("&f"))s = s.replace("&f", ChatColor.WHITE + "");
		while(s.contains("&k"))s = s.replace("&k", ChatColor.MAGIC + "");
		while(s.contains("&l"))s = s.replace("&l", ChatColor.BOLD + "");
		while(s.contains("&m"))s = s.replace("&m", ChatColor.STRIKETHROUGH + "");
		while(s.contains("&n"))s = s.replace("&n", ChatColor.UNDERLINE + "");
		while(s.contains("&o"))s = s.replace("&o", ChatColor.ITALIC + "");
		while(s.contains("&r"))s = s.replace("&r", ChatColor.RESET + "");
		//Handle custom hex codes (1.16 and up)
        Matcher match = HEXPAT.matcher(s);
        while(match.find()) {
        	String color = s.substring(match.start(), match.end());
        	s = s.replace(color, ChatColor.of(color.replace("&", "")) + "");
        }
        
		return s;
	}
	
}
