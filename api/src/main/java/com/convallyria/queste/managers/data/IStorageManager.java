package com.convallyria.queste.managers.data;

import com.convallyria.queste.managers.data.account.QuesteAccount;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentMap;

public interface IStorageManager {

    /**
     * Gets a player's account from the storage.
     * This will return an account stored in the cache.
     * If no account is found in the cache a new account will be fetched and added to the cache.
     * @param uuid player's UUID
     * @return player's account
     */
    CompletableFuture<QuesteAccount> getAccount(UUID uuid);

    /**
     * Gets a map of currently cached accounts.
     * @return map of cached accounts
     */
    ConcurrentMap<UUID, QuesteAccount> getCachedAccounts();

    void deleteAccount(UUID uuid);

    /**
     * Removes an account from the storage cache and saves its data.
     * @param uuid player's UUID
     */
    void removeCachedAccount(UUID uuid);

    /**
     * Gets a UUID safe to use in databases.
     * @param uuid player's UUID
     * @return new string uuid to use in databases
     */
    default String getDatabaseUuid(UUID uuid) {
        return uuid.toString().replace("-", "");
    }
}
