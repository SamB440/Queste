package com.convallyria.queste.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.CommandHelp
import co.aikar.commands.annotation.*
import com.convallyria.queste.Queste
import com.convallyria.queste.colour.ColourScheme
import com.convallyria.queste.gui.QuestCreateGUI
import com.convallyria.queste.quest.Quest
import com.convallyria.queste.quest.objective.QuestObjective
import com.convallyria.queste.quest.objective.QuestObjectiveRegistry
import com.convallyria.queste.quest.requirement.ItemRequirement
import com.convallyria.queste.quest.requirement.QuestQuestRequirement
import com.convallyria.queste.quest.requirement.QuestRequirementRegistry
import com.convallyria.queste.quest.reward.ItemReward
import com.convallyria.queste.quest.reward.QuestReward
import com.convallyria.queste.quest.reward.QuestRewardRegistry
import com.convallyria.queste.util.TimeUtils
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap

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
        val primaryColour = ColourScheme.getPrimaryColour().toString()
        val secondaryColour = ColourScheme.getSecondaryColour().toString()
        val pluginColour = ColourScheme.getExternalPluginColour().toString()
        sender.sendMessage(translate(primaryColour
                + "List of all available quests (" + secondaryColour + "name" + primaryColour + ", "
                + pluginColour + "plugin" + primaryColour + "): "))
        for (quest in plugin.managers.questeCache.quests.values) {
            sender.sendMessage(translate(" " + secondaryColour + "- " + quest.name))
        }
    }

    @Subcommand("info")
    @CommandCompletion("@quests")
    fun onInfo(sender: CommandSender, quest: Quest) {
        val primaryColour = ColourScheme.getPrimaryColour().toString()
        val secondaryColour = ColourScheme.getSecondaryColour().toString()
        val pluginColour = ColourScheme.getExternalPluginColour().toString()
        sender.sendMessage(translate(primaryColour + "Info for quest "
                + secondaryColour + quest.name + primaryColour + "."))
        sender.sendMessage(translate(primaryColour
                + "List of all objectives (" + secondaryColour + "name" + primaryColour + ", "
                + pluginColour + "plugin" + primaryColour + "): "))
        for (objective in quest.objectives) {
            val pluginRequirement =
                if (objective.pluginRequirement != null) "" + pluginColour + " (" + objective.pluginRequirement + ")"
                else ""
            sender.sendMessage(translate(" " + secondaryColour + "- " + objective.displayName + pluginRequirement))
        }

        sender.sendMessage(" ")
        sender.sendMessage(translate(primaryColour
                + "List of all rewards (" + secondaryColour + "name" + primaryColour + "): "))
        for (reward in quest.rewards) {
            sender.sendMessage(translate(" " + secondaryColour + "- " + reward.name))
        }

        sender.sendMessage(" ")
        sender.sendMessage(translate(primaryColour
                + "List of all requirements (" + secondaryColour + "name" + primaryColour + "): "))
        for (requirement in quest.requirements) {
            sender.sendMessage(translate(" " + secondaryColour + "- " + requirement.name))
        }

        val timeSeconds = TimeUtils.convertTicks(quest.time.toLong(), TimeUnit.SECONDS)
        val cooldownTimeSeconds = TimeUtils.convertTicks(quest.cooldown.toLong(), TimeUnit.SECONDS)
        sender.sendMessage(" ")
        sender.sendMessage(secondaryColour + "Is a story? " + quest.isStoryMode)
        sender.sendMessage(secondaryColour + "Complete Sound: " + quest.completeSound)
        sender.sendMessage(secondaryColour + "Icon: " + quest.icon)
        sender.sendMessage(secondaryColour + "Description: " + quest.description)
        sender.sendMessage(secondaryColour + "Display name: " + quest.displayName)
        sender.sendMessage(secondaryColour + "Time: " + quest.time + " (" + timeSeconds + "s)")
        sender.sendMessage(secondaryColour + "Restartable: " + quest.canRestart())
        sender.sendMessage(secondaryColour + "Cooldown: " + quest.cooldown + " (" + cooldownTimeSeconds + "s)")
        sender.sendMessage(" ")
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

    @Subcommand("copy|clone")
    @CommandPermission("quest.copy")
    @CommandCompletion("@quests @nothing")
    fun onClone(sender: CommandSender, quest: Quest, name: String) {
        if (plugin.managers.questeCache.getQuest(name) != null) {
            sender.sendMessage(translate("&cQuest by that name already exists. Please choose another name."))
            return
        }
        val newQuest = Quest(name, quest)
        plugin.managers.questeCache.addQuest(newQuest)
        sender.sendMessage(translate("&aThe quest &6$name&a has been copied from " + quest.name + " and created."))
    }

    @Subcommand("create")
    fun onCreate(sender: CommandSender, name: String) {
        if (plugin.managers.questeCache.getQuest(name) != null) {
            sender.sendMessage(translate("&cQuest by that name already exists. Please choose another name."))
            return
        }
        if (name.contains(' ')) {
            sender.sendMessage(translate("&cQuest names must be unique with no spaces. You can set the display name afterwards."))
            return
        }
        val quest = Quest(name)
        plugin.managers.questeCache.addQuest(quest)
        quest.save(plugin)
        sender.sendMessage(translate("&aThe quest &6$name&a has been created. Use /quest edit $name to edit it."))
    }

    @Subcommand("delete|remove")
    @CommandCompletion("@quests")
    fun onDelete(sender: CommandSender, quest: Quest) {
        plugin.managers.storageManager.cachedAccounts.forEach { (_, account) ->
            account.removeActiveQuest(quest)
            account.removeCompletedQuest(quest)
        }

        plugin.managers.questeCache.quests.forEach { (_, theQuest) ->
            var removeRequirement: QuestQuestRequirement? = null
            for (requirement in theQuest.requirements) {
                if (requirement is QuestQuestRequirement
                    && requirement.questName == quest.name) {
                    removeRequirement = requirement
                    break
                }
            }

            if (removeRequirement != null) {
                theQuest.removeRequirement(removeRequirement)
            }
        }

        plugin.managers.questeCache.removeQuest(quest)
        if (quest.delete()) {
            sender.sendMessage(translate("&aThe quest &6" + quest.name + "&a has been deleted."))
        } else {
            sender.sendMessage(translate("&cCould not delete the quest. Does the file exist still?"))
        }
    }

    @Subcommand("edit")
    @CommandCompletion("@quests")
    fun onEdit(player: Player, quest: Quest) {
        QuestCreateGUI(plugin, player, quest).open()
    }

    @Subcommand("setdummy")
    @CommandCompletion("@quests")
    fun onSetDummy(sender: CommandSender, quest: Quest) {
        quest.isDummy = !quest.isDummy
        sender.sendMessage(translate("&aDummy has been set to &6" + quest.isDummy + "&a."))
    }

    @Subcommand("setcooldown")
    @CommandCompletion("@quests @range:160")
    fun onSetCooldown(sender: CommandSender, quest: Quest, cooldown: Int) {
        quest.cooldown = cooldown
        sender.sendMessage(translate("&aCooldown has been set to &6$cooldown&a."))
    }

    @Subcommand("addrequirement")
    @CommandCompletion("@requirements @quests")
    fun onAddRequirement(player: Player, requirementName: String, quest: Quest) {
        val registry = plugin.managers.getQuestRegistry(QuestRequirementRegistry::class.java)
        if (registry is QuestRequirementRegistry) {
            val requirement = registry.getNew(requirementName, plugin)
            if (requirement != null) {
                if (requirement is ItemRequirement) {
                    requirement.item = player.inventory.itemInMainHand
                }
                quest.addRequirement(requirement)
                quest.save(plugin)
                player.sendMessage(translate("&aAdded new requirement " + requirement.name + " to " + quest.name + "."))
            }
        }
    }

    @Subcommand("addobjective")
    @CommandCompletion("@objectives @quests")
    fun onAddObjective(sender: CommandSender, objectiveName: String, quest: Quest) {
        val registry = plugin.managers.getQuestRegistry(QuestObjectiveRegistry::class.java)
        if (registry is QuestObjectiveRegistry) {
            val objective = registry.getNewObjective(objectiveName, plugin, quest)
            if (objective != null) {
                if (!quest.addObjective(objective)) {
                    sender.sendMessage(translate("&cThat quest is a dummy quest and cannot have multiple objectives of the same type."))
                    return
                }
                quest.save(plugin)
                sender.sendMessage(translate("&aAdded new objective " + objective.name + " to " + quest.name + "."))
            }
        }
    }

    @Subcommand("addobjectivepreset")
    @CommandCompletion("@objectives-presets @quests")
    fun onAddObjectivePreset(sender: CommandSender, presetName: String, quest: Quest) {
        val registry = plugin.managers.getQuestRegistry(QuestObjectiveRegistry::class.java)
        val objective: Any? = registry?.loadPreset(presetName)
        if (objective is QuestObjective) {
            objective.setProgress(HashMap<UUID, Int>())
            quest.addObjective(objective)
            Bukkit.getPluginManager().registerEvents(objective, plugin)
            sender.sendMessage(translate("&aObjective preset has been added to quest."))
        }
    }

    @Subcommand("setrestart")
    @CommandCompletion("@quests")
    fun onSetRestart(sender: CommandSender, quest: Quest, restart: Boolean) {
        quest.setCanRestart(restart)
        quest.save(plugin)
        sender.sendMessage(translate("&aQuest " + quest.name + " has had 'canRestart' set to: " + quest.canRestart() + "."))
    }

    @Subcommand("addquestrequirement")
    @CommandCompletion("@quests @quests")
    fun onAddQuestRequirement(sender: CommandSender, quest: Quest, requirement: Quest) {
        val registry = plugin.managers.getQuestRegistry(QuestRequirementRegistry::class.java) ?: return
        val questRequirement: QuestQuestRequirement = registry.getNew("QuestQuestRequirement", plugin) as QuestQuestRequirement
        questRequirement.questName = requirement.name
        quest.addRequirement(questRequirement)
        quest.save(plugin)
        sender.sendMessage(translate("&aAdded required quest " + requirement.name + " to quest " + quest.name + "."))
    }

    @Subcommand("addreward")
    @CommandCompletion("@quests @rewards")
    fun onAddReward(player: Player, quest: Quest, rewardName: String) {
        val registry = plugin.managers.getQuestRegistry(QuestRewardRegistry::class.java) ?: return
        val reward = registry.getNew(rewardName, plugin)
        if (reward != null) {
            if (reward is ItemReward) {
                reward.item = player.inventory.itemInMainHand
            }
            quest.addReward(reward as QuestReward)
            quest.save(plugin)
            player.sendMessage(translate("&aAdded reward &6" + reward.name + "&a to quest &6" + quest.name + "&a."))
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

    @Subcommand("settime")
    @CommandCompletion("@quests @range:20")
    fun onSetTime(sender: CommandSender, quest: Quest, time: Int) {
        val currentTime = quest.time
        quest.time = time
        sender.sendMessage(translate("&aSet time from &6$currentTime &ato &6$time&a."))
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

    @Subcommand("addplayer|start")
    @CommandCompletion("@players @quests @options")
    fun onAddPlayer(sender: CommandSender, playerName: String, quest: Quest, arguments: Array<String>) {
        val player = Bukkit.getPlayer(playerName)
        if (player != null) {
            if (arguments.contains("--force")) {
                quest.forceStart(player)
                sender.sendMessage(translate("&aPlayer " + playerName + " now has the quest " + quest.name + ". &c(FORCED)"))
                return
            }

            quest.tryStart(player).thenAccept { denyReason ->
                if (denyReason == Quest.DenyReason.NONE) {
                    sender.sendMessage(translate("&aPlayer " + playerName + " now has the quest " + quest.name + "."))
                } else {
                    sender.sendMessage(translate("&cCould not start the quest for this player. Reason: $denyReason"))
                    sender.sendMessage(translate("&aTIP: &fTry running with &6--force &fto force a quest start."))
                }
            }
        } else {
            sender.sendMessage(translate("&cThat player is not online."))
        }
    }

    @Subcommand("seticon")
    @CommandCompletion("@quests")
    fun onSetIcon(sender: CommandSender, quest: Quest, material: Material) {
        val currentIcon = quest.icon
        quest.icon = material
        sender.sendMessage(translate("&aQuest icon has been set to &6" + quest.icon + "&a from &6" + currentIcon + "&a."))
    }

    @Subcommand("setdescription")
    @CommandCompletion("@quests")
    fun onSetDescription(sender: CommandSender, quest: Quest, description: String) {
        val currentDescription = quest.description
        quest.description = description
        sender.sendMessage(translate("&aQuest description has been set to &6" + quest.description + "&a from &6" + currentDescription + "&a."))
    }
}
