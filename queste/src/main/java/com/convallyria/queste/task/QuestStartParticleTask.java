package com.convallyria.queste.task;

import com.convallyria.queste.Queste;
import com.convallyria.queste.quest.start.NPCQuestStart;
import com.convallyria.queste.quest.start.QuestStart;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class QuestStartParticleTask implements Runnable {

    private final Queste plugin;

    public QuestStartParticleTask(Queste plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        plugin.getManagers().getQuesteCache().getQuests().forEach((name, quest) -> {
            for (QuestStart starter : quest.getStarters()) {
                if (starter instanceof NPCQuestStart npcQuestStart) {
                    if (!npcQuestStart.showNpcParticles()) continue;
                    NPC npc = CitizensAPI.getNPCRegistry().getById(npcQuestStart.getNpcId());
                    Location location = npc.getStoredLocation();
                    if (location.getWorld() == null) continue;
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        plugin.getManagers().getStorageManager().getAccount(player.getUniqueId()).thenAccept(account -> {
                            if (!account.getAllQuests().contains(quest)) {
                                player.spawnParticle(Particle.VILLAGER_HAPPY, location.add(0, 2.25, 0), 10);
                            }
                        });
                    }
                }
            }
        });
    }
}
