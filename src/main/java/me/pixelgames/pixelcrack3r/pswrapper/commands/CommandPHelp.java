package me.pixelgames.pixelcrack3r.pswrapper.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import gq.pixelgames.pixelcrack3r.configuration.MySQLConfiguration;
import gq.pixelgames.pixelcrack3r.main.PixelGames;
import me.pixelgames.pixelcrack3r.pswrapper.main.PrivateServer;
import gq.pixelgames.pixelcrack3r.utils.MultiLanguageManager;
import gq.pixelgames.pixelcrack3r.utils.MultiLanguageMessanger;

public class CommandPHelp implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		MultiLanguageMessanger messanger = new MultiLanguageMessanger("PSWrapper", "de");
		
		if(sender instanceof Player) {
			MultiLanguageManager man = new MultiLanguageManager(((Player)sender).getUniqueId(), new MySQLConfiguration(PixelGames.getDefaultMySQL(), "languages"));
			messanger = new MultiLanguageMessanger("PSWrapper", man.getLanguage());
		}
		
		sender.sendMessage("§7§m-------------------------------------------------");
		sender.sendMessage("");
		
		for(String commandName : PrivateServer.getInstance().getDescription().getCommands().keySet()) {
			sender.sendMessage(" §8 - §9/" + commandName + " §7» " + messanger.getMessage("command-" + commandName));			
		}

		sender.sendMessage("");
		sender.sendMessage("§7§m-------------------------------------------------");
		return true;
	}

}
