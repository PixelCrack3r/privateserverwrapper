package me.pixelgames.pixelcrack3r.pswrapper.commands;

import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import gq.pixelgames.pixelcrack3r.configuration.MySQLConfiguration;
import gq.pixelgames.pixelcrack3r.main.PixelGames;
import me.pixelgames.pixelcrack3r.pswrapper.main.PrivateServer;
import gq.pixelgames.pixelcrack3r.utils.MultiLanguageManager;
import gq.pixelgames.pixelcrack3r.utils.MultiLanguageMessanger;

public class CommandPL implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		MultiLanguageMessanger messenger = new MultiLanguageMessanger("PSWrapper", "de");
		
		if(sender instanceof Player) {
			MultiLanguageManager man = new MultiLanguageManager(((Player)sender).getUniqueId(), new MySQLConfiguration(PixelGames.getDefaultMySQL(), "languages"));
			messenger = new MultiLanguageMessanger("PSWrapper", man.getLanguage());
		}
		
		JsonObject obj = PrivateServer.getInstance().getProperty("plugins", new JsonObject()).getAsJsonObject();
		List<Entry<String, JsonElement>> installedPlugins = obj.entrySet().stream()
				.filter(plugin -> plugin.getValue().getAsJsonObject().get("installed").getAsBoolean())
				.collect(Collectors.toList());
		
		if(installedPlugins.isEmpty()) {
			sender.sendMessage(messenger.getMessage("plugins-empty"));
			return true;
		}
		
		sender.sendMessage(messenger.getMessage("plugins-installed"));
		
		for(Entry<String, JsonElement> plugin : installedPlugins) {
			sender.sendMessage(messenger.getMessage("plugins-entry").replaceAll("%name%", plugin.getValue().getAsJsonObject().get("name").getAsString()));
		}
		
		return true;
	}
	
}
