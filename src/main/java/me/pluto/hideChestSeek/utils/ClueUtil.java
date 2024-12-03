package me.pluto.hideChestSeek.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import me.pluto.hideChestSeek.HideChestSeek;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClueUtil {

  private final HideChestSeek plugin = HideChestSeek.instance;


  public List<String> getApplicableClues(Location location) {
    ConfigUtil configUtil = plugin.getConfigUtil();
    Map<Material, List<String>> cluesMap = configUtil.getClues();
    List<String> applicableClues = new ArrayList<>();

    // Retrieve the clue search radius from the config
    int radius = configUtil.getClueSearchRadius();

    World world = location.getWorld();
    if (world == null) {
      plugin.getLogger().warning("World is null at location: " + location);
      return applicableClues;
    }

    // Get the block coordinates
    int baseX = location.getBlockX();
    int baseY = location.getBlockY();
    int baseZ = location.getBlockZ();

    // For each clue material, check if it's near the location
    for (Map.Entry<Material, List<String>> entry : cluesMap.entrySet()) {
      Material material = entry.getKey();
      boolean found = isMaterialNearLocation(world, material, baseX, baseY, baseZ, radius);
      if (found) {
        applicableClues.addAll(entry.getValue());
      }
    }

    return applicableClues;
  }

  private boolean isMaterialNearLocation(World world, Material material, int baseX, int baseY, int baseZ, int radius) {
    for (int x = baseX - radius; x <= baseX + radius; x++) {
      for (int y = baseY - radius; y <= baseY + radius; y++) {
        for (int z = baseZ - radius; z <= baseZ + radius; z++) {
          Block block = world.getBlockAt(x, y, z);
          if (block.getType() == material) {
            return true;
          }
        }
      }
    }
    return false;
  }
}

