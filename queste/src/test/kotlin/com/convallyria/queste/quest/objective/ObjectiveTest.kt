package com.convallyria.queste.quest.objective

import be.seeseemelk.mockbukkit.Coordinate
import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import be.seeseemelk.mockbukkit.WorldMock
import be.seeseemelk.mockbukkit.entity.PlayerMock
import com.convallyria.queste.Queste
import com.convallyria.queste.quest.Quest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ObjectiveTest {

    private lateinit var server: ServerMock
    private lateinit var world: WorldMock
    private lateinit var plugin: Queste
    private lateinit var player: PlayerMock
    private lateinit var quest: Quest

    @Before
    fun setUp() {
        server = MockBukkit.mock()
        world = server.addSimpleWorld("TestWorld")
        plugin = MockBukkit.load(Queste::class.java)
        player = server.addPlayer()
        quest = Quest("Test")
        val objective = BreakBlockQuestObjective(plugin, quest)
        objective.completionAmount = 1
        quest.addObjective(objective)
        plugin.managers.questeCache.addQuest(quest)
        val account = plugin.managers.storageManager.getAccount(player.uniqueId).get()
        account.addActiveQuest(quest)
    }

    @After
    fun tearDown() {
        MockBukkit.unmock()
    }

    @Test
    fun completeObjectiveTest() {
        val block = world.createBlock(Coordinate(0, 1, 0))
        println("Broken? " + player.simulateBlockBreak(block))
        println("Increment is: " + quest.objectives[0].getIncrement(player))
        Assert.assertTrue("Objective was not completed", quest.objectives[0].hasCompleted(player))
    }
}