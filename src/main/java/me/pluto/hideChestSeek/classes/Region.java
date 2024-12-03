package me.pluto.hideChestSeek.classes;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Region {
  public World world;
  public int minx;
  public int miny;
  public int minz;
  public int maxx;
  public int maxy;
  public int maxz;
  public List<Block> blocks;

  public Region(Location loc1, Location loc2) {
    this.world = loc1.getWorld();
    this.minx = loc1.getBlockX();
    this.miny = loc1.getBlockY();
    this.minz = loc1.getBlockZ();
    this.maxx = loc2.getBlockX();
    this.maxy = loc2.getBlockY();
    this.maxz = loc2.getBlockZ();
  }

  public Location getMaxLocation(){
    return new Location(world, maxx, maxy, maxz);
  }

  public Location getMinLocation(){
    return new Location(world, minx, miny, minz);
  }

  public boolean isInside(Location loc) {
    if (loc == null || loc.getWorld() == null) {
      return false;
    }

    if (!loc.getWorld().equals(world)) {
      return false;
    }

    int x = loc.getBlockX();
    int y = loc.getBlockY();
    int z = loc.getBlockZ();

    return x >= minx && x <= maxx &&
        y >= miny && y <= maxy &&
        z >= minz && z <= maxz;
  }

  public List<Block> loadBlocks(){
    blocks = new ArrayList<Block>();
    for (int x = minx; x <= maxx; x++) {
      for (int y = miny; y <= maxy; y++) {
        for (int z = minz; z <= maxz; z++) {
          Block block = world.getBlockAt(x, y, z);
          blocks.add(block);
        }
      }
    }
    return blocks;
  }
}
