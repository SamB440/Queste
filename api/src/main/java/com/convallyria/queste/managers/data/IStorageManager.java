package com.convallyria.queste.managers.data;

import com.convallyria.queste.managers.data.account.QuesteAccount;

import java.math.BigInteger;
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

    /**
     * Deletes an account in its entirety, and removes it from the cached accounts.
     * @param uuid uuid of player
     */
    void deleteAccount(UUID uuid);

    /**
     * Removes an account from the storage cache and saves its data.
     * @param uuid player's UUID
     */
    CompletableFuture<Void> removeCachedAccount(UUID uuid);

    /**
     * Gets a UUID safe to use in databases.
     * @param uuid player's UUID
     * @return new string uuid to use in databases
     */
    default String getDatabaseUuid(UUID uuid) {
        return uuid.toString().replace("-", "");
    }

    default UUID fromDatabaseUUID(String uuidString) {
        return new UUID(new BigInteger(uuidString.substring(0, 16), 16).longValue(),
                new BigInteger(uuidString.substring(16), 16).longValue());
    }
}
