package com.convallyria.queste.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import com.convallyria.queste.Queste
import com.convallyria.queste.gui.QuestViewGUI
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandAlias("quests")
class QuestsCommand(private val plugin: Queste) : BaseCommand(), IQuesteCommand {

    @Default
    @CommandPermission("queste.quests|queste.admin")
    fun onDefault(player: Player) {
        val gui = QuestViewGUI(plugin, player)
        gui.open()
    }

    @CommandAlias("save")
    @CommandPermission("quests.save|queste.admin")
    fun onSave(sender: CommandSender) {
        plugin.managers.questeCache.quests.values.forEach { quest -> quest.save(plugin) }
        sender.sendMessage(translate("&aQuests have been saved to file."))
    }
}