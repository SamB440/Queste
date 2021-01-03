package com.convallyria.queste.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.CommandHelp
import co.aikar.commands.annotation.*
import com.convallyria.queste.Queste
import com.convallyria.queste.managers.data.account.QuesteAccount
import com.convallyria.queste.quest.Quest
import com.convallyria.queste.quest.objective.QuestObjective
import com.convallyria.queste.quest.reward.QuestReward
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

    @Subcommand("setrestart")
    @CommandCompletion("@quests")
    fun onSetRestart(sender: CommandSender, quest: Quest, restart: Boolean) {
        quest.setCanRestart(restart)
        quest.save(plugin)
        sender.sendMessage(translate("&aQuest " + quest.name + " has had 'canRestart' set to: " + quest.canRestart() + "."))
    }

    @Subcommand("addrequirement")
    @CommandCompletion("@quests @quests")
    fun onAddRequirement(sender: CommandSender, quest: Quest, requirement: Quest) {
        quest.addRequiredQuest(requirement)
        sender.sendMessage(translate("&aAdded required quest " + requirement.name + " to quest " + quest.name + "."))
    }

    @Subcommand("addreward")
    @CommandCompletion("@quests @rewards")
    fun onAddReward(sender: CommandSender, quest: Quest, reward: QuestReward) {
        quest.addReward(reward)
        sender.sendMessage(translate("&aAdded reward &6" + reward.name + "&a to quest &6" + quest.name + "&a."))
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
}