package com.convallyria.queste.quest;

import com.convallyria.queste.Queste;
import com.convallyria.queste.quest.objective.QuestObjective;
import com.google.gson.Gson;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public final class Quest  {

    private final String name;
    private final List<QuestObjective> objectives;

    public Quest(String name) {
        this.name = name;
        this.objectives = new ArrayList<>();
    }

    public void addObjective(QuestObjective objective) {
        objectives.add(objective);
    }

    public void removeObjective(QuestObjective objective) {
        objectives.remove(objective);
    }

    public List<QuestObjective> getObjectives() {
        return objectives;
    }

    public String getName() {
        return name;
    }

    public boolean isCompleted(@NotNull Player player) {
        boolean objectivesCompleted = true;
        for (QuestObjective objective : objectives) {
            if (!objective.hasCompleted(player)) {
                objectivesCompleted = false;
                break;
            }
        }
        return objectivesCompleted;
    }

    public boolean tryComplete(@NotNull Player player) {
        if (isCompleted(player)) {
            player.sendTitle(ChatColor.GREEN + "Quest Completed", getName(), 40, 60, 40);
            return true;
        }
        return false;
    }

    public boolean save(Queste plugin) {
        File file = new File(plugin.getDataFolder() + "/quests/" + this.getName() + ".json");
        try {
            Writer writer = new FileWriter(file);
            Gson gson = plugin.getGson();
            gson.toJson(this, writer);
            writer.flush();
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
