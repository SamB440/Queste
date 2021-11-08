package com.convallyria.queste.quest.objective;

import com.convallyria.queste.api.IQuesteAPI;
import com.convallyria.queste.gui.GuiEditable;
import com.convallyria.queste.quest.Quest;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.jetbrains.annotations.Nullable;

public final class BucketFillObjective extends QuestObjective {

    @GuiEditable(value = "Liquid Type", icon = Material.BUCKET)
    private Material blockType;

    public BucketFillObjective(IQuesteAPI api, Quest quest) {
        super(api, quest);
    }

    @EventHandler
    public void onFill(PlayerBucketFillEvent event) {
        Player player = event.getPlayer();
        if (this.hasCompleted(player)) return;
        boolean flag = blockType == null || event.getBlockClicked().getType() == blockType;
        if (flag) {
            this.increment(player);
        }
    }

    @Nullable
    public Material getBlockType() {
        return blockType;
    }

    public void setBlockType(Material blockType) {
        this.blockType = blockType;
    }

    @Override
    public String getName() {
        return "Fill Bucket";
    }
}
