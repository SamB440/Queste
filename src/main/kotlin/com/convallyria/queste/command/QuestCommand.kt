package com.convallyria.queste.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.CommandHelp
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.HelpCommand
import co.aikar.commands.annotation.Subcommand
import com.convallyria.queste.Queste
import org.bukkit.entity.Player

@CommandAlias("quest")
class QuestCommand(private val plugin: Queste) : BaseCommand() {

    @Default
    @HelpCommand
    fun onDefault(commandHelp : CommandHelp) {
        commandHelp.showHelp()
    }

    @Subcommand("create")
    fun onCreate(player: Player) {

    }
}