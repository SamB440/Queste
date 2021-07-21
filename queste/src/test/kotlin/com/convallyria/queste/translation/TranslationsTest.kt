package com.convallyria.queste.translation

import org.bukkit.configuration.file.YamlConfiguration
import org.junit.Assert
import org.junit.Test
import org.reflections.Reflections
import org.reflections.scanners.ResourcesScanner
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*
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
                if (config[translation.toString().lowercase(Locale.ROOT)] == null) {
                    Assert.fail("$translation($translation) not found in $fileName.")
                }
            }
        })
    }
}