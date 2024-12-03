package me.pluto.hideChestSeek.classes;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import me.pluto.hideChestSeek.HideChestSeek;
import me.pluto.hideChestSeek.utils.ClueUtil;

import java.util.List;
import java.util.Random;

public class HiddenChest {

  @Getter
  public final Chest chest;
  @Setter
  public boolean spawned;
  private final HideChestSeek plugin = HideChestSeek.instance;

  public HiddenChest(Chest chest) {

    this.chest = chest;
    this.spawned = true;
    this.giveClue();
  }

  public void remove(){
    chest.getLocation().getBlock().setType(Material.AIR);
  }

  private void giveClue() {
    ClueUtil clueUtil = new ClueUtil();
    List<String> clues = clueUtil.getApplicableClues(chest.getLocation());
    if (!clues.isEmpty()) {
      String clue = clues.get(new Random().nextInt(clues.size()));
      String clueMessage = ChatColor.GOLD + clue;

      for (Player player : Bukkit.getOnlinePlayers()) {
        player.sendMessage(clueMessage);
      }
    } else {
      plugin.getLogger().info("No applicable clues found for the hidden chest.");
    }
  }
}
