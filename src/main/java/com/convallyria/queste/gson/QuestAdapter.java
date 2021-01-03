package com.convallyria.queste.gson;

import com.convallyria.queste.quest.Quest;
import com.convallyria.queste.quest.objective.QuestObjective;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class QuestAdapter implements JsonSerializer<Quest>, JsonDeserializer<Quest> {

    @Override
    public JsonElement serialize(Quest quest, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("name", new JsonPrimitive(quest.getName()));
        result.add("canRestart", new JsonPrimitive(quest.canRestart()));
        AbstractAdapter<QuestObjective> objectiveAdapter = new AbstractAdapter<>(null);
        JsonArray objectives = new JsonArray();
        quest.getObjectives().forEach(questObjective -> {
            objectives.add(objectiveAdapter.serialize(questObjective, typeOfSrc, context));
        });
        return result;
    }

    @Override
    public Quest deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        Quest quest = new Quest(jsonObject.get("name").getAsString());

        boolean canRestart = jsonObject.get("canRestart").getAsBoolean();
        quest.setCanRestart(canRestart);

        AbstractAdapter<QuestObjective> objectiveAdapter = new AbstractAdapter<>(null);
        JsonArray objectives = jsonObject.getAsJsonArray("objectives");
        objectives.forEach(jsonElement -> {
            quest.addObjective(objectiveAdapter.deserialize(json, typeOfT, context));
        });
        return quest;
    }

}
