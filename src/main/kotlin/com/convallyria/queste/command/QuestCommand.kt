package com.convallyria.queste.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.CommandHelp
import co.aikar.commands.annotation.*
import com.convallyria.queste.Queste
import com.convallyria.queste.managers.data.account.QuesteAccount
import com.convallyria.queste.quest.Quest
import com.convallyria.queste.quest.objective.QuestObjective
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandAlias("quest")
class QuestCommand(private val plugin: Queste) : BaseCommand(), IQuesteCommand {

    @Default
    @HelpCommand
    fun onDefault(commandHelp : CommandHelp) {
        commandHelp.showHelp()
    }

    @Subcommand("create")
    fun onCreate(sender: CommandSender, name: String) {
        val quest = Quest(name)
        plugin.managers.questeCache.addQuest(quest)
        quest.save(plugin)
        sender.sendMessage(translate("&aThe quest &6$name&a has been created."))
    }

    @Subcommand("addobjective")
    @CommandCompletion("@objectives @quests")
    fun onAddObjective(sender: CommandSender, objective: QuestObjective.QuestObjectiveEnum, quest: Quest) {
        quest.addObjective(objective.getNewObjective(plugin, quest))
        quest.save(plugin)
        sender.sendMessage(translate("&aAdded new objective " + objective.name + " to " + quest.name + "."))
    }

    @Subcommand("addplayer")
    @CommandCompletion("@players @quests")
    fun onAddPlayer(sender: CommandSender, playerName: String, quest: Quest) {
        val player = Bukkit.getPlayer(playerName)
        if (player != null) {
            plugin.managers.storageManager.getAccount(player.uniqueId).thenAccept { account: QuesteAccount ->
                account.addActiveQuest(quest)
                sender.sendMessage(translate("&aPlayer " + playerName + " now has the quest " + quest.name + "."))
            }.exceptionally { err ->
                sender.sendMessage(translate("&cError performing command. See console for details."))
                err.printStackTrace()
                null
            }
        } else {
            sender.sendMessage(translate("&cThat player is not online."))
        }
    }
}