package com.convallyria.queste.listener;

import com.convallyria.queste.api.event.QuestCompleteEvent;
import com.convallyria.queste.api.event.QuestObjectiveCompleteEvent;
import com.convallyria.queste.api.event.QuestStartEvent;
import com.convallyria.queste.config.Configurations;
import com.convallyria.queste.quest.Quest;
import com.convallyria.queste.quest.objective.QuestObjective;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class QuestAdvancementListener implements Listener {

    @EventHandler
    public void onObjectiveComplete(QuestObjectiveCompleteEvent event) {
        Player player = event.getPlayer();
        QuestObjective objective = event.getObjective();
        this.award(player, objective);
    }

    @EventHandler
    public void onQuestComplete(QuestCompleteEvent event) {
        Player player = event.getPlayer();
        Quest quest = event.getQuest();
        this.award(player, quest);
    }

    @EventHandler
    public void onQuestStart(QuestStartEvent event) {
        Player player = event.getPlayer();
        Quest quest = event.getQuest();
        this.award(player, quest);
    }

    private void award(Player player, Keyed key) {
        if (Configurations.GENERATE_ADVANCEMENTS.getBoolean()) {
            Advancement advancement = Bukkit.getAdvancement(key.getKey());
            if (advancement == null) return;
            AdvancementProgress progress = player.getAdvancementProgress(advancement);
            progress.awardCriteria("impossible");
        }
    }
}
