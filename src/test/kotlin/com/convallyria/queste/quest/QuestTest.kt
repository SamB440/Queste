package com.convallyria.queste.quest

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import com.convallyria.queste.Queste
import com.convallyria.queste.quest.objective.PlaceBlockQuestObjective
import com.convallyria.queste.quest.objective.QuestObjective
import org.bukkit.entity.Player
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test


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
    }

    @After
    fun tearDown() {
        MockBukkit.unmock()
    }

    @Test
    fun incrementTest() {
        quest.objectives.forEach { questObjective: QuestObjective ->
            questObjective.increment(player)
            Assert.assertEquals(1, questObjective.getIncrement(player))
        }
    }

    @Test
    fun saveTest() {
        Assert.assertTrue(quest.save(plugin))
    }
}