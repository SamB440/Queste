package com.convallyria.queste.quest

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import com.convallyria.queste.Queste
import com.convallyria.queste.quest.objective.PlaceBlockQuestObjective
import com.convallyria.queste.quest.objective.QuestObjective
import org.bukkit.entity.Player
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader
import java.io.Reader
import java.lang.reflect.Modifier

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class QuestTest {

    private lateinit var server: ServerMock
    private lateinit var plugin: Queste
    private lateinit var player: Player
    private lateinit var quest: Quest

    @BeforeAll
    fun setUp() {
        server = MockBukkit.mock()
        plugin = MockBukkit.load(Queste::class.java)
        player = server.addPlayer()
        quest = Quest("Test")
        quest.addObjective(PlaceBlockQuestObjective(plugin, quest))
        plugin.managers.questeCache.addQuest(quest)
    }

    @AfterAll
    fun tearDown() {
        MockBukkit.unmock()
    }

    @Test
    fun incrementTest() {
        val account = plugin.managers.storageManager.getAccount(player.uniqueId).get()
        account.addActiveQuest(quest)
        quest.objectives.forEach { questObjective: QuestObjective ->
            questObjective.increment(player)
            Assertions.assertEquals(1, questObjective.getIncrement(player))
        }
    }

    @Test
    fun completeTest() {
        quest.objectives.forEach { questObjective: QuestObjective ->
            questObjective.setIncrement(player, questObjective.completionAmount)
            Assertions.assertTrue(questObjective.hasCompleted(player))
        }
    }

    @Test
    fun saveTest() {
        Assertions.assertTrue(quest.save(plugin))
    }

    @Test
    fun loadTest() {
        saveTest() // Save so we can load

        val folder = File(plugin.dataFolder.toString() + "/quests/")
        if (!folder.exists()) folder.mkdirs()
        for (file in folder.listFiles()) {
            try {
                val reader: Reader = FileReader(file)
                val quest = plugin.gson.fromJson(reader, Quest::class.java)
                Assertions.assertNotNull(quest)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
    }

    @Test
    fun serialiseTest() {
        saveTest() // Save so we can check

        // Load it
        var loadedQuest: Quest? = null
        val folder = File(plugin.dataFolder.toString() + "/quests/")
        if (!folder.exists()) folder.mkdirs()
        for (file in folder.listFiles()) {
            try {
                val reader: Reader = FileReader(file)
                loadedQuest = plugin.gson.fromJson(reader, Quest::class.java)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }

        if (loadedQuest != null) {
            for (declaredField in quest.javaClass.declaredFields) {
                declaredField.isAccessible = true
                if (!Modifier.isTransient(declaredField.modifiers)) {
                    for (loadedQuestField in loadedQuest.javaClass.declaredFields) {
                        loadedQuestField.isAccessible = true
                        if (loadedQuestField.name == declaredField.name) {
                            Assertions.assertFalse(loadedQuestField.get(loadedQuest) == null, loadedQuestField.name + " was null at loading after save")
                        }
                    }
                }
            }
        } else {
            Assertions.fail("loadedQuest is null")
        }
    }
}