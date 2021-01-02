package com.convallyria.queste.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.CommandHelp
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.HelpCommand
import co.aikar.commands.annotation.Subcommand
import com.convallyria.queste.Queste
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

@CommandAlias("queste")
class QuesteCommand(private val plugin: Queste) : BaseCommand(), IQuesteCommand {

    @Default
    fun onDefault(sender: CommandSender) {
        if (sender.hasPermission("queste.noview") && !sender.isOp) return
        sender.sendMessage(ChatColor.YELLOW.toString() + "Wiki > " + plugin.description.website)
        sender.sendMessage(ChatColor.YELLOW.toString() + "Bugs > https://gitlab.com/convallyria/queste")
        sender.sendMessage(ChatColor.YELLOW.toString() + "Discord > https://discord.gg/fh62mxU")
        //TODO show quest data
    }

    @HelpCommand
    @Subcommand("help")
    fun onHelp(commandHelp: CommandHelp) {
        commandHelp.showHelp()
    }
}