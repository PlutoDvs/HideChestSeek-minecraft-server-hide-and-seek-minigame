package me.pluto.hideChestSeek;

import me.pluto.hideChestSeek.classes.Region;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class GameMonitor {

  private BukkitTask monitoringTask;
  private final HideChestSeek plugin = HideChestSeek.instance;
  private static final int TICKS_PER_SECOND = 20;
  private static final int MILLISECONDS_PER_SECOND = 1000;
  private final long spawnInterval = (long) (plugin.getConfigUtil().getSpawnIntervalTicks() / TICKS_PER_SECOND) * MILLISECONDS_PER_SECOND;

  private long lastSpawn = 0L;
  private Region region;

  public GameMonitor() {
    this.startMonitor();
  }

  public void startMonitor() {
    monitoringTask = new BukkitRunnable() {
      @Override
      public void run() {

        long nextSpawnTime = lastSpawn + spawnInterval;
        if (nextSpawnTime > System.currentTimeMillis() || !plugin.getGames().isEmpty()) {
          return;
        }

        lastSpawn = (int) System.currentTimeMillis();
        region = new Region(plugin.getConfigUtil().getMinLocation(), plugin.getConfigUtil().getMaxLocation());

        Game game = new Game(region);
        plugin.getGames().put(game.getId(), game);

      }
    }.runTaskTimer(plugin, 0L, 1L);
  }

  public void stopMonitor() {
    if (monitoringTask != null && !monitoringTask.isCancelled()) {
      monitoringTask.cancel();
    }
  }

}
