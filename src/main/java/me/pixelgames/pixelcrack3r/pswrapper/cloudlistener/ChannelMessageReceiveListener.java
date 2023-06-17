package me.pixelgames.pixelcrack3r.pswrapper.cloudlistener;

import eu.cloudnetservice.common.document.gson.JsonDocument;
import eu.cloudnetservice.driver.event.EventListener;
import eu.cloudnetservice.driver.event.events.channel.ChannelMessageReceiveEvent;
import org.bukkit.Bukkit;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import me.pixelgames.pixelcrack3r.pswrapper.main.PrivateServer;

public class ChannelMessageReceiveListener {

	@EventListener
	public void onChannelMessageReceive(ChannelMessageReceiveEvent e) {
		if(!e.channel().equalsIgnoreCase("private_server")) return;

		if(e.message().equalsIgnoreCase("send_data")) {
			JsonDocument data = JsonDocument.fromJsonString(e.content().readString());
			if(data.contains("properties")) {
				JsonObject obj = new JsonParser().parse(data.getString("properties")).getAsJsonObject();
				PrivateServer.getInstance().setProperties(obj);
			}
			if(data.contains("action") && data.getString("action").equalsIgnoreCase("broadcast")) {
				Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(data.getString("message")));
			}
		}
	}
	
}
