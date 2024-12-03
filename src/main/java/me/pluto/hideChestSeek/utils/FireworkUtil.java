package me.pluto.hideChestSeek.utils;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import me.pluto.hideChestSeek.HideChestSeek;

import java.util.ArrayList;
import java.util.List;

public class FireworkUtil {

  private final HideChestSeek plugin = HideChestSeek.instance;
  private final ConfigUtil configUtil = plugin.getConfigUtil();

  /**
  *  @param location Location of block where firework should be spawned
  */
  public void spawnFirework(Location location) {
    if (!configUtil.areFireworksEnabled()) {
      return;
    }

    World world = location.getWorld();
    if (world == null) {
      plugin.getLogger().warning("Cannot spawn firework: World is null at location " + location);
      return;
    }

    Firework firework = (Firework) world.spawnEntity(location, EntityType.FIREWORK);
    FireworkMeta fireworkMeta = firework.getFireworkMeta();

    // Set firework type
    FireworkEffect.Type type = getFireworkType(configUtil.getFireworkType());
    if (type == null) {
      plugin.getLogger().warning("Invalid firework type in config. Defaulting to BALL.");
      type = FireworkEffect.Type.BALL;
    }

    // Set firework colors
    List<Color> colors = getFireworkColors(configUtil.getFireworkColors());
    if (colors.isEmpty()) {
      // Default to white if no colors are specified
      colors.add(Color.WHITE);
    }

    // Build the firework effect
    FireworkEffect effect = FireworkEffect.builder()
        .with(type)
        .withColor(colors)
        .flicker(true)
        .trail(true)
        .build();

    fireworkMeta.addEffect(effect);

    // Set firework power
    int power = configUtil.getFireworkPower();
    fireworkMeta.setPower(power);

    // Apply the meta to the firework
    firework.setFireworkMeta(fireworkMeta);
  }

  private FireworkEffect.Type getFireworkType(String typeName) {
    try {
      return FireworkEffect.Type.valueOf(typeName.toUpperCase());
    } catch (IllegalArgumentException e) {
      plugin.getLogger().warning("Unknown firework type: " + typeName);
      return null;
    }
  }

  private List<Color> getFireworkColors(List<String> colorNames) {
    List<Color> colors = new ArrayList<>();
    for (String colorName : colorNames) {
      Color color = getColorByName(colorName);
      if (color != null) {
        colors.add(color);
      } else {
        plugin.getLogger().warning("Unknown firework color: " + colorName);
      }
    }
    return colors;
  }

  private Color getColorByName(String colorName) {
    switch (colorName.toUpperCase()) {
      case "AQUA": return Color.AQUA;
      case "BLACK": return Color.BLACK;
      case "BLUE": return Color.BLUE;
      case "FUCHSIA": return Color.FUCHSIA;
      case "GRAY": return Color.GRAY;
      case "GREEN": return Color.GREEN;
      case "LIME": return Color.LIME;
      case "MAROON": return Color.MAROON;
      case "NAVY": return Color.NAVY;
      case "OLIVE": return Color.OLIVE;
      case "ORANGE": return Color.ORANGE;
      case "PURPLE": return Color.PURPLE;
      case "RED": return Color.RED;
      case "SILVER": return Color.SILVER;
      case "TEAL": return Color.TEAL;
      case "WHITE": return Color.WHITE;
      case "YELLOW": return Color.YELLOW;
      default: return null;
    }
  }
}
