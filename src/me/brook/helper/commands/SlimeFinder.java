package me.brook.helper.commands;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.brook.helper.Main;

public class SlimeFinder implements CommandExecutor {

	private static final String[] map_key = {
			Main.color(
					"&eEach segment is one chunk (16x16 blocks). Up on the map is North, left is East, right is West, and South is down."),
			Main.color("&eA green &a+ &emeans it is a slime chunk. A grey &r- &eis a regular chunk."),
			Main.color("&eThe 0 is you! If the &20 &eis green, then you're in a slime chunk."),
	};

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		Chunk center = null;
		if(sender.hasPermission("SurvivalHelper.find.slime")) {
			if(sender instanceof Player) {
				Player player = (Player) sender;
				center = player.getLocation().getChunk();
			}
			else {

				if(args.length >= 3) {

					try {
						String worldName = args[0];
						int x = Integer.valueOf(args[1]);
						int z = Integer.valueOf(args[2]);
						
						if(Bukkit.getWorld(worldName) == null) {
							String string = "";
							for(World world : Bukkit.getWorlds()) {
								string += world.getName() + ", ";
							}
							string = string.substring(0, string.length() - 2);
							sender.sendMessage("Worlds: " + string);
							return false;
						}
						
						center = Bukkit.getWorld(worldName).getChunkAt(x, z);
					}
					catch(Exception e) {
						sender.sendMessage("That is an invalid usage of the command. Try /slimef [world] [chunkX] [chunkY]");
						return false;
					}

				}
				else {
					sender.sendMessage("That is an invalid usage of the command. Try /slimef [world] [chunkX] [chunkY]");
					return false;
				}

			}
		}
		else {
			sender.sendMessage("You do not have the proper permissions.");
		}

		if(center == null) {
			return false;
		}

		World world = center.getWorld();

		sender.sendMessage(Main.color("&2Loading chunks..."));

		int distance = Bukkit.getServer().getViewDistance();
		for(int i = -distance; i <= distance; i++) {
			String line = "";
			for(int j = -distance; j <= distance; j++) {
				int x = center.getX() + i;
				int z = center.getZ() + j;
				Chunk chunk = world.getChunkAt(x, z);

				String color = chunk.isSlimeChunk() ? "&a" : "&r";

				if(chunk.getBlock(0, 0, 0).getBiome() == Biome.MUSHROOM_FIELD_SHORE ||
						chunk.getBlock(0, 0, 0).getBiome() == Biome.MUSHROOM_FIELDS) {
					color = chunk.isSlimeChunk() ? "&4" : "&r";
				}

				String str;

				if(i == 0 && j == 0) {
					str = color + "0";
				}
				else {
					str = color + (chunk.isSlimeChunk() ? "+" : "-");
				}

				line += str;
			}
			sender.sendMessage(Main.color(line));
		}
		sender.sendMessage(map_key);

		return true;
	}

}
