package com.convallyria.queste.quest.start;

import com.convallyria.queste.api.IQuesteAPI;
import com.convallyria.queste.gui.GuiEditable;
import com.convallyria.queste.quest.Quest;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class NPCQuestStart extends QuestStart {

    @GuiEditable("NPC id")
    private int npcId;
    @GuiEditable("Show Particles")
    private boolean showNpcParticles;

    public NPCQuestStart(IQuesteAPI plugin, Quest quest) {
        super(plugin, quest);
    }

    @Override
    public String getName() {
        return "NPC";
    }

    @EventHandler
    public void onClick(NPCRightClickEvent event) {
        NPC npc = event.getNPC();
        Player player = event.getClicker();
        if (npc.getId() == npcId) {
            getQuest().tryStart(player);
        }
    }

    public int getNpcId() {
        return npcId;
    }

    public void setNpcId(int npcId) {
        this.npcId = npcId;
    }

    public void setShowNpcParticles(boolean showNpcParticles) {
        this.showNpcParticles = showNpcParticles;
    }

    public boolean showNpcParticles() {
        return showNpcParticles;
    }
}
