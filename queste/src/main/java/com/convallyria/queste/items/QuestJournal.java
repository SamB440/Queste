package com.convallyria.queste.items;

import com.convallyria.queste.managers.data.account.QuesteAccount;
import com.convallyria.queste.quest.Quest;
import com.convallyria.queste.quest.objective.QuestObjective;
import com.convallyria.queste.translation.Translations;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import xyz.upperlevel.spigot.book.BookUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class QuestJournal {

    public static ItemStack getQuestJournal(final QuesteAccount account) {
        Player player = Bukkit.getPlayer(account.getUuid());
        BookUtil.BookBuilder builder = new BookUtil.BookBuilder();
        builder.author(player.getName()).title(Translations.JOURNAL_TITLE.get(player));
        List<BaseComponent[]> pages = new ArrayList<>();
        if (account.getActiveQuests().isEmpty()) {
            pages.add(new TextComponent[]{new TextComponent(ChatColor.DARK_RED + "You have no accepted quests!")});
        } else {
            int currentLength = 0;
            int currentLines = 0;
            ComponentBuilder componentBuilder = new ComponentBuilder();
            final List<Quest> sortedList = account.getActiveQuests().stream()
                    .sorted(Comparator.comparing(Quest::getName))
                    .collect(Collectors.toList());
            for (final Quest quest : sortedList) {
                if ((currentLength + quest.getDisplayName().length() > 240) || (currentLines
                        + ((quest.getDisplayName().length() % 19) == 0 ? (quest.getDisplayName().length() / 19)
                        : ((quest.getDisplayName().length() / 19) + 1))) > 13) {
                    pages.add(componentBuilder.create());
                    componentBuilder.append()
                    page += ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + quest.getDisplayName() + "\n";
                    currentLength = quest.getDisplayName().length();
                    currentLines = (quest.getDisplayName().length() % 19) == 0 ? (quest.getDisplayName().length() / 19)
                            : (quest.getDisplayName().length() + 1);
                } else {
                    page += ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + quest.getDisplayName() + "\n";
                    currentLength += quest.getDisplayName().length();
                    currentLines += (quest.getDisplayName().length() / 19);
                }
                for (Quest activeQuest : account.getActiveQuests()) {
                    for (final QuestObjective objective : activeQuest.getObjectives()) {
                        String obj = objective.getDisplayName();
                        // Length/Line check
                        if ((currentLength + obj.length() > 240) || (currentLines + ((obj.length() % 19)
                                == 0 ? (obj.length() / 19) : ((obj.length() / 19) + 1))) > 13) {
                            book.addPage(page);
                            page = obj + "\n";
                            currentLength = obj.length();
                            currentLines = (obj.length() % 19) == 0 ? (obj.length() / 19) : (obj.length() + 1);
                        } else {
                            page += obj + "\n";
                            currentLength += obj.length();
                            currentLines += (obj.length() / 19);
                        }
                    }
                }

                if (currentLines < 13)
                    page += "\n";
                book.addPage(page);
                page = "";
                currentLines = 0;
                currentLength = 0;
            }
        }
        return builder.pages(pages).build();
    }
}