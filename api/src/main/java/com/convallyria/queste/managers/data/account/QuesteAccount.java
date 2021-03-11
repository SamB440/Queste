package com.convallyria.queste.managers.data.account;

import com.convallyria.queste.quest.Quest;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QuesteAccount {

    private final UUID uuid;
    private final List<Quest> activeQuests;
    private final List<Quest> completedQuests;
    private final ConcurrentMap<Quest, Long> startTimes;

    public QuesteAccount(UUID uuid) {
        this.uuid = uuid;
        this.activeQuests = new ArrayList<>();
        this.completedQuests = new ArrayList<>();
        this.startTimes = new ConcurrentHashMap<>();
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

    public ImmutableMap<Quest, Long> getStartTimes() {
        return ImmutableMap.copyOf(startTimes);
    }

    public long getStartTime(Quest quest) {
        return startTimes.containsKey(quest) ? startTimes.get(quest) : -1;
    }

    public void addActiveQuest(Quest quest) {
        activeQuests.add(quest);
        startTimes.put(quest, System.currentTimeMillis());
    }

    public void addActiveQuest(Quest quest, long currentTime) {
        activeQuests.add(quest);
        startTimes.put(quest, currentTime);
    }

    public void removeActiveQuest(Quest quest) {
        activeQuests.remove(quest);
        startTimes.remove(quest);
    }

    public void removeCompletedQuest(Quest quest) {
        completedQuests.remove(quest);
    }

    public void addCompletedQuest(Quest quest) {
        completedQuests.add(quest);
    }

}