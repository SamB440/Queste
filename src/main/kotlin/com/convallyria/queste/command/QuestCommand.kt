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
    @CommandCompletion("@players @quests @options")
    fun onAddPlayer(sender: CommandSender, playerName: String, quest: Quest, arguments: Array<String>) {
        val player = Bukkit.getPlayer(playerName)
        if (player != null) {
            if (arguments.contains("--force")) {
                quest.forceStart(player)
                sender.sendMessage(translate("&aPlayer " + playerName + " now has the quest " + quest.name + ". &c(FORCED)"))
                return
            }

            quest.tryStart(player).thenAccept { started ->
                if (started) {
                    sender.sendMessage(translate("&aPlayer " + playerName + " now has the quest " + quest.name + "."))
                } else {
                    sender.sendMessage(translate("&cCould not start the quest for this player. Did they already complete it and 'restartable' is false? Have they completed all required quests?"))
                    sender.sendMessage(translate("&aTIP: &fTry running with &6--force &fto force a quest start."))
                }
            }
        } else {
            sender.sendMessage(translate("&cThat player is not online."))
        }
    }

    @Subcommand("setrestart")
    @CommandCompletion("@quests")
    fun onSetRestart(sender: CommandSender, quest: Quest, restart: Boolean) {
        quest.setCanRestart(restart)
        sender.sendMessage(translate("&aQuest " + quest.name + " has had 'canRestart' set to: " + quest.canRestart() + "."))
    }

    @Subcommand("setcompletion")
    @CommandCompletion("@objectives @quests @range:20")
    fun onSetCompletion(sender: CommandSender, objective: QuestObjective.QuestObjectiveEnum, quest: Quest, completion: Int) {
        for (questObjective in quest.objectives) {
            if (objective == questObjective.type) {
                questObjective.completionAmount = completion
                sender.sendMessage(translate("&aSet objective " + questObjective.type.getName() + " completion requirement to " + completion + "."))
                return
            }
        }
        sender.sendMessage(translate("&cThe quest " + quest.name + " does not have the objective &6" + objective.getName() + "."))
    }
}