package com.convallyria.queste.managers.data;

import com.convallyria.queste.quest.Quest;

import java.util.Map;

public interface IQuesteCache {

    Map<String, Quest> getQuests();

    Quest getQuest(String name);

    void addQuest(Quest quest);

    void removeQuest(Quest quest);

    void reload();
}
