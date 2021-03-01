package com.convallyria.queste.quest.reward;

import com.convallyria.queste.Queste;
import com.convallyria.queste.gui.GuiEditable;
import com.convallyria.queste.quest.Quest;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class AcceptQuestReward extends QuestReward {

    @GuiEditable("The Quest")
    private final String questName;
    
    public AcceptQuestReward() {
        this.questName = "";
    }
    
    public AcceptQuestReward(String questName) {
        this.questName = questName;
    }
    
    @Override
    public void award(Player player) {
        if (this.questName == null || this.questName.isEmpty()) return;
        Queste plugin = JavaPlugin.getPlugin(Queste.class);
        plugin.getManagers().getStorageManager().getAccount(player.getUniqueId()).thenAccept(account -> {
            Quest quest = plugin.getManagers().getQuesteCache().getQuest(questName);
            if (quest == null) {
                plugin.getLogger().warning("Could not find quest " + questName + ".");
                return;
            }
            quest.forceStart(player);
        });
    }
    
    @Override
    public String getName() {
        return "Accept Quest";
    }
}
