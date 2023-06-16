package me.pixelgames.pixelcrack3r.pswrapper.cloudlistener;

import org.bukkit.Bukkit;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.dytanic.cloudnet.driver.event.EventListener;
import de.dytanic.cloudnet.driver.event.events.channel.ChannelMessageReceiveEvent;
import me.pixelgames.pixelcrack3r.pswrapper.main.PrivateServer;

public class ChannelMessageReceiveListener {

	@EventListener
	public void onChannelMessageReceive(ChannelMessageReceiveEvent e) {
		if(e.getMessage() == null) return;
		if(!e.getChannel().equalsIgnoreCase("private_server")) return;

		if(e.getMessage().equalsIgnoreCase("send_data")) {
			if(e.getData().contains("properties")) {
				JsonObject obj = JsonParser.parseString(e.getData().getString("properties")).getAsJsonObject();
				PrivateServer.getInstance().setProperties(obj);
			}
			if(e.getData().contains("action") && e.getData().getString("action").equalsIgnoreCase("broadcast")) {
				Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(e.getData().getString("message")));
			}
		}
	}
	
}
