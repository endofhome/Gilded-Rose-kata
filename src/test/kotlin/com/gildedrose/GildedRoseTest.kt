package com.gildedrose

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.lang.Integer.max
import java.util.stream.Stream


internal class GildedRoseTest {

    companion object {
        @JvmStatic
        fun allItems(): Stream<Arguments?>? =
            Stream.of(
                Arguments.of(Item("+5 Dexterity Vest", 10, 20)),
                Arguments.of(Item("Aged Brie", 2, 0)),
                Arguments.of(Item("Elixir of the Mongoose", 5, 7)),
                Arguments.of(Item("Sulfuras, Hand of Ragnaros", 0, 80)),
                Arguments.of(Item("Sulfuras, Hand of Ragnaros", -1, 80)),
                Arguments.of(Item("Backstage passes to a TAFKAL80ETC concert", 15, 20)),
                Arguments.of(Item("Backstage passes to a TAFKAL80ETC concert", 10, 49)),
                Arguments.of(Item("Backstage passes to a TAFKAL80ETC concert", 5, 49)),
                Arguments.of(Item("Conjured Mana Cake", 3, 6)),
            )
    }

    @ParameterizedTest
    @MethodSource("allItems")
    fun `number of days to sell item reduces appropriately when updating quality`(item: Item) {
        val items = arrayOf(item)
        val app = GildedRose(items)
        // TODO The system behaviour would be more visible if each branch is extracted to its own test.
        val expectedSellIn = when (item.name) {
            in namesOfLegendaryItems -> item.sellIn
            else                     -> item.sellIn - 1
        }

        app.updateQuality()

        val actualItem = app.items().single()

        assertEquals(expectedSellIn, actualItem.sellIn)
    }

    @ParameterizedTest
    @MethodSource("allItems")
    fun `item quality reduces appropriately when updating quality`(item: Item) {
        val items = arrayOf(item)
        val app = GildedRose(items)
        // TODO The system behaviour would be more visible if each branch is extracted to its own test.
        val expectedQuality = when {
            item.name.startsWith("Conjured")               -> item.quality - 2
            item.name in namesOfLegendaryItems             -> item.quality
            item.name in namesOfItemsThatGainValueOverTime -> item.quality + 1
            item.sellIn < 0                                -> item.quality - 2
            else                                           -> max(item.quality - 1, 0)
        }

        app.updateQuality()

        val actualItem = app.items().single()

        assertEquals(expectedQuality, actualItem.quality)
    }

    @Test
    fun `the quality of an item never increases above 50`() {
        // TODO try to get rid of this by using the type system - `ItemType` now tells us what kind of strategies are used for each type of item.
        val itemsThatGainValueOverTime = arrayOf(agedBrie(quality = 50), backstagePass(quality = 50))
        val app = GildedRose(itemsThatGainValueOverTime)

        app.updateQuality()

        assertEquals(itemsThatGainValueOverTime.map { it.name }.toSet(), namesOfItemsThatGainValueOverTime)
        app.items().forEach { item ->
            assertEquals(50, item.quality)
        }
    }

    @Test
    fun `legendary items retain their quality no matter the value`() {
        // TODO try to get rid of this by using the type system - `ItemType` now tells us what kind of strategies are used for each type of item.
        val legendaryItems = arrayOf(sulfuras(quality = 51))
        val app = GildedRose(legendaryItems)

        app.updateQuality()

        assertEquals(legendaryItems.map { it.name }.toSet(), namesOfLegendaryItems)
        app.items().forEach { item ->
            assertEquals(51, item.quality)
        }
    }

    @Test
    fun `backstage passes increase in quality by 1 when there are 11 days before the concert`() {
        // TODO try to get rid of this by using the type system - `ItemType` now tells us what kind of strategies are used for each type of item.
        val backstagePass = arrayOf(backstagePass(quality = 10))
        val app = GildedRose(backstagePass)

        app.updateQuality()

        val actualItem = app.items().single()
        assertEquals(11, actualItem.quality)
    }

    @Test
    fun `backstage passes increase in quality by 2 when there are 10 days or less before the concert`() {
        // TODO try to get rid of this by using the type system - `ItemType` now tells us what kind of strategies are used for each type of item.
        (6..10).forEach { sellIn ->
            val backstagePass = arrayOf(backstagePass(sellIn = sellIn, quality = 10))
            val app = GildedRose(backstagePass)

            app.updateQuality()

            val actualItem = app.items().single()
            assertEquals(12, actualItem.quality)
        }
    }

    @Test
    fun `backstage passes increase in quality by 3 when there are 5 days or less before the concert`() {
        // TODO try to get rid of this by using the type system - `ItemType` now tells us what kind of strategies are used for each type of item.
        (1..5).forEach { sellIn ->
            val backstagePass = arrayOf(backstagePass(sellIn = sellIn, quality = 10))
            val app = GildedRose(backstagePass)

            app.updateQuality()

            val actualItem = app.items().single()
            assertEquals(13, actualItem.quality)
        }
    }

    @Test
    fun `backstage passes are worthless after the concert`() {
        // TODO try to get rid of this by using the type system - `ItemType` now tells us what kind of strategies are used for each type of item.
        val backstagePass = arrayOf(backstagePass(sellIn = 0, quality = 10))
        val app = GildedRose(backstagePass)

        app.updateQuality()

        val actualItem = app.items().single()
        assertEquals(0, actualItem.quality)
    }

    // TODO try to get rid of this by using the type system - `ItemType` now tells us what kind of strategies are used for each type of item.
    private val namesOfLegendaryItems = setOf("Sulfuras, Hand of Ragnaros")
    private val namesOfItemsThatGainValueOverTime = setOf("Aged Brie", "Backstage passes to a TAFKAL80ETC concert")

    private fun agedBrie(quality: Int): Item = Item("Aged Brie", 20, quality)
    private fun backstagePass(sellIn: Int = 20, quality: Int): Item = Item("Backstage passes to a TAFKAL80ETC concert", sellIn, quality)
    private fun sulfuras(quality: Int): Item = Item("Sulfuras, Hand of Ragnaros", 10, quality)
}


