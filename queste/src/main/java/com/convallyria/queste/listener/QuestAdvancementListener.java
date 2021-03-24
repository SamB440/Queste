package com.convallyria.queste.listener;

import com.convallyria.queste.api.event.QuestCompleteEvent;
import com.convallyria.queste.api.event.QuestObjectiveCompleteEvent;
import com.convallyria.queste.api.event.QuestStartEvent;
import com.convallyria.queste.config.Configurations;
import com.convallyria.queste.quest.Quest;
import org.bukkit.Bukkit;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class QuestAdvancementListener implements Listener {

    @EventHandler
    public void onObjectiveComplete(QuestObjectiveCompleteEvent event) {
        Player player = event.getPlayer();
        Quest quest = event.getObjective().getQuest();
        if (quest == null) return;
        if (Configurations.GENERATE_ADVANCEMENTS.getBoolean()) {
            Advancement advancement = Bukkit.getAdvancement(quest.getKey());
            if (advancement == null) return;
            AdvancementProgress progress = player.getAdvancementProgress(advancement);
            progress.awardCriteria("impossible");
        }
    }

    @EventHandler
    public void onQuestComplete(QuestCompleteEvent event) {
        Player player = event.getPlayer();
        Quest quest = event.getQuest();
        if (Configurations.GENERATE_ADVANCEMENTS.getBoolean()) {
            Advancement advancement = Bukkit.getAdvancement(quest.getKey());
            if (advancement == null) return;
            AdvancementProgress progress = player.getAdvancementProgress(advancement);
            progress.awardCriteria("impossible");
        }
    }

    @EventHandler
    public void onQuestStart(QuestStartEvent event) {
        Player player = event.getPlayer();
        Quest quest = event.getQuest();
        if (Configurations.GENERATE_ADVANCEMENTS.getBoolean()) {
            Advancement advancement = Bukkit.getAdvancement(quest.getKey());
            if (advancement == null) return;
            AdvancementProgress progress = player.getAdvancementProgress(advancement);
            progress.awardCriteria("impossible");
        }
    }
}
