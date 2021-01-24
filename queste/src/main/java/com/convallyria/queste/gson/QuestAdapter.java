package com.convallyria.queste.gson;

import com.convallyria.queste.quest.Quest;
import com.convallyria.queste.quest.objective.QuestObjective;
import com.convallyria.queste.quest.requirement.QuestRequirement;
import com.convallyria.queste.quest.reward.QuestReward;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.bukkit.Material;
import org.bukkit.Sound;

import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentHashMap;

public class QuestAdapter implements JsonSerializer<Quest>, JsonDeserializer<Quest> {

    @Override
    public JsonElement serialize(Quest quest, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("name", new JsonPrimitive(quest.getName()));
        String displayName = quest.getDisplayName() == null ? quest.getName() : quest.getDisplayName();
        result.add("displayName", new JsonPrimitive(displayName));
        result.add("canRestart", new JsonPrimitive(quest.canRestart()));
        AbstractAdapter<QuestObjective> objectiveAdapter = new AbstractAdapter<>(null);
        JsonArray objectives = new JsonArray();
        quest.getObjectives().forEach(questObjective -> {
            objectives.add(objectiveAdapter.serialize(questObjective, typeOfSrc, context));
        });
        result.add("objectives", objectives);

        AbstractAdapter<QuestReward> rewardAdapter = new AbstractAdapter<>(null);
        JsonArray rewards = new JsonArray();
        quest.getRewards().forEach(questReward -> {
            rewards.add(rewardAdapter.serialize(questReward, typeOfSrc, context));
        });
        result.add("rewards", rewards);

        AbstractAdapter<QuestRequirement> requirementAdapter = new AbstractAdapter<>(null);
        JsonArray requirements = new JsonArray();
        quest.getRequirements().forEach(questRequirement -> {
            requirements.add(requirementAdapter.serialize(questRequirement, typeOfSrc, context));
        });
        result.add("requirements", requirements);

        result.add("storyMode", new JsonPrimitive(quest.isStoryMode()));
        result.add("completeSound", new JsonPrimitive(quest.getCompleteSound().toString()));
        result.add("time", new JsonPrimitive(quest.getTime()));
        result.add("icon", new JsonPrimitive(quest.getIcon().toString()));
        result.add("description", new JsonPrimitive(quest.getDescription()));
        return result;
    }

    @Override
    public Quest deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        JsonObject jsonObject = json.getAsJsonObject();
        Quest quest = new Quest(jsonObject.get("name").getAsString());
        quest.setDisplayName(jsonObject.get("displayName").getAsString());

        boolean canRestart = jsonObject.get("canRestart").getAsBoolean();
        quest.setCanRestart(canRestart);

        AbstractAdapter<QuestObjective> objectiveAdapter = new AbstractAdapter<>(null);
        JsonArray objectives = jsonObject.getAsJsonArray("objectives");
        objectives.forEach(jsonElement -> {
            quest.addObjective(objectiveAdapter.deserialize(jsonElement, typeOfT, context));
        });

        AbstractAdapter<QuestReward> rewardAdapter = new AbstractAdapter<>(null);
        JsonArray rewards = jsonObject.getAsJsonArray("rewards");
        if (rewards != null) {
            rewards.forEach(jsonElement -> {
                quest.addReward(rewardAdapter.deserialize(jsonElement, typeOfT, context));
            });
        }

        AbstractAdapter<QuestRequirement> requirementAdapter = new AbstractAdapter<>(null);
        JsonArray requirements = jsonObject.getAsJsonArray("requirements");
        if (requirements != null) {
            requirements.forEach(jsonElement -> {
                quest.addRequirement(requirementAdapter.deserialize(jsonElement, typeOfT, context));
            });
        }

        if (jsonObject.get("storyMode") != null) {
            boolean storyMode = jsonObject.get("storyMode").getAsBoolean();
            quest.setStoryMode(storyMode);
        }

        if (jsonObject.get("completeSound") != null) {
            Sound sound = Sound.valueOf(jsonObject.get("completeSound").getAsString());
            quest.setCompleteSound(sound);
        }

        if (jsonObject.get("time") != null) {
            quest.setTime(jsonObject.get("time").getAsInt());
        }

        if (jsonObject.get("icon") != null) {
            quest.setIcon(Material.valueOf(jsonObject.get("icon").getAsString()));
        }

        if (jsonObject.get("description") != null) {
            quest.setDescription(jsonObject.get("description").getAsString());
        }

        quest.getObjectives().forEach(questObjective -> {
            questObjective.setProgress(new ConcurrentHashMap<>());
        });
        return quest;
    }

}
