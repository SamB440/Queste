package com.convallyria.queste.quest.objective;

import com.convallyria.queste.Queste;
import com.convallyria.queste.gui.GuiEditable;
import com.convallyria.queste.quest.Quest;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;

public final class PlaceBlockQuestObjective extends QuestObjective {

    @GuiEditable("Block Type")
    private Material blockType;

    public PlaceBlockQuestObjective(Queste plugin, Quest quest) {
        super(plugin, quest);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (this.hasCompleted(player)) return;
        boolean flag = blockType == null || blockType == event.getBlockPlaced().getType();
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
        return "Place Block";
    }
}
