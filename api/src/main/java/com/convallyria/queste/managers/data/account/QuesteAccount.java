package com.convallyria.queste.managers.data.account;

import com.convallyria.queste.quest.Quest;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QuesteAccount {

    private final UUID uuid;
    private final List<Quest> activeQuests;
    private final List<Quest> completedQuests;

    public QuesteAccount(UUID uuid) {
        this.uuid = uuid;
        this.activeQuests = new ArrayList<>();
        this.completedQuests = new ArrayList<>();
    }

    public ImmutableList<Quest> getActiveQuests() {
        return ImmutableList.copyOf(activeQuests);
    }

    public ImmutableList<Quest> getCompletedQuests() {
        return ImmutableList.copyOf(completedQuests);
    }

    public ImmutableList<Quest> getAllQuests() {
        List<Quest> questList = Stream.concat(activeQuests.stream(), completedQuests.stream())
                .collect(Collectors.toList());
        return ImmutableList.copyOf(questList);
    }

    public void addActiveQuest(Quest quest) {
        activeQuests.add(quest);
    }

    public void removeActiveQuest(Quest quest) {
        activeQuests.remove(quest);
    }

    public void removeCompletedQuest(Quest quest) {
        completedQuests.remove(quest);
    }

    public void addCompletedQuest(Quest quest) {
        completedQuests.add(quest);
    }

}