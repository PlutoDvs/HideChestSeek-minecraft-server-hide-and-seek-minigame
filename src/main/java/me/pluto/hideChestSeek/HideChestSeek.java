package me.pluto.hideChestSeek;

import co.aikar.commands.PaperCommandManager;
import lombok.Getter;
import me.pluto.hideChestSeek.commands.HCSCommand;
import me.pluto.hideChestSeek.listeners.HiddenChestFoundListener;
import me.pluto.hideChestSeek.listeners.PlayerInteractListener;
import me.pluto.hideChestSeek.utils.ConfigUtil;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

@Getter
public final class HideChestSeek extends JavaPlugin {

  public static HideChestSeek instance;
  public Map<String, Game> games = new HashMap<>();
  public ConfigUtil configUtil;
  public GameMonitor gameMonitor;

  @Override
  public void onEnable() {
    instance = this;

    saveDefaultConfig();

    this.configUtil = new ConfigUtil(this.getConfig());
    this.gameMonitor = new GameMonitor();

    PaperCommandManager commandManager = new PaperCommandManager(this);
    commandManager.registerCommand(new HCSCommand());

    getServer().getPluginManager()
        .registerEvents(new PlayerInteractListener(), this);
    getServer().getPluginManager()
        .registerEvents(new HiddenChestFoundListener(), this);

    this.getLogger().info("HideChestSeek enabled!");
  }

  @Override
  public void onDisable() {
    this.gameMonitor.stopMonitor();

    if(!games.isEmpty()) {
      for (Game game : games.values()) {
        game.destroy();
      }
    }

    this.getLogger().info("HideChestSeek disabled!");
  }

  public void reload(){
    this.gameMonitor.stopMonitor();
    this.reloadConfig();
    this.configUtil = new ConfigUtil(this.getConfig());

    if(!games.isEmpty()) {
      for (Game game : games.values()) {
        game.destroy();
      }
    }
    this.gameMonitor.startMonitor();
  }
}
