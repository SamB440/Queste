package com.convallyria.queste.registry

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import com.convallyria.queste.Queste
import com.convallyria.queste.quest.objective.QuestObjectiveRegistry
import com.convallyria.queste.quest.reward.QuestRewardRegistry
import com.convallyria.queste.quest.requirement.QuestRequirementRegistry
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test


class RegistryTest {

    private lateinit var server: ServerMock
    private lateinit var plugin: Queste

    @Before
    fun setUp() {
        server = MockBukkit.mock()
        plugin = MockBukkit.load(Queste::class.java)
    }

    @After
    fun tearDown() {
        MockBukkit.unmock()
    }

    @Test
    fun registryTest() {
        val objectiveRegistry = plugin.managers.getQuestRegistry(QuestObjectiveRegistry::class.java)
        Assert.assertNotNull(objectiveRegistry)
        val rewardRegistry = plugin.managers.getQuestRegistry(QuestRewardRegistry::class.java)
        Assert.assertNotNull(rewardRegistry)
        val requirementRegistry = plugin.managers.getQuestRegistry(QuestRequirementRegistry::class.java)
        Assert.assertNotNull(requirementRegistry)
    }
}