package com.convallyria.queste.quest.objective;

import com.convallyria.queste.api.IQuesteAPI;
import com.convallyria.queste.gui.GuiEditable;
import com.convallyria.queste.quest.Quest;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;

public final class BreakBlockQuestObjective extends QuestObjective {

    @GuiEditable("Block Type")
    private Material blockType;

    public BreakBlockQuestObjective(IQuesteAPI api, Quest quest) {
        super(api, quest);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (this.hasCompleted(player)) return;
        boolean flag = blockType == null || blockType == event.getBlock().getType();
        if (flag) {
            this.increment(player);
        }
    }

    public Material getBlockType() {
        return blockType;
    }

    public void setBlockType(Material blockType) {
        this.blockType = blockType;
    }

    @Override
    public String getName() {
        return "Break Block";
    }
}
