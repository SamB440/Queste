package com.convallyria.queste.managers.data;

import com.convallyria.queste.quest.Quest;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface IQuesteCache {

    Map<String, Quest> getQuests();

    /**
     * Gets a quest by its unique name.
     * @param name unique name of quest
     * @return the {@link Quest}, or null if none found
     */
    @Nullable
    Quest getQuest(String name);

    /**
     * Adds a quest to the cache.
     * @param quest the quest
     */
    void addQuest(Quest quest);

    /**
     * Removes a quest from the cache.
     * @param quest the quest
     */
    void removeQuest(Quest quest);

    /**
     * Reloads cached quests from file.
     */
    void reload();
}
