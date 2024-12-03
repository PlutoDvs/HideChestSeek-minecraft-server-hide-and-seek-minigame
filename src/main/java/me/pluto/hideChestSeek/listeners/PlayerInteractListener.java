package me.pluto.hideChestSeek.listeners;

import me.pluto.hideChestSeek.Game;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataType;
import me.pluto.hideChestSeek.HideChestSeek;
import me.pluto.hideChestSeek.events.HiddenChestFoundEvent;

public class PlayerInteractListener implements Listener {

  private final HideChestSeek plugin = HideChestSeek.instance;

  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent event) {
    Player player = event.getPlayer();
    Block block = event.getClickedBlock();

    if(block == null || block.getType() != Material.CHEST) return;

    Chest chest = (Chest) block.getState();

    boolean isGameChest = chest.getPersistentDataContainer().has(
            new NamespacedKey(plugin, "chest"),
            PersistentDataType.STRING
            );

    if(!isGameChest) {
      player.sendMessage("this is not a game chest");
      return;
    }
    event.setCancelled(true);

    String gameId = chest.getPersistentDataContainer().get(
        new NamespacedKey(plugin, "chest"),
        PersistentDataType.STRING
    );

    Game game = HideChestSeek.instance.getGames().get(gameId);

    HiddenChestFoundEvent chestFoundEvent = new HiddenChestFoundEvent(player, game);
    Bukkit.getServer().getPluginManager().callEvent(chestFoundEvent);

  }
}
