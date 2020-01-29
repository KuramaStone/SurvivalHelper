package me.brook.helper.listeners;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedEnterEvent.BedEnterResult;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.brook.helper.Main;

public class SoloSleepListener implements Listener {

	private static final int timeSkip = 1000;
	private static final int skipStop = 250;

	private Main main;

	private Player sleepingPlayer;
	private BukkitRunnable runnable;

	public SoloSleepListener(Main main) {
		this.main = main;
	}

	@EventHandler
	public void onSleep(PlayerBedEnterEvent event) {
		if(event.getBedEnterResult() == BedEnterResult.OK && sleepingPlayer == null) {
			sleepingPlayer = event.getPlayer();
			startDay(event.getPlayer().getWorld());
		}
	}
	
	@EventHandler
	public void onLeaveBed(PlayerBedLeaveEvent event) {
		if(event.getPlayer() == sleepingPlayer) {
			runnable.cancel();
		}
	}

	private void startDay(World world) {
		runnable = new BukkitRunnable() {
			@Override
			public void run() {

				long time = world.getTime();

				if(time > 12000) {
					time += timeSkip;
				}
				time %= 24000;
				
				if(time >= skipStop && time < 12000) {
					time = skipStop;
					
					sleepingPlayer = null;
					cancel();
				}

				world.setTime(time);
			}
		};

		runnable.runTaskTimer(main, 0l, 10);
	}

}
