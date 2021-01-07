package com.convallyria.queste.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.CommandHelp
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.HelpCommand
import co.aikar.commands.annotation.Subcommand
import com.convallyria.queste.Queste
import com.convallyria.queste.colour.ColourScheme
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

@CommandAlias("queste")
class QuesteCommand(private val plugin: Queste) : BaseCommand(), IQuesteCommand {

    @Default
    fun onDefault(sender: CommandSender) {
        if (sender.hasPermission("queste.noview") && !sender.isOp) return
        val primaryColour = ColourScheme.getPrimaryColour()
        val secondaryColour = ColourScheme.getSecondaryColour()
        sender.sendMessage("" + secondaryColour + "Wiki > " + ChatColor.UNDERLINE + plugin.description.website)
        sender.sendMessage("" + secondaryColour + "Bugs > " + ChatColor.UNDERLINE + "https://gitlab.com/convallyria/queste")
        sender.sendMessage("" + secondaryColour + "Discord > " + ChatColor.UNDERLINE + "https://discord.gg/fh62mxU")
        var objectives = 0
        for (quest in plugin.managers.questeCache.quests.values) {
            objectives += quest.objectives.size
        }
        sender.sendMessage("" +
                primaryColour + "" +
                plugin.managers.questeCache.quests.size
                + " quests are loaded with " + objectives + " objectives."
        )
    }

    @HelpCommand
    @Subcommand("help")
    fun onHelp(commandHelp: CommandHelp) {
        commandHelp.showHelp()
    }

    @Subcommand("about")
    fun onAbout(sender: CommandSender) {
        val primaryColour = ColourScheme.getPrimaryColour()
        val secondaryColour = ColourScheme.getSecondaryColour()
        sender.sendMessage(translate("" + primaryColour + plugin.description.name + " v" + plugin.description.version + "."))
        sender.sendMessage(translate("" + secondaryColour + "Owner: https://www.spigotmc.org/members/%%__USER__%%/"))
        sender.sendMessage(translate("" + secondaryColour + "Storage: " + plugin.managers.storageManager.javaClass.name))
    }

    @Subcommand("reload")
    fun onReload(commandSender: CommandSender) {
        plugin.reloadConfig()
        commandSender.sendMessage(translate("&aReloaded the configuration file."))
    }
}