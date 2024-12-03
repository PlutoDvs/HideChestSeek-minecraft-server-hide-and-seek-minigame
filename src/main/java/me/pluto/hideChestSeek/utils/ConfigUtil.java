package me.pluto.hideChestSeek.utils;

import lombok.Getter;
import org.apache.commons.jexl3.*;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class ConfigUtil {
  private final FileConfiguration config;
  @Getter
  private Map<String, Object> variables;

  public ConfigUtil(FileConfiguration config) {
    this.config = config;
    loadVariables();
  }

  public int getSpawnIntervalTicks() {
    return config.getInt("spawn.interval_ticks", 72000);
  }

  public Location getMinLocation() {
    return getLocation("location.minx", "location.miny", "location.minz");
  }

  public Location getMaxLocation() {
    return getLocation("location.maxx", "location.maxy", "location.maxz");
  }

  private Location getLocation(String xPath, String yPath, String zPath) {
    String worldName = config.getString("location.world", "world");
    World world = Bukkit.getWorld(worldName);
    if (world == null) {
      throw new IllegalArgumentException("World '" + worldName + "' not found!");
    }

    double x = config.getDouble(xPath, 0);
    double y = config.getDouble(yPath, 64);
    double z = config.getDouble(zPath, 0);

    return new Location(world, x, y, z);
  }

  private void loadVariables() {
    variables = new HashMap<>();
    ConfigurationSection variablesSection = config.getConfigurationSection("variables");
    if (variablesSection != null) {
      for (String variableName : variablesSection.getKeys(false)) {
        ConfigurationSection variableSection = variablesSection.getConfigurationSection(variableName);
        if (variableSection != null) {
          Map<String, Object> functionMap = variableSection.getValues(false);
          variables.put(variableName, functionMap);
        } else {
          Object value = variablesSection.get(variableName);
          variables.put(variableName, value);
        }
      }
    }
  }

  public Object getVariable(String variableName) {
    return variables.get(variableName);
  }

  public String processVariable(String variableName) {
    Object variable = getVariable(variableName);
    if (variable == null) {
      Bukkit.getLogger().warning("Variable '" + variableName + "' not found.");
      return "";
    }

    if (variable instanceof Map) {
      @SuppressWarnings("unchecked")
      Map<String, Object> functionMap = (Map<String, Object>) variable;

      if (functionMap.size() == 1) {
        Map.Entry<String, Object> entry = functionMap.entrySet().iterator().next();
        String functionName = entry.getKey();
        Object args = entry.getValue();

        return processFunction(functionName, args);
      } else {
        Bukkit.getLogger().warning("Multiple functions defined for variable '" + variableName + "'.");
        return "";
      }
    } else if (variable instanceof String || variable instanceof Number) {
      return variable.toString();
    }

    return variable.toString();
  }

  private String processFunction(String functionName, Object args) {
    return switch (functionName.toLowerCase()) {
      case "random_number" -> processRandomNumberFunction(args);
      case "random_item" -> processRandomItemFunction(args);
      case "expression" -> processExpressionFunction(args);

      default -> {
        Bukkit.getLogger().warning("Unknown function '" + functionName + "' in variable processing.");
        yield "";
      }
    };
  }

  private String processExpressionFunction(Object args) {
    if (!(args instanceof String exprStr)) {
      Bukkit.getLogger().warning("Arguments for 'expression' function must be a string.");
      return "";
    }

    Bukkit.getLogger().info("Evaluating expression: " + exprStr);

    try {
      Map<String, Object> functionMap = new HashMap<>();
      functionMap.put(null, Math.class);
      functionMap.put("Util", new ExpressionFunctions());
      functionMap.put("Integer", Integer.class);
      functionMap.put("String", String.class);
      functionMap.put("Random", new Random());

      JexlEngine jexlEngine = new JexlBuilder()
          .namespaces(functionMap)
          .silent(false)
          .strict(true)
          .create();

      JexlExpression expression = jexlEngine.createExpression(exprStr);

      JexlContext context = new MapContext();

      Object result = expression.evaluate(context);

      Bukkit.getLogger().info("Expression result: " + result);
      return result != null ? result.toString() : "";
    } catch (Exception ex) {
      Bukkit.getLogger().warning("Error evaluating expression: " + exprStr);
      Bukkit.getLogger().warning("Exception: " + ex.getMessage());
      return "";
    }
  }


  private String processRandomItemFunction(Object args) {
    if (args instanceof List<?> itemList) {
      if (!itemList.isEmpty()) {
        int index = new Random().nextInt(itemList.size());
        return itemList.get(index).toString();
      } else {
        Bukkit.getLogger().warning("Item list for 'random_item' function is empty.");
      }
    } else {
      Bukkit.getLogger().warning("Arguments for 'random_item' function must be a list.");
    }
    return "";
  }

  private String processRandomNumberFunction(Object args) {
    if (args instanceof List<?> argList) {
      if (argList.size() == 2 && argList.get(0) instanceof Number && argList.get(1) instanceof Number) {
        int min = ((Number) argList.get(0)).intValue();
        int max = ((Number) argList.get(1)).intValue();
        Random random = new Random();
        int randomNumber = min + random.nextInt(max - min + 1);
        return String.valueOf(randomNumber);
      } else {
        Bukkit.getLogger().warning("Invalid arguments for 'random_number' function.");
      }
    } else {
      Bukkit.getLogger().warning("Arguments for 'random_number' function must be a list.");
    }
    return "";
  }

  public List<String> getChestCommands() {
    return config.getStringList("chest.commands");
  }

  public List<String> getBlockedBlocks() {
    List<String> blockedBlocksInit = config.getStringList("blocked-blocks");

    List<String> blockedBlocks = new ArrayList<>();
    for (String s : blockedBlocksInit) {
      blockedBlocks.add(s.toUpperCase());
    }
    return blockedBlocks;
  }

  public boolean areFireworksEnabled() {
    return config.getBoolean("fireworks.enabled", true);
  }

  public String getFireworkType() {
    return config.getString("fireworks.type", "BURST");
  }

  public List<String> getFireworkColors() {
    return config.getStringList("fireworks.colors");
  }

  public int getFireworkPower() {
    return config.getInt("fireworks.power", 1);
  }

  public String getFoundMessage() {
    return ChatColor.translateAlternateColorCodes('&', config.getString("messages.found_message", "Congratulations {player}, you found the treasure!"));
  }

  public boolean isBroadcastMessageEnabled() {
    return config.getBoolean("messages.broadcast_message", true);
  }

  public Map<Material, List<String>> getClues() {
    Map<Material, List<String>> clues = new HashMap<>();
    ConfigurationSection cluesSection = config.getConfigurationSection("messages.clues");
    if (cluesSection != null) {
      for (String key : cluesSection.getKeys(false)) {
        if (key.startsWith("near_")) {
          String materialName = key.substring("near_".length()).toUpperCase();
          Material material = Material.getMaterial(materialName);
          if (material != null) {
            List<String> messages = cluesSection.getStringList(key);
            clues.put(material, messages);
          } else {
            Bukkit.getLogger().warning("Invalid material '" + materialName + "' in clues configuration.");
          }
        } else {
          Bukkit.getLogger().warning("Invalid clue key format: '" + key + "'. Expected format 'near_MATERIAL'.");
        }
      }
    }
    return clues;
  }

  public int getClueSearchRadius() {
    return config.getInt("clue_search_radius", 5);
  }

  public String parsePlaceholders(String input, Map<String, String> placeholders) {
    String result = input;
    for (Map.Entry<String, String> placeholder : placeholders.entrySet()) {
      result = result.replace("{" + placeholder.getKey() + "}", placeholder.getValue());
    }
    return result;
  }
}
