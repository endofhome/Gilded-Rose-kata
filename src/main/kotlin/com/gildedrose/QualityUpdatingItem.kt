package com.gildedrose

import java.lang.Integer.max
import java.lang.Integer.min

class QualityUpdatingItem(private val item: Item) {
    val underlyingItem = Item(item.name, item.sellIn, item.quality)
    // TODO this still conflates updating `quality` and `sellIn`
    fun updateQuality(): QualityUpdatingItem = QualityUpdatingItem(Item(item.name, updateSellIn(item.name), updateStrategy.newQuality(item)))

    private val updateStrategy = qualityUpdateStrategyFor(item)
    private fun updateSellIn(itemName: String) =
        // TODO express this in terms of 'legendary' items and bundle it with the quality update strategy
        if (itemName == "Sulfuras, Hand of Ragnaros") item.sellIn
        else item.sellIn - 1
}

private fun qualityUpdateStrategyFor(item: Item): QualityUpdateStrategy =
    when (item.name) {
        "+5 Dexterity Vest"                         -> DefaultQualityUpdateStrategy
        "Aged Brie"                                 -> IncreasingOverTimeQualityUpdateStrategy
        "Elixir of the Mongoose"                    -> DefaultQualityUpdateStrategy
        "Sulfuras, Hand of Ragnaros"                -> LegendaryQualityUpdateStrategy
        "Backstage passes to a TAFKAL80ETC concert" -> ConcertTicketQualityUpdateStrategy
        else                                        -> error("Unknown item")
    }

sealed interface QualityUpdateStrategy {
    fun newQuality(item: Item): Int
}

object DefaultQualityUpdateStrategy : QualityUpdateStrategy {
    override fun newQuality(item: Item): Int = max(0, item.quality - 1)
}

object LegendaryQualityUpdateStrategy : QualityUpdateStrategy {
    override fun newQuality(item: Item): Int = item.quality
}

object IncreasingOverTimeQualityUpdateStrategy : QualityUpdateStrategy {
    // TODO magic number 50
    override fun newQuality(item: Item): Int = min(50, item.quality + 1)
}

object ConcertTicketQualityUpdateStrategy : QualityUpdateStrategy {
    override fun newQuality(item: Item): Int =
            // TODO magic number 50
            when (item.sellIn) {
                in 6..10 -> min(50, item.quality + 2)
                in 1..5  -> min(50, item.quality + 3)
                0        -> 0
                else     -> min(50, item.quality + 1)
            }
}
