package com.convallyria.queste.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.CommandHelp
import co.aikar.commands.annotation.*
import com.convallyria.queste.Queste
import com.convallyria.queste.colour.ColourScheme
import com.convallyria.queste.quest.Quest
import com.convallyria.queste.quest.objective.LocationObjective
import com.convallyria.queste.quest.objective.QuestObjective
import com.convallyria.queste.quest.objective.RegionObjective
import org.bukkit.Bukkit
import org.bukkit.ChatColor
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

    @Subcommand("setregion")
    @CommandCompletion("@objectives @quests @nothing")
    fun onSetRegion(sender: CommandSender, objective: QuestObjective.QuestObjectiveEnum, quest: Quest, region: String) {
        if (objective.pluginRequirement != null && objective.pluginRequirement == "RPGRegions") {
            quest.objectives.forEach { questObjective ->
                if (questObjective.type == objective) {
                    if (questObjective is RegionObjective) {
                        questObjective.region = region
                        quest.save(plugin)
                        sender.sendMessage(translate("&aSet objective " + objective.getName() + " region to " + questObjective.region + "."))
                    }
                }
            }
        } else {
            sender.sendMessage(translate("&cObjective type " + objective.getName() + " does not support RPGRegions."))
        }
    }

    @Subcommand("setlocation")
    @CommandCompletion("@objectives @quests")
    fun onSetRegion(player: Player, objective: QuestObjective.QuestObjectiveEnum, quest: Quest, where: String) {
        if (objective.pluginRequirement != null && objective.getNewObjective(plugin, quest) is LocationObjective) {
            quest.objectives.forEach { questObjective ->
                if (questObjective.type == objective) {
                    if (questObjective is LocationObjective) {
                        val location = if (where == "TARGET") player.getTargetBlockExact(6)?.location else player.location
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
    fun onSetCompletion(sender: CommandSender, objective: QuestObjective.QuestObjectiveEnum, quest: Quest, completion: Int) {
        for (questObjective in quest.objectives) {
            if (objective == questObjective.type) {
                questObjective.completionAmount = completion
                quest.save(plugin)
                sender.sendMessage(translate("&aSet objective &6" + questObjective.type.getName() + "&a completion requirement to &6" + completion + "&a."))
                return
            }
        }
        sender.sendMessage(translate("&cThe quest " + quest.name + " does not have the objective &6" + objective.getName() + "."))
    }

    @Subcommand("setstorykey")
    @CommandCompletion("@objectives @quests @range:20")
    fun onSetStoryKey(sender: CommandSender, objective: QuestObjective.QuestObjectiveEnum, quest: Quest, storyKey: Int) {
        for (questObjective in quest.objectives) {
            if (objective == questObjective.type) {
                if (!quest.isStoryMode) {
                    sender.sendMessage(translate("&cThat quest does not have storyMode set to &6true&c."))
                    return
                }
                questObjective.storyModeKey = storyKey
                quest.save(plugin)
                sender.sendMessage(translate("&aSet objective &6" + questObjective.type.getName() + "&a story key to &6" + storyKey + "&a."))
                break
            }
        }
    }
}