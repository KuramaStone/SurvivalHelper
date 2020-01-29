package me.brook.helper.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.google.common.util.concurrent.Monitor;

import me.brook.helper.Main;

public class MonitorSpawns implements CommandExecutor {

	private Map<UUID, Monitor> monitoring;
	
	public MonitorSpawns() {
		monitoring = new HashMap<>();
	}

	@Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if(sender.hasPermission("SurvivalHelper.monitorspawns")) {

			if(sender instanceof Player) {
				Player player = (Player) sender;
				
				
			}
			else {
				sender.sendMessage(Main.color("&cOnly players may use this."));
			}
			
		}
		else {
			sender.sendMessage(Main.color("&cYou do not have permission to use this."));
		}

		return false;
	}

	public static class MonitorInfo {
		private List<SpawnInfo> spawns;

		public MonitorInfo() {
			spawns = new ArrayList<>();
		}

		public void addEntity(LivingEntity entity) {
			spawns.add(new SpawnInfo(entity.getType(), entity.getLocation()));
		}

		public static class SpawnInfo {
			private final EntityType type;
			private final Location location;

			public SpawnInfo(EntityType type, Location location) {
				this.type = type;
				this.location = location;
			}

			public EntityType getType() {
				return type;
			}

			public Location getLocation() {
				return location;
			}
		}
	}
}
