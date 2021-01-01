package com.convallyria.queste.account

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import com.convallyria.queste.Queste
import com.convallyria.queste.managers.data.account.QuesteAccount
import com.convallyria.queste.quest.Quest
import com.convallyria.queste.quest.objective.PlaceBlockQuestObjective
import org.bukkit.entity.Player
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class AccountTest {

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
    fun saveTest() {
        plugin.managers.storageManager.getAccount(player.uniqueId).thenAccept { account: QuesteAccount ->
            account.addActiveQuest(quest)
            account.save(plugin)
        }
    }

    @Test
    fun loadTest() {
        System.out.println("load1")
        if (plugin.managers.storageManager.cachedAccounts.containsKey(player.uniqueId)) {
            System.out.println("load")
            plugin.managers.storageManager.removeCachedAccount(player.uniqueId)
        }
        plugin.managers.storageManager.getAccount(player.uniqueId).thenAccept { account: QuesteAccount ->
            plugin.logger.info(account.activeQuests.toString())
            Assert.assertTrue(!account.activeQuests.isEmpty())
        }
        plugin.managers.storageManager.getAccount(player.uniqueId).whenComplete { questeAccount: QuesteAccount, throwable: Throwable ->
            Assert.assertNull(throwable)
            plugin.logger.info(questeAccount.activeQuests.toString())
            Assert.assertTrue(!questeAccount.activeQuests.isEmpty())
        }
    }
}