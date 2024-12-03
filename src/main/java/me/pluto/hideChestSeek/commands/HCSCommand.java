package me.pluto.hideChestSeek.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.command.CommandSender;
import me.pluto.hideChestSeek.HideChestSeek;

@CommandAlias("hcs")
@CommandPermission("hcs.admin")
public class HCSCommand  extends BaseCommand {

  @Subcommand("reload")
  public void onReload(CommandSender sender) {
    HideChestSeek.instance.reload();
    sender.sendMessage("Reloaded "+ HideChestSeek.instance.getName());
  }
}
