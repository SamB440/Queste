package com.convallyria.queste.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import com.convallyria.queste.Queste
import com.convallyria.queste.gui.QuestViewGUI
import org.bukkit.entity.Player

@CommandAlias("quests")
@CommandPermission("queste.quests|queste.admin")
class QuestsCommand(private val plugin: Queste) : BaseCommand(), IQuesteCommand {

    @Default
    fun onDefault(player: Player) {
        val gui = QuestViewGUI(plugin, player)
        gui.open()
    }
}