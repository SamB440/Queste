package com.convallyria.queste.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.CommandHelp
import co.aikar.commands.annotation.*
import com.convallyria.queste.Queste
import com.convallyria.queste.colour.ColourScheme
import com.convallyria.queste.gui.ObjectiveSelectGUI
import com.convallyria.queste.quest.Quest
import com.convallyria.queste.quest.objective.LocationObjective
import com.convallyria.queste.quest.objective.QuestObjectiveRegistry
import com.convallyria.queste.quest.objective.RegionObjective
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import java.util.*

@CommandAlias("questobjective")
@CommandPermission("queste.objective|queste.admin")
class QuestObjectiveCommand(private val plugin: Queste) : BaseCommand(), IQuesteCommand {

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
                + "List of all available objectives (" + secondaryColour + "name" + primaryColour + ", "
                + pluginColour + "plugin" + primaryColour + "): "))
        val testQuest = Quest(UUID.randomUUID().toString())
        val registry = plugin.managers.getQuestRegistry(QuestObjectiveRegistry::class.java)
        if (registry is QuestObjectiveRegistry) {
            for (objectiveName in registry.get().keys) {
                val objective = registry.getNewObjective(objectiveName, plugin, testQuest)
                if (objective != null) {
                    HandlerList.unregisterAll(objective)
                    val pluginRequirement =
                        if (objective.pluginRequirement != null) "" + pluginColour + " (" + objective.pluginRequirement + ")"
                        else ""
                    sender.sendMessage(translate(" " + secondaryColour + "- " + objective.name + pluginRequirement))
                }
            }
        }
    }

    @Subcommand("setdisplayname")
    @CommandCompletion("@objectives @quests")
    fun onSetDisplayName(player: Player, objectiveName: String, quest: Quest, displayName: String) {
        if (testGui(player, quest, objectiveName, true, "setDisplayName", String::class.java, displayName)) {
            return
        }

        quest.objectives.forEach { questObjective ->
            if (questObjective.javaClass.simpleName == objectiveName) {
                questObjective.displayName = displayName
                quest.save(plugin)
                player.sendMessage(translate("&aSet objective " + questObjective.name + " display name to " + questObjective.displayName + "."))
            }
        }
    }

    @Subcommand("setregion")
    @CommandCompletion("@objectives @quests @nothing")
    fun onSetRegion(player: Player, objectiveName: String, quest: Quest, region: String) {
        val testQuest = Quest(UUID.randomUUID().toString())
        val registry = plugin.managers.getQuestRegistry(QuestObjectiveRegistry::class.java)
        if (registry is QuestObjectiveRegistry) {
            val objective = registry.getNewObjective(objectiveName, plugin, testQuest) ?: return
            HandlerList.unregisterAll(objective)
            if (objective.pluginRequirement != null && objective.pluginRequirement == "RPGRegions") {
                if (testGui(player, quest, objectiveName, false, "setRegion", String::class.java, region)) {
                    return
                }

                quest.objectives.forEach { questObjective ->
                    if (questObjective.javaClass.simpleName == objectiveName
                        && questObjective is RegionObjective) {
                        questObjective.region = region
                        quest.save(plugin)
                        player.sendMessage(translate("&aSet objective " + objective.name + " region to " + questObjective.region + "."))
                    }
                }
            } else {
                player.sendMessage(translate("&cObjective type " + objective.name + " does not support RPGRegions."))
            }
        }
    }

    @Subcommand("setlocation")
    @CommandCompletion("@objectives @quests")
    fun onSetLocation(player: Player, objectiveName: String, quest: Quest, where: String) {
        val testQuest = Quest(UUID.randomUUID().toString())
        val registry = plugin.managers.getQuestRegistry(QuestObjectiveRegistry::class.java)
        if (registry is QuestObjectiveRegistry) {
            val objective = registry.getNewObjective(objectiveName, plugin, testQuest) ?: return
            HandlerList.unregisterAll(objective)
            if (objective is LocationObjective) {
                val location = if (where == "TARGET") player.getTargetBlockExact(6)?.location else player.location
                if (testGui(player, quest, objectiveName, false, "setLocation", Location::class.java, location)) {
                    return
                }

                quest.objectives.forEach { questObjective ->
                    if (questObjective.javaClass.simpleName == objectiveName
                        && questObjective is LocationObjective) {
                        questObjective.location = location
                        quest.save(plugin)
                        player.sendMessage(translate("&aSet objective " + objective.name + " location to " + questObjective.location.toString() + "."))
                    }
                }
            } else {
                player.sendMessage(translate("&cObjective type " + objective.name + " does not support locations."))
            }
        }
    }

    @Subcommand("setcompletion")
    @CommandCompletion("@objectives @quests @range:20")
    fun onSetCompletion(player: Player, objectiveName: String, quest: Quest, completion: Int) {
        if (testGui(player, quest, objectiveName, true, "setCompletionAmount", Int::class.java, completion)) {
            return
        }

        for (questObjective in quest.objectives) {
            if (questObjective.javaClass.simpleName == objectiveName) {
                questObjective.completionAmount = completion
                quest.save(plugin)
                player.sendMessage(translate("&aSet objective &6" + questObjective.name + "&a completion requirement to &6" + completion + "&a."))
                return
            }
        }
        player.sendMessage(translate("&cThe quest " + quest.name + " does not have the objective &6" + objectiveName + "."))
    }

    @Subcommand("setstorykey")
    @CommandCompletion("@objectives @quests @range:20")
    fun onSetStoryKey(player: Player, objectiveName: String, quest: Quest, storyKey: Int) {
        if (!quest.isStoryMode) {
            player.sendMessage(translate("&cThat quest does not have storyMode set to &6true&c."))
            return
        }

        if (testGui(player, quest, objectiveName, true, "setStoryModeKey", Int::class.java, storyKey)) {
            return
        }

        for (questObjective in quest.objectives) {
            if (questObjective.javaClass.simpleName == objectiveName) {
                questObjective.storyModeKey = storyKey
                quest.save(plugin)
                player.sendMessage(translate("&aSet objective &6" + questObjective.name + "&a story key to &6" + storyKey + "&a."))
                break
            }
        }
    }

    private fun testGui(player: Player, quest: Quest, objectiveName: String,
                        superclass: Boolean, method: String, methodData: Class<*>, vararg data: Any?): Boolean {
        val registry = plugin.managers.getQuestRegistry(QuestObjectiveRegistry::class.java)
        if (registry is QuestObjectiveRegistry) {
            val type = registry.get()[objectiveName]
            if (quest.getObjectivesFromType(type).size > 1) {
                val clazz = registry.getNewObjective(type, plugin, quest)
                if (clazz != null) {
                    val methodClazz = if (superclass) clazz::class.java.superclass else clazz::class.java
                    val gui = ObjectiveSelectGUI(plugin, player, quest, type, methodClazz.getDeclaredMethod(method, methodData), *data)
                    gui.open()
                    player.sendMessage(translate("&aOpened a GUI for selection because multiple objectives of this type exist."))
                    return true
                }
            }
        }

        return false
    }
}