package com.convallyria.queste.registry

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import com.convallyria.queste.Queste
import com.convallyria.queste.quest.objective.QuestObjectiveRegistry
import com.convallyria.queste.quest.requirement.QuestRequirementRegistry
import com.convallyria.queste.quest.reward.QuestRewardRegistry
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RegistryTest {

    private lateinit var server: ServerMock
    private lateinit var plugin: Queste

    @BeforeAll
    fun setUp() {
        server = MockBukkit.mock()
        plugin = MockBukkit.load(Queste::class.java)
    }

    @AfterAll
    fun tearDown() {
        MockBukkit.unmock()
    }

    @Test
    fun registryTest() {
        val objectiveRegistry = plugin.managers.getQuestRegistry(QuestObjectiveRegistry::class.java)
        Assertions.assertNotNull(objectiveRegistry)
        val rewardRegistry = plugin.managers.getQuestRegistry(QuestRewardRegistry::class.java)
        Assertions.assertNotNull(rewardRegistry)
        val requirementRegistry = plugin.managers.getQuestRegistry(QuestRequirementRegistry::class.java)
        Assertions.assertNotNull(requirementRegistry)
    }
}