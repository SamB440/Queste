package com.convallyria.queste.quest

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import com.convallyria.queste.Queste
import com.convallyria.queste.quest.objective.PlaceBlockQuestObjective
import com.convallyria.queste.quest.objective.QuestObjective
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader
import java.io.Reader
import java.lang.reflect.Modifier
import java.util.function.Consumer


class QuestTest {

    private lateinit var server: ServerMock
    private lateinit var plugin: Queste
    private lateinit var player: Player
    private lateinit var quest: Quest

    @Before
    fun setUp() {
        server = MockBukkit.mock()
        plugin = MockBukkit.load(Queste::class.java)
        player = server.addPlayer()
        quest = Quest("Test")
        quest.addObjective(PlaceBlockQuestObjective(plugin, quest))
        plugin.managers.questeCache.addQuest(quest)
    }

    @After
    fun tearDown() {
        MockBukkit.unmock()
    }

    @Test
    fun incrementTest() {
        val account = plugin.managers.storageManager.getAccount(player.uniqueId).get()
        account.addActiveQuest(quest)
        quest.objectives.forEach { questObjective: QuestObjective ->
            questObjective.increment(player)
            Assert.assertEquals(1, questObjective.getIncrement(player))
        }
    }

    @Test
    fun completeTest() {
        quest.objectives.forEach { questObjective: QuestObjective ->
            questObjective.setIncrement(player, questObjective.completionAmount)
            Assert.assertTrue(questObjective.hasCompleted(player))
        }
    }

    @Test
    fun saveTest() {
        Assert.assertTrue(quest.save(plugin))
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
                Assert.assertNotNull(quest)
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
                            if (loadedQuestField.get(loadedQuest) == null) {
                                Assert.fail(loadedQuestField.name + " was null at loading after save")
                            }
                        }
                    }
                }
            }
        } else {
            Assert.fail("loadedQuest is null")
        }
    }
}