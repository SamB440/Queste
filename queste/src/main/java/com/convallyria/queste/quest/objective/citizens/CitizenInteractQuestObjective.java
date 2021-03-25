package com.convallyria.queste.quest.objective.citizens;

import com.convallyria.queste.Queste;
import com.convallyria.queste.api.IQuesteAPI;
import com.convallyria.queste.quest.Quest;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public final class CitizenInteractQuestObjective extends CitizensQuestObjective {

    public CitizenInteractQuestObjective(IQuesteAPI api, Quest quest) {
        super(api, quest);
    }

    @EventHandler
    public void onRightClick(NPCRightClickEvent event) {
        Player player = event.getClicker();
        NPC npc = event.getNPC();
        if (npc.getId() == getNpcId()) {
            if (this.hasCompleted(player)) return;
            this.increment(player);
        }
    }

    @Override
    public String getName() {
        return "Interact with NPC";
    }

    @Override
    public String getPluginRequirement() {
        return "Citizens";
    }
}
