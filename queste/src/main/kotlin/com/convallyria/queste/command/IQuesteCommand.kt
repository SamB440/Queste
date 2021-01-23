package com.convallyria.queste.command

import org.bukkit.ChatColor

interface IQuesteCommand {

    fun translate(message: String): String {
        return ChatColor.translateAlternateColorCodes('&', message)
    }
}