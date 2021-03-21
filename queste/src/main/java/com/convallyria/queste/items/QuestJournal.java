package com.convallyria.queste.items;

import com.convallyria.queste.managers.data.account.QuesteAccount;
import com.convallyria.queste.quest.Quest;
import com.convallyria.queste.quest.objective.QuestObjective;
import com.convallyria.queste.quest.reward.QuestReward;
import com.convallyria.queste.translation.Translations;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.upperlevel.spigot.book.BookUtil;

import java.util.ArrayList;
import java.util.List;

public class QuestJournal {

    public static ItemStack getQuestJournal(final QuesteAccount account) {
        Player player = Bukkit.getPlayer(account.getUuid());
        BookUtil.BookBuilder builder = BookUtil.writtenBook().author(player.getName()).title(Translations.JOURNAL_TITLE.get(player));
        List<BaseComponent[]> pages = new ArrayList<>();
        if (account.getActiveQuests().isEmpty()) {
            pages.add(new BaseComponent[]{new TextComponent(ChatColor.DARK_RED + "You have no accepted quests!")});
            return builder.pages(pages).build();
        }

        for (Quest quest : account.getActiveQuests()) {
            int lines = 0;
            BookUtil.PageBuilder pageBuilder = new BookUtil.PageBuilder();
            pageBuilder.add(BookUtil.TextBuilder.of(quest.getDisplayName())
                    .color(ChatColor.DARK_AQUA)
                    .style(ChatColor.BOLD)
                    .onHover(BookUtil.HoverAction.showText(ChatColor.GOLD + quest.getDescription())).build());
            lines++;
            pageBuilder.add("\nObjectives:");
            for (QuestObjective objective : quest.getObjectives()) {
                pageBuilder.add(BookUtil.TextBuilder.of("\n" + ChatColor.RESET + objective.getDisplayName())
                        .onHover(BookUtil.HoverAction.showText(ChatColor.WHITE + "" + objective.getIncrement(player) + "/" + objective.getCompletionAmount()))
                        .build());
                lines++;
            }
            pageBuilder.add("\nRewards:");
            for (QuestReward reward : quest.getRewards()) {
                pageBuilder.add(BookUtil.TextBuilder.of("\n" + ChatColor.RESET + reward.getName())
                        .build());
                lines++;
            }

            pages.add(pageBuilder.build());
        }
        return builder.pages(pages).build();
    }

    public static boolean isJournal(final QuesteAccount account, ItemStack item) {
        return item.isSimilar(getQuestJournal(account));
    }
}