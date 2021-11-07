package com.convallyria.queste.listener

import com.convallyria.queste.Queste
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.ServerLoadEvent

class ServerReloadListener(val plugin: Queste): Listener {

    @EventHandler
    fun onReload(event: ServerLoadEvent) {
        if (event.type == ServerLoadEvent.LoadType.RELOAD) {
            plugin.logger.warning("Queste does not support reloading. " +
                    "Reloading the plugin/server is unsupported and you should restart if experiencing issues.")
        }
    }
}