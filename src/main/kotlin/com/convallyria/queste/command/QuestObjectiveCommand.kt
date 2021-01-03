package com.convallyria.queste.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.CommandHelp
import co.aikar.commands.annotation.*
import com.convallyria.queste.Queste
import com.convallyria.queste.quest.Quest
import com.convallyria.queste.quest.objective.QuestObjective
import com.convallyria.queste.quest.objective.RegionObjective
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

@CommandAlias("questobjective")
class QuestObjectiveCommand(private val plugin: Queste) : BaseCommand(), IQuesteCommand {

    @Default
    @HelpCommand
    fun onDefault(commandHelp : CommandHelp) {
        commandHelp.showHelp()
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

    @Subcommand("setcompletion")
    @CommandCompletion("@objectives @quests @range:20")
    fun onSetCompletion(sender: CommandSender, objective: QuestObjective.QuestObjectiveEnum, quest: Quest, completion: Int) {
        for (questObjective in quest.objectives) {
            if (objective == questObjective.type) {
                questObjective.completionAmount = completion
                quest.save(plugin)
                sender.sendMessage(translate("&aSet objective " + questObjective.type.getName() + " completion requirement to " + completion + "."))
                return
            }
        }
        sender.sendMessage(translate("&cThe quest " + quest.name + " does not have the objective &6" + objective.getName() + "."))
    }
}