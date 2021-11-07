package com.convallyria.queste.account

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import com.convallyria.queste.Queste
import com.convallyria.queste.quest.Quest
import com.convallyria.queste.quest.objective.PlaceBlockQuestObjective
import org.bukkit.entity.Player
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AccountTest {

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
    fun saveTest() {
        val account = plugin.managers.storageManager.getAccount(player.uniqueId).get()
        account.addActiveQuest(quest)
        plugin.managers.storageManager.removeCachedAccount(player.uniqueId)
        Assertions.assertFalse(plugin.managers.storageManager.cachedAccounts.containsKey(player.uniqueId))
    }

    @Test
    fun loadTest() {
        saveTest() // Save so we can load

        val account = plugin.managers.storageManager.getAccount(player.uniqueId).get()
        Assertions.assertFalse(account.activeQuests.isEmpty())
    }
}