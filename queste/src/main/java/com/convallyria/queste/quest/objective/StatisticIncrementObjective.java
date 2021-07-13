package com.convallyria.queste.quest.objective;

import com.convallyria.queste.api.IQuesteAPI;
import com.convallyria.queste.gui.GuiEditable;
import com.convallyria.queste.quest.Quest;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;

import java.util.Locale;

public class StatisticIncrementObjective extends QuestObjective {

    @GuiEditable(value = "Statistic", type = GuiEditable.GuiEditableType.CHAT)
    private Statistic statistic;

    public StatisticIncrementObjective(IQuesteAPI plugin, Quest quest) {
        super(plugin, quest);
        this.statistic = Statistic.JUMP;
    }

    @EventHandler
    public void onIncrement(PlayerStatisticIncrementEvent event) {
        Player player = event.getPlayer();
        if (event.getStatistic() == statistic) {
            if (hasCompleted(player)) return;
            this.increment(player);
        }
    }

    @Override
    public String getName() {
        return StringUtils.capitalize(statistic.name().replace('_', ' ').toLowerCase(Locale.ROOT));
    }
}
