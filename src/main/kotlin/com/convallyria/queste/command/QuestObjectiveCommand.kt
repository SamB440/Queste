package com.convallyria.queste.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.CommandHelp
import co.aikar.commands.annotation.*
import com.convallyria.queste.Queste
import com.convallyria.queste.colour.ColourScheme
import com.convallyria.queste.gui.ObjectiveSelectGUI
import com.convallyria.queste.quest.Quest
import com.convallyria.queste.quest.objective.LocationObjective
import com.convallyria.queste.quest.objective.QuestObjective
import com.convallyria.queste.quest.objective.RegionObjective
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandAlias("questobjective")
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
        for (objective in QuestObjective.QuestObjectiveEnum.values()) {
            val pluginRequirement =
                if (objective.pluginRequirement != null) "" + pluginColour + " (" + objective.pluginRequirement + ")"
                else ""
            sender.sendMessage(translate(" " + secondaryColour + "- " + objective.getName() + pluginRequirement))
        }
    }

    @Subcommand("setdisplayname")
    @CommandCompletion("@objectives @quests")
    fun onSetDisplayName(player: Player, objective: QuestObjective.QuestObjectiveEnum, quest: Quest, displayName: String) {
        if (testGui(player, quest, objective, true, "setDisplayName", String::class.java, displayName)) {
            return
        }

        quest.objectives.forEach { questObjective ->
            if (questObjective.type == objective) {
                if (questObjective is RegionObjective) {
                    questObjective.displayName = displayName
                    quest.save(plugin)
                    player.sendMessage(translate("&aSet objective " + objective.getName() + " display name to " + questObjective.displayName + "."))
                }
            }
        }
    }

    @Subcommand("setregion")
    @CommandCompletion("@objectives @quests @nothing")
    fun onSetRegion(player: Player, objective: QuestObjective.QuestObjectiveEnum, quest: Quest, region: String) {
        if (objective.pluginRequirement != null && objective.pluginRequirement == "RPGRegions") {
            if (testGui(player, quest, objective, false, "setRegion", String::class.java, region)) {
                return
            }

            quest.objectives.forEach { questObjective ->
                if (questObjective.type == objective) {
                    if (questObjective is RegionObjective) {
                        questObjective.region = region
                        quest.save(plugin)
                        player.sendMessage(translate("&aSet objective " + objective.getName() + " region to " + questObjective.region + "."))
                    }
                }
            }
        } else {
            player.sendMessage(translate("&cObjective type " + objective.getName() + " does not support RPGRegions."))
        }
    }

    @Subcommand("setlocation")
    @CommandCompletion("@objectives @quests")
    fun onSetLocation(player: Player, objective: QuestObjective.QuestObjectiveEnum, quest: Quest, where: String) {
        if (objective.pluginRequirement != null && objective.getNewObjective(plugin, quest) is LocationObjective) {
            val location = if (where == "TARGET") player.getTargetBlockExact(6)?.location else player.location
            if (testGui(player, quest, objective, false, "setLocation", Location::class.java, location)) {
                return
            }

            quest.objectives.forEach { questObjective ->
                if (questObjective.type == objective) {
                    if (questObjective is LocationObjective) {
                        questObjective.location = location
                        quest.save(plugin)
                        player.sendMessage(translate("&aSet objective " + objective.getName() + " location to " + questObjective.location.toString() + "."))
                    }
                }
            }
        } else {
            player.sendMessage(translate("&cObjective type " + objective.getName() + " does not support locations."))
        }
    }

    @Subcommand("setcompletion")
    @CommandCompletion("@objectives @quests @range:20")
    fun onSetCompletion(player: Player, objective: QuestObjective.QuestObjectiveEnum, quest: Quest, completion: Int) {
        if (testGui(player, quest, objective, true, "setCompletionAmount", Int::class.java, completion)) {
            return
        }

        for (questObjective in quest.objectives) {
            if (objective == questObjective.type) {
                questObjective.completionAmount = completion
                quest.save(plugin)
                player.sendMessage(translate("&aSet objective &6" + questObjective.type.getName() + "&a completion requirement to &6" + completion + "&a."))
                return
            }
        }
        player.sendMessage(translate("&cThe quest " + quest.name + " does not have the objective &6" + objective.getName() + "."))
    }

    @Subcommand("setstorykey")
    @CommandCompletion("@objectives @quests @range:20")
    fun onSetStoryKey(player: Player, objective: QuestObjective.QuestObjectiveEnum, quest: Quest, storyKey: Int) {
        if (!quest.isStoryMode) {
            player.sendMessage(translate("&cThat quest does not have storyMode set to &6true&c."))
            return
        }

        if (testGui(player, quest, objective, true, "setStoryModeKey", Int::class.java, storyKey)) {
            return
        }

        for (questObjective in quest.objectives) {
            if (objective == questObjective.type) {
                questObjective.storyModeKey = storyKey
                quest.save(plugin)
                player.sendMessage(translate("&aSet objective &6" + questObjective.type.getName() + "&a story key to &6" + storyKey + "&a."))
                break
            }
        }
    }

    private fun testGui(player: Player, quest: Quest, objective: QuestObjective.QuestObjectiveEnum,
                        superclass: Boolean, method: String, methodData: Class<*>, vararg data: Any?): Boolean {
        if (quest.getObjectivesFromType(objective).size > 1) {
            val clazz = objective.getNewObjective(plugin, quest)
            if (clazz != null) {
                val methodClazz = if (superclass) clazz::class.java.superclass else clazz::class.java
                val gui = ObjectiveSelectGUI(plugin, player, quest, objective, methodClazz.getDeclaredMethod(method, methodData), *data)
                gui.open()
                player.sendMessage(translate("&aOpened a GUI for selection because multiple objectives of this type exist."))
                return true
            }
        }
        return false
    }
}