package me.TheTealViper.chatbubbles.citizens;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.java.JavaPlugin;
import me.TheTealViper.chatbubbles.ChatBubbles;
import net.citizensnpcs.api.ai.speech.SpeechContext;
import net.citizensnpcs.api.ai.speech.event.NPCSpeechEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;

public class ChatBubbleTrait extends Trait {
	
	ChatBubbles plugin = null;
	CitizensHDChatbubble hdHandler;
	CitizensDHChatbubble dhHandler;
	private boolean chatBubbleOverridesNPCChat = false;
	
	public ChatBubbleTrait() {
		super("chatbubble");
		plugin = JavaPlugin.getPlugin(ChatBubbles.class);
		if(plugin.foundHolographicDisplays) {
			hdHandler = new CitizensHDChatbubble(plugin);
			//debug
			plugin.getServer().getLogger().info("[ChatBubbles] " + "Initializing Citizens HolographicDisplays Handler");
		}else if(plugin.foundDecentHolograms) {
			dhHandler = new CitizensDHChatbubble(plugin);
			//debug
			plugin.getServer().getLogger().info("[ChatBubbles] " + "Initializing Citizens DecentHolograms Handler");
		}
		this.chatBubbleOverridesNPCChat = plugin.getConfig().getBoolean("ChatBubble_Overrides_NPC_Chat");
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onNPCSpeech(NPCSpeechEvent event) {
		if (this.npc != event.getNPC()) return;
	    if ((event.getNPC() != null) && (event.getNPC().isSpawned())) {
	    	NPC talker = event.getNPC();
	    	if ((talker.getEntity() instanceof LivingEntity))
	    	{
	    		SpeechContext sp = event.getContext();
	    		String msg = sp.getMessage();
	    		LivingEntity p = (LivingEntity)talker.getEntity();
	    		//Check which Hologram plugin is being used, then call correct method
	    		if(plugin.foundHolographicDisplays) {
	    			hdHandler.createBubbleHD(p, msg);
	    		}else if(plugin.foundDecentHolograms) {
	    			dhHandler.createBubbleDH(p, msg);
	    		}
	    		//create config options and set up
	    		if(this.chatBubbleOverridesNPCChat)
	    			event.setCancelled(true);
	    	}
	    }
	}
	
	@Override
	public void onAttach() {
		plugin.getServer().getLogger().info("[ChatBubbles] " + npc.getName() + " has been assigned trait ChatBubble!");
	}
	
	public void onDisable() {
		net.citizensnpcs.api.CitizensAPI.getTraitFactory().deregisterTrait(net.citizensnpcs.api.trait.TraitInfo.create(ChatBubbleTrait.class).withName("chatbubble"));
	}

}