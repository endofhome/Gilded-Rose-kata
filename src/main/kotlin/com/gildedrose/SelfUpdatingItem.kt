package com.gildedrose

class SelfUpdatingItem(private val item: Item) {
    val underlyingItem = Item(item.name, item.sellIn, item.quality)
    // TODO this still conflates updating `quality` and `sellIn`
    fun updateQuality(): SelfUpdatingItem = SelfUpdatingItem(Item(item.name, updateSellIn(item.name), updateStrategy.newQuality(item)))

    private val updateStrategy = qualityUpdateStrategyFor(item)
    private fun updateSellIn(itemName: String) =
        // TODO express this in terms of 'legendary' items and bundle it with the quality update strategy
        if (itemName == "Sulfuras, Hand of Ragnaros") item.sellIn
        else item.sellIn - 1
}

private fun qualityUpdateStrategyFor(item: Item): QualityUpdateStrategy =
    when (item.name) {
        "+5 Dexterity Vest",
        "Elixir of the Mongoose"                    -> DefaultQualityUpdateStrategy
        "Aged Brie"                                 -> IncreasingOverTimeQualityUpdateStrategy
        "Sulfuras, Hand of Ragnaros"                -> LegendaryQualityUpdateStrategy
        "Backstage passes to a TAFKAL80ETC concert" -> ConcertTicketQualityUpdateStrategy
        else                                        -> error("Unknown item")
    }