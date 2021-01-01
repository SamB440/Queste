package com.convallyria.queste.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.CommandHelp
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.HelpCommand
import co.aikar.commands.annotation.Subcommand
import com.convallyria.queste.Queste
import org.bukkit.entity.Player

class QuestCommand(private val plugin: Queste) : BaseCommand() {

    @Default
    fun onDefault(player : Player) {

    }

    @HelpCommand
    @Subcommand("help")
    fun onHelp(commandHelp : CommandHelp) {
        commandHelp.showHelp()
    }

    @Subcommand("add")
    fun onAdd(player: Player) {

    }
}