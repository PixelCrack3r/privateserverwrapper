package me.pixelgames.pixelcrack3r.pswrapper.commands;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import gq.pixelgames.pixelcrack3r.configuration.MySQLConfiguration;
import gq.pixelgames.pixelcrack3r.main.PixelGames;
import gq.pixelgames.pixelcrack3r.utils.MultiLanguageManager;
import gq.pixelgames.pixelcrack3r.utils.MultiLanguageMessanger;

public class CommandTime implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		MultiLanguageMessanger messenger = new MultiLanguageMessanger("PSWrapper", "de");
		
		List<World> worlds = Bukkit.getServer().getWorlds();
		World world = sender instanceof Player ? ((Player)sender).getLocation().getWorld() : null;
		
		if(sender instanceof Player) {
			MultiLanguageManager man = new MultiLanguageManager(((Player)sender).getUniqueId(), new MySQLConfiguration(PixelGames.getDefaultMySQL(), "languages"));
			messenger = new MultiLanguageMessanger("PSWrapper", man.getLanguage());
		}
		
		if(args.length > 1) {
			long ticks = this.getTimeTickByDayTime(args[1]);
			
			
			if(world != null) {
				world.setTime(0);
			} else worlds.forEach(w -> {
				w.setTime(ticks);
			});
		}
		
		return true;
	}
	
	private long getTimeTickByDayTime(String time) {
		switch(time.toUpperCase()) {
			case "DAY": return 7000;
			case "NIGHT": return 16000;
			case "NOON": return 1000;
			case "EVENING": return 13500;
			default: return this.isParsable(time) ? Long.parseLong(time) : -1;
		}	
	}
	
	private boolean isParsable(String s) {
		for(char c : s.toCharArray()) if(!Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9').contains(c)) return false;
		return true;
	}
	
}
