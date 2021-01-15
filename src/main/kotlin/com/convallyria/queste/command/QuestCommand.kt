package com.convallyria.queste.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.CommandHelp
import co.aikar.commands.annotation.*
import com.convallyria.queste.Queste
import com.convallyria.queste.colour.ColourScheme
import com.convallyria.queste.managers.data.account.QuesteAccount
import com.convallyria.queste.quest.Quest
import com.convallyria.queste.quest.objective.QuestObjective
import com.convallyria.queste.quest.reward.QuestReward
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.io.File
import java.io.FileReader
import java.io.Reader
import java.lang.Exception

@CommandAlias("quest")
@CommandPermission("queste.quest|queste.admin")
class QuestCommand(private val plugin: Queste) : BaseCommand(), IQuesteCommand {

    @Default
    @HelpCommand
    fun onDefault(commandHelp : CommandHelp) {
        commandHelp.showHelp()
    }

    @Subcommand("list")
    fun onList(sender: CommandSender) {
        val primaryColour = ColourScheme.getPrimaryColour()
        val secondaryColour = ColourScheme.getSecondaryColour()
        val pluginColour = ColourScheme.getExternalPluginColour()
        sender.sendMessage(translate("" + primaryColour
                + "List of all available quests (" + secondaryColour + "name" + primaryColour + ", "
                + pluginColour + "plugin" + primaryColour + "): "))
        for (quest in plugin.managers.questeCache.quests.values) {
            sender.sendMessage(translate(" " + secondaryColour + "- " + quest.name))
        }
    }

    @Subcommand("info")
    @CommandCompletion("@quests")
    fun onInfo(sender: CommandSender, quest: Quest) {
        val primaryColour = ColourScheme.getPrimaryColour()
        val secondaryColour = ColourScheme.getSecondaryColour()
        val pluginColour = ColourScheme.getExternalPluginColour()
        sender.sendMessage(translate("" + primaryColour + "Info for quest "
                + secondaryColour + quest.name + primaryColour + "."))
        sender.sendMessage(translate("" + primaryColour
                + "List of all objectives (" + secondaryColour + "name" + primaryColour + ", "
                + pluginColour + "plugin" + primaryColour + "): "))
        for (objective in quest.objectives) {
            val pluginRequirement =
                if (objective.pluginRequirement != null) "" + pluginColour + " (" + objective.pluginRequirement + ")"
                else ""
            sender.sendMessage(translate(" " + secondaryColour + "- " + objective.displayName + pluginRequirement))
        }

        sender.sendMessage(" ")
        sender.sendMessage(translate("" + primaryColour
                + "List of all rewards (" + secondaryColour + "name" + primaryColour + "): "))
        for (reward in quest.rewards) {
            sender.sendMessage(translate(" " + secondaryColour + "- " + reward.name))
        }

        sender.sendMessage(" ")
        sender.sendMessage(translate("" + primaryColour
                + "List of all required quests (" + secondaryColour + "name" + primaryColour + "): "))
        for (requiredQuest in quest.requiredQuests) {
            sender.sendMessage(translate(" " + secondaryColour + "- " + requiredQuest.name))
        }

        sender.sendMessage(" ")
        sender.sendMessage("" + secondaryColour + "Is a story? " + quest.isStoryMode)
        sender.sendMessage("" + secondaryColour + "Complete Sound: " + quest.completeSound)
    }

    @Subcommand("reload")
    @CommandPermission("queste.reload")
    fun onReload(sender: CommandSender) {
        sender.sendMessage(ChatColor.GREEN.toString() + "Reloading quest files...")
        val startTime = System.currentTimeMillis()
        plugin.managers.questeCache.reload()
        val endTime = System.currentTimeMillis()
        val totalTime = endTime - startTime
        sender.sendMessage(ChatColor.GREEN.toString() + "Done! (" + totalTime + "ms)")
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
    fun onAddObjective(sender: CommandSender, objectiveName: String, quest: Quest) {
        val objective = plugin.managers.objectiveRegistry.getNewObjective(objectiveName, plugin, quest)
        if (objective != null) {
            quest.addObjective(objective)
            quest.save(plugin)
            sender.sendMessage(translate("&aAdded new objective " + objective.name + " to " + quest.name + "."))
        }
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
        quest.save(plugin)
        sender.sendMessage(translate("&aAdded required quest " + requirement.name + " to quest " + quest.name + "."))
    }

    @Subcommand("addreward")
    @CommandCompletion("@quests @rewards")
    fun onAddReward(sender: CommandSender, quest: Quest, rewardName: String) {
        val reward = plugin.managers.rewardRegistry.getNewReward(rewardName, plugin)
        if (reward != null) {
            quest.addReward(reward)
            quest.save(plugin)
            sender.sendMessage(translate("&aAdded reward &6" + reward.name + "&a to quest &6" + quest.name + "&a."))
        }
    }

    @Subcommand("setdisplayname")
    @CommandCompletion("@quests")
    fun onSetDisplayName(sender: CommandSender, quest: Quest, name: String) {
        quest.displayName = name
        quest.save(plugin)
        sender.sendMessage(translate("&aSet " + quest.name + "'s display name to &6" + name + "&a."))
    }

    @Subcommand("setstorymode")
    @CommandCompletion("@quests")
    fun onSetStoryMode(sender: CommandSender, quest: Quest, storyMode: Boolean) {
        val currentStoryMode = quest.isStoryMode
        quest.isStoryMode = storyMode
        sender.sendMessage(translate("&aSet story mode from &6$currentStoryMode &ato &6$storyMode&a."))
        sender.sendMessage(translate("&aTIP: &fYou will need to set story keys with &6/questobjective setstorykey&a."))
    }

    @Subcommand("setcompletesound")
    @CommandCompletion("@quests")
    fun onSetCompleteSound(sender: CommandSender, quest: Quest, sound: Sound) {
        val currentSound = quest.completeSound
        quest.completeSound = sound
        sender.sendMessage(translate("&aSet complete sound from &6$currentSound &ato &6$sound&a."))
    }

    @Subcommand("complete")
    @CommandCompletion("@players @quests @options")
    fun onComplete(sender: CommandSender, playerName: String, quest: Quest, arguments: Array<String>) {
        val player = Bukkit.getPlayer(playerName)
        if (player != null) {
            if (arguments.contains("--force")) {
                quest.forceComplete(player)
                sender.sendMessage(translate("&aPlayer " + playerName + " has now completed the quest " + quest.name + ". &c(FORCED)"))
                return
            }

            val completed = quest.tryComplete(player)
            if (completed) {
                sender.sendMessage(translate("&aPlayer " + playerName + " has now completed the quest " + quest.name + "."))
            } else {
                sender.sendMessage(translate("&cCould not complete the quest for this player. Have they completed all required objectives?"))
                sender.sendMessage(translate("&aTIP: &fTry running with &6--force &fto force a quest completion."))
            }
        } else {
            sender.sendMessage(translate("&cThat player is not online."))
        }
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