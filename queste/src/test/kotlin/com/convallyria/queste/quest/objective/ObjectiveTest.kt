package com.convallyria.queste.quest.objective

import be.seeseemelk.mockbukkit.Coordinate
import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import be.seeseemelk.mockbukkit.WorldMock
import be.seeseemelk.mockbukkit.entity.PlayerMock
import com.convallyria.queste.Queste
import com.convallyria.queste.quest.Quest
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ObjectiveTest {

    private lateinit var server: ServerMock
    private lateinit var world: WorldMock
    private lateinit var plugin: Queste
    private lateinit var player: PlayerMock
    private lateinit var quest: Quest

    @BeforeAll
    fun setUp() {
        server = MockBukkit.mock()
        world = server.addSimpleWorld("TestWorld")
        plugin = MockBukkit.load(Queste::class.java)
        player = server.addPlayer()
        quest = Quest("Test")

        // Create objective and set values
        val objective = BreakBlockQuestObjective(plugin, quest)
        objective.completionAmount = 1

        // Add to quest and add quest to cache
        quest.addObjective(objective)
        plugin.managers.questeCache.addQuest(quest)

        // Load account
        val account = plugin.managers.storageManager.getAccount(player.uniqueId).get()
        account.addActiveQuest(quest)
    }

    @AfterAll
    fun tearDown() {
        MockBukkit.unmock()
    }

    @Test
    fun completeObjectiveTest() {
        val block = world.createBlock(Coordinate(0, 1, 0))
        println("Broken? " + player.simulateBlockBreak(block))
        println("Increment is: " + quest.objectives[0].getIncrement(player))
        Assertions.assertTrue(quest.objectives[0].hasCompleted(player), "Objective was not completed")
    }
}