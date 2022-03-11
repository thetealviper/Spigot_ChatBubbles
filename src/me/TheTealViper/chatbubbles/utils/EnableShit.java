package me.TheTealViper.chatbubbles.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class EnableShit {
	
	public static void handleOnEnable(JavaPlugin plugin, Listener pluginL, String spigotID){
		plugin.saveDefaultConfig();
		checkUpdates(plugin, spigotID);
		Bukkit.getPluginManager().registerEvents(pluginL, plugin);
		Bukkit.getServer().getConsoleSender().sendMessage(plugin.getDescription().getName() + " from TheTealViper powered ON!");
	}
	
	public static void checkUpdates(JavaPlugin plugin, String spigotID){
		if(!spigotID.equals("-1"))
			updatePlugin(plugin, spigotID);
		updateConfig(plugin);
	}
	public static void updatePlugin(JavaPlugin plugin, String spigotID){
		String installed = plugin.getDescription().getVersion();
		String[] installed_Arr = installed.split("[.]");
		String posted = getSpigotVersion(spigotID);
		if(posted == null)
			return;
		String[] posted_Arr = posted.split("[.]");
		for(int i = 0;i < posted_Arr.length;i++){
			if(installed_Arr.length <= i || Integer.valueOf(installed_Arr[i]) < Integer.valueOf(posted_Arr[i])){
				Bukkit.getServer().getConsoleSender().sendMessage(plugin.getDescription().getName() + " has an update ready [" + installed + " -> " + posted + "]!");
				break;
			}
		}
	}
	public static void updateConfig(JavaPlugin plugin){
		YamlConfiguration compareTo = YamlConfiguration.loadConfiguration(new InputStreamReader(plugin.getResource("config.yml")));
		boolean update = false;
		if(!plugin.getConfig().contains("VERSION"))
			update = true;
		String oldVersion = plugin.getConfig().getString("VERSION");
		String[] oldVersion_Arr = oldVersion.split("[.]");
		String newVersion = compareTo.getString("VERSION");
		String[] newVersion_Arr = newVersion.split("[.]");
		for(int i = 0;i < newVersion_Arr.length;i++){
			if(oldVersion_Arr.length <= i || Integer.valueOf(oldVersion_Arr[i]) < Integer.valueOf(newVersion_Arr[i])){
				update = true;
				break;
			}
		}
		if(update){
			File file = new File("plugins/" + plugin.getDescription().getName() + "/config.yml");
			try {
				FileUtils.copyFile(file, new File("plugins/" + plugin.getDescription().getName() + "/configBACKUP_" + oldVersion + ".yml"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(file.exists())
				file.delete();
			plugin.saveDefaultConfig();
			Bukkit.getServer().getConsoleSender().sendMessage(plugin.getDescription().getName() + " config.yml has been updated [" + oldVersion + " -> " + newVersion + "] and a backup created of old configuration!");
		}
	}
	private static String getSpigotVersion(String spigotID) {
        try {
            HttpURLConnection con = (HttpURLConnection) new URL("http://www.spigotmc.org/api/general.php").openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.getOutputStream().write(
                    ("key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4&resource=" + spigotID).getBytes("UTF-8"));
            String version = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
            return version;
        } catch (Exception ex) {
            
        }
        return null;
    }
}
