package me.pixelgames.pixelcrack3r.pswrapper.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import gq.pixelgames.pixelcrack3r.configuration.MySQLConfiguration;
import gq.pixelgames.pixelcrack3r.main.PixelGames;
import me.pixelgames.pixelcrack3r.pswrapper.main.PrivateServer;
import gq.pixelgames.pixelcrack3r.utils.MultiLanguageManager;
import gq.pixelgames.pixelcrack3r.utils.MultiLanguageMessanger;

public class OnPlayerConnectionListener implements Listener {

	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent e) {
		JsonArray bannedPlayers = PrivateServer.getInstance().getProperty("bannedPlayers", new JsonArray()).getAsJsonArray();
		for(int i = 0; i < bannedPlayers.size(); i++) {
			if(!bannedPlayers.get(i).isJsonObject()) continue;
			JsonObject playerData = bannedPlayers.get(i).getAsJsonObject();
			if(playerData.get("uuid").getAsString().equalsIgnoreCase(e.getPlayer().getUniqueId().toString())) {
				e.setKickMessage("§cYou are banned from this server.");
				e.setResult(Result.KICK_BANNED);
				return;
			}
			if(playerData.get("name").getAsString().equalsIgnoreCase(e.getPlayer().getName())) {
				e.setKickMessage("§cYou are banned from this server.");
				e.setResult(Result.KICK_BANNED);
				return;
			}
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		if(!PrivateServer.getInstance().isTemplate()) {
			e.setJoinMessage("");
			Bukkit.getOnlinePlayers().forEach(player -> {
				MultiLanguageManager man = new MultiLanguageManager(player.getUniqueId(), new MySQLConfiguration(PixelGames.getDefaultMySQL(), "languages"));
				MultiLanguageMessanger messanger = new MultiLanguageMessanger("PSWrapper", man.getLanguage());
				
				player.sendMessage(messanger.getMessage("player-join").replaceAll("%player%", e.getPlayer().getName()));
			});
		}

		
		MultiLanguageManager man = new MultiLanguageManager(e.getPlayer().getUniqueId(), new MySQLConfiguration(PixelGames.getDefaultMySQL(), "languages"));
		MultiLanguageMessanger messanger = new MultiLanguageMessanger("PSWrapper", man.getLanguage());
		
		if(e.getPlayer().getUniqueId().toString().equalsIgnoreCase(PrivateServer.getInstance().getOwner().toString())) {
			e.getPlayer().sendMessage("§6§m-------------------------------------------------");
			e.getPlayer().sendMessage(messanger.getMessage("join-info-owner"));
			e.getPlayer().sendMessage("§6§m-------------------------------------------------");
		} else {
			e.getPlayer().sendMessage("§6§m-------------------------------------------------");
			e.getPlayer().sendMessage(messanger.getMessage("join-info-player"));
			e.getPlayer().sendMessage("§6§m-------------------------------------------------");
		}
		e.getPlayer().sendMessage(messanger.getMessage("join-command-info"));
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		if(!PrivateServer.getInstance().isTemplate()) {
			e.setQuitMessage("");
			Bukkit.getOnlinePlayers().forEach(player -> {
				MultiLanguageManager man = new MultiLanguageManager(player.getUniqueId(), new MySQLConfiguration(PixelGames.getDefaultMySQL(), "languages"));
				MultiLanguageMessanger messanger = new MultiLanguageMessanger("PSWrapper", man.getLanguage());
				
				player.sendMessage(messanger.getMessage("player-quit").replaceAll("%player%", e.getPlayer().getName()));
			});
			
		}

	}
	
}
