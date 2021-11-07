package com.convallyria.queste.translation

import org.bukkit.configuration.file.YamlConfiguration
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.reflections.Reflections
import org.reflections.scanners.ResourcesScanner
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Locale
import java.util.function.Consumer
import java.util.regex.Pattern

class TranslationsTest {

    @Test
    fun translationTest() {
        val reflections = Reflections("lang/", ResourcesScanner())
        val fileNames = reflections.getResources(Pattern.compile(".*\\.yml"))
        fileNames.forEach(Consumer { fileName: String ->
            val input = javaClass.classLoader.getResourceAsStream(fileName) ?: return@Consumer
            val reader = BufferedReader(InputStreamReader(input))
            val config = YamlConfiguration.loadConfiguration(reader)
            for (translation in Translations.values()) {
                Assertions.assertFalse(config[translation.toString().lowercase(Locale.ROOT)] == null,
                    "$translation($translation) not found in $fileName.")
            }
        })
    }
}