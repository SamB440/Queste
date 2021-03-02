package com.convallyria.queste.quest.objective;

import com.convallyria.queste.Queste;
import com.convallyria.queste.gui.GuiEditable;
import com.convallyria.queste.quest.Quest;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

public final class BreakBlockQuestObjective extends QuestObjective {

    @GuiEditable("Block Type")
    private Material blockType;

    public BreakBlockQuestObjective(Queste plugin, Quest quest) {
        super(plugin, quest);
    }

    @EventHandler(ignoreCancelled = true)
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
