package hu.trigary.advancementcreator

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import com.convallyria.queste.Queste
import hu.trigary.advancementcreator.shared.*
import hu.trigary.advancementcreator.trigger.InventoryChangedTrigger
import hu.trigary.advancementcreator.trigger.LocationTrigger
import hu.trigary.advancementcreator.trigger.PlacedBlockTrigger
import hu.trigary.advancementcreator.trigger.PlayerHurtEntityTrigger
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.StructureType
import org.bukkit.block.Biome
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.EntityType
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

/**
 * Automatically converted to Kotlin.
 */
class AdvancementTest {

    private lateinit var server: ServerMock
    private lateinit var plugin: Queste
    private var factory: AdvancementFactory? = null

    @Before
    fun setUp() {
        server = MockBukkit.mock()
        plugin = MockBukkit.load(Queste::class.java)
        factory = AdvancementFactory(plugin, false, false)
    }

    @After
    fun tearDown() {
        MockBukkit.unmock()
    }

    @Test
    fun testToJson() {
        createLongAdvancement().toJson()
    }

    @Test
    fun testEquals() {
        Assert.assertEquals(createLongAdvancement(), createLongAdvancement())
    }

    @Test
    fun testFactory() {
        val root = factory!!.getRoot("test/root", "Root", "Test Advancements", Material.STONE, "blocks/gravel")
        val manual = Advancement(
            NamespacedKey(
                plugin, "test/id"
            ), ItemObject().setItem(Material.BEACON),
            TextComponent("Bacon Lover"), TextComponent("Have 3 beacons in your inventory at once")
        )
        .addTrigger(
            "item", InventoryChangedTrigger().addItem(
                ItemObject()
                    .setItem(Material.BEACON).setCount(RangeObject().setMin(3))
            )
        )
        .makeChild(root.id)
        .setFrame(Advancement.Frame.GOAL)
        val automated = factory!!.getItem(
            "test/id", root, "Bacon Lover", "Have 3 beacons in your inventory at once",
            Material.BEACON, 3
        )
            .setFrame(Advancement.Frame.GOAL)
        Assert.assertEquals(manual, automated)
    }

    private fun createLongAdvancement(): Advancement {
        return Advancement(
            NamespacedKey(plugin, "test/id"), ItemObject().setItem(Potion.Type.NORMAL.item),
            TextComponent("Displayed title"), TextComponent("Displayed description")
        )
        .makeRoot("blocks/gravel", false)
        .setFrame(Advancement.Frame.CHALLENGE)
        .setRewards(
            Rewards()
                .addRecipe(NamespacedKey(plugin, "test/recipe"))
                .addLoot(NamespacedKey(plugin, "test/loot"))
                .setExperience(1)
                .setFunction(NamespacedKey(plugin, "test/function"))
        )
        .addRequirement("1", "2")
        .addRequirement("1", "3")
        .addRequirement("2", "3")
        .addTrigger(
            "1", LocationTrigger().setLocation(
                LocationObject()
                    .setX(RangeObject().setMin(1))
                    .setY(RangeObject().setMax(1))
                    .setBiome(Biome.DESERT)
                    .setFeature(StructureType.JUNGLE_PYRAMID)
                    .setDimension(Dimension.OVERWORLD)
            )
        )
        .addTrigger(
            "2", PlayerHurtEntityTrigger().setEntity(
                EntityObject()
                    .setType(EntityType.ZOMBIE)
                    .setDistance(DistanceObject().setAbsolute(RangeObject().setExact(1)))
                    .setEffects(StatusEffectsObject().setEffect(Effect.STRENGTH, EffectObject().setVisible(true)))
                    .setNbt("{NoAI:0}")
            )
            .setDamage(
                DamageObject().setType(
                    DamageFlagsObject()
                        .setBypassesInvulnerability(true)
                        .setExplosion(true)
                )
            )
        )
        .addTrigger(
            "3", PlacedBlockTrigger().setItem(
                ItemObject()
                    .setItem(Material.GRASS)
                    .setPotion(Potion.LONG_INVISIBILITY)
                    .addEnchant(EnchantObject().setEnchant(Enchantment.ARROW_FIRE))
            ).setBlock(BlockObject(Material.GRASS).setState("snowy", "true"))
        )
    }
}