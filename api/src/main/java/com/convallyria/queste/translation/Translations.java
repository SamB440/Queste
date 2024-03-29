package com.convallyria.queste.translation;

import com.convallyria.queste.api.IQuesteAPI;
import com.convallyria.queste.api.QuesteAPI;
import me.clip.placeholderapi.PlaceholderAPI;
import net.islandearth.languagy.api.language.Language;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public enum Translations {
    NEXT_PAGE("&aNext Page"),
    NEXT_PAGE_LORE("&fGo to the next page", true),
    PREVIOUS_PAGE("&cPrevious Page"),
    PREVIOUS_PAGE_LORE("&fGo to the previous page", true),
    EXIT("&cExit"),
    EXIT_LORE("&fExit the GUI", true),
    OBJECTIVE_COMPLETE(" &e&lObjective Complete! &8(%0/%1)", true),
    QUEST_COMPLETED("&e&lQuest Completed!", true),
    QUEST_COMPLETED_TITLE("&aQuest Completed"),
    QUEST_FAILED_TITLE("&cQuest Failed"),
    QUEST_STARTED("&aQuest Started"),
    OBJECTIVE_PROGRESS("&6%0 %1 &7(%2/%3)"),
    JOURNAL_TITLE("&d&lQuest Journal");

    private final String defaultValue;
    private final boolean isList;

    Translations(String defaultValue) {
        this.defaultValue = defaultValue;
        this.isList = false;
    }

    Translations(String defaultValue, boolean isList) {
        this.defaultValue = defaultValue;
        this.isList = isList;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public boolean isList() {
        return isList;
    }

    private String getPath() {
        return this.toString().toLowerCase(Locale.ROOT);
    }

    public void send(Player player) {
        String message = QuesteAPI.getAPI().getTranslator().getTranslationFor(player, this.getPath());
        player.sendMessage(message);
    }

    public void send(Player player, Object... values) {
        String message = QuesteAPI.getAPI().getTranslator().getTranslationFor(player, this.getPath());
        message = this.setPapi(player, replaceVariables(message, values));
        player.sendMessage(message);
    }

    public void sendList(Player player) {
        List<String> messages = QuesteAPI.getAPI().getTranslator().getTranslationListFor(player, this.getPath());
        messages.forEach(player::sendMessage);
    }

    public void sendList(Player player, Object... values) {
        List<String> messages = QuesteAPI.getAPI().getTranslator().getTranslationListFor(player, this.getPath());
        messages.forEach(message -> {
            message = this.setPapi(player, replaceVariables(message, values));
            player.sendMessage(message);
        });
    }

    public String get(Player player) {
        return this.setPapi(player, QuesteAPI.getAPI().getTranslator().getTranslationFor(player, this.getPath()));
    }

    public String get(Player player, Object... values) {
        String message = QuesteAPI.getAPI().getTranslator().getTranslationFor(player, this.getPath());
        message = replaceVariables(message, values);
        return this.setPapi(player, message);
    }

    public List<String> getList(Player player) {
        List<String> list = new ArrayList<>();
        QuesteAPI.getAPI().getTranslator().getTranslationListFor(player, this.getPath()).forEach(text -> list.add(this.setPapi(player, text)));
        return list;
    }

    public List<String> getList(Player player, Object... values) {
        List<String> messages = new ArrayList<>();
        QuesteAPI.getAPI().getTranslator()
                .getTranslationListFor(player, this.getPath())
                .forEach(message -> messages.add(this.setPapi(player, replaceVariables(message, values))));
        return messages;
    }

    public static void generateLang(IQuesteAPI plugin) {
        File lang = new File(plugin.getDataFolder() + "/lang/");
        lang.mkdirs();

        for (Language language : Language.values()) {
            try {
                plugin.saveResource("lang/" + language.getCode() + ".yml", false);
                plugin.getLogger().info("Generated " + language.getCode() + ".yml");
            } catch (IllegalArgumentException ignored) { }

            File file = new File(plugin.getDataFolder() + "/lang/" + language.getCode() + ".yml");
            if (file.exists()) {
                FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                for (Translations key : values()) {
                    if (config.get(key.toString().toLowerCase(Locale.ROOT)) == null) {
                        plugin.getLogger().warning("No value in translation file for key "
                                + key + " was found. Regenerate language files?");
                    }
                }
            }
        }
    }

    @NotNull
    private String replaceVariables(String message, Object... values) {
        String modifiedMessage = message;
        for (int i = 0; i < 10; i++) {
            if (values.length > i) modifiedMessage = modifiedMessage.replaceAll("%" + i, String.valueOf(values[i]));
            else break;
        }

        return modifiedMessage;
    }

    @NotNull
    private String setPapi(Player player, String message) {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            return PlaceholderAPI.setPlaceholders(player, message);
        }

        return message;
    }
}
