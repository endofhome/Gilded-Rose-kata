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
        private val allItems = listOf(
            Item("+5 Dexterity Vest", 10, 20),
            Item("Aged Brie", 2, 0),
            Item("Elixir of the Mongoose", 5, 7),
            Item("Sulfuras, Hand of Ragnaros", 0, 80),
            Item("Sulfuras, Hand of Ragnaros", -1, 80),
            Item("Backstage passes to a TAFKAL80ETC concert", 15, 20),
            Item("Backstage passes to a TAFKAL80ETC concert", 10, 49),
            Item("Backstage passes to a TAFKAL80ETC concert", 5, 49),
            Item("Conjured Mana Cake", 3, 6)
        )

        @JvmStatic
        fun allItemsAsArguments(): Stream<Arguments?>? =
            Stream.of(
                *allItems.map { Arguments.of(it) }.toTypedArray()
            )
    }

    @ParameterizedTest
    @MethodSource("allItemsAsArguments")
    fun `number of days to sell item reduces appropriately when updating quality`(item: Item) {
        val items = arrayOf(item)
        val app = GildedRose(items)
        // TODO The system behaviour would be more visible if each branch is extracted to its own test.
        val expectedSellIn =
            when {
                item.isLegendary() -> item.sellIn
                else               -> item.sellIn - 1
            }

        app.updateQuality()

        val actualItem = app.items().single()

        assertEquals(expectedSellIn, actualItem.sellIn)
    }

    @ParameterizedTest
    @MethodSource("allItemsAsArguments")
    fun `item quality is adjusted appropriately when updating quality`(item: Item) {
        val items = arrayOf(item)
        val app = GildedRose(items)
        // TODO The system behaviour would be more visible if each branch is extracted to its own test.
        val expectedQuality = when {
            item.name.startsWith("Conjured")        -> item.quality - 2
            item.isLegendary()                             -> item.quality
            item.sellIn < 0                                -> item.quality - 2
            item.increasesInValueOverTime()                -> item.quality + 1
            else                                           -> max(item.quality - 1, 0)
        }

        app.updateQuality()

        val actualItem = app.items().single()

        assertEquals(expectedQuality, actualItem.quality)
    }

    @Test
    fun `the quality of an item never increases above 50`() {
        val itemsThatIncreaseInValueOverTime = allItems
            .filter { item -> item.increasesInValueOverTime() }
            .map { Item(it.name, it.sellIn, 50) }
            .toTypedArray()

        val app = GildedRose(itemsThatIncreaseInValueOverTime)

        app.updateQuality()

        app.items().forEach { item ->
            assertEquals(50, item.quality)
        }
    }

    private fun Item.increasesInValueOverTime() =
        itemTypeFor(name).qualityUpdateStrategy is IncreasingOverTimeQualityUpdateStrategy

    @Test
    fun `legendary items retain their quality no matter the value`() {
        val legendaryItems = allItems
            .filter { item -> item.isLegendary() }
            .map { Item(it.name, it.sellIn, 51) }
            .toTypedArray()

        val app = GildedRose(legendaryItems)

        app.updateQuality()

        app.items().forEach { item ->
            assertEquals(51, item.quality)
        }
    }

    private fun Item.isLegendary() =
        itemTypeFor(name) is LegendaryItemType

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

    private fun backstagePass(sellIn: Int = 20, quality: Int): Item = Item("Backstage passes to a TAFKAL80ETC concert", sellIn, quality)
}


