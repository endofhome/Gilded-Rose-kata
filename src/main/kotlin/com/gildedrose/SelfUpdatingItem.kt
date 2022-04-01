package com.gildedrose

class SelfUpdatingItem(private val item: Item) {
    val underlyingItem = Item(item.name, item.sellIn, item.quality)
    fun update(): SelfUpdatingItem = SelfUpdatingItem(
            Item(
                item.name,
                sellInUpdateStrategy.newSellIn(item),
                qualityUpdateStrategy.newQuality(item)
            )
    )

    private val qualityUpdateStrategy = qualityUpdateStrategyFor(item)
    private val sellInUpdateStrategy = sellInUpdateStrategyFor(item)
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

private fun sellInUpdateStrategyFor(item: Item): SellInUpdateStrategy =
    when (item.name) {
        "+5 Dexterity Vest",
        "Elixir of the Mongoose",
        "Aged Brie",
        "Backstage passes to a TAFKAL80ETC concert" -> DefaultSellInUpdateStrategy
        "Sulfuras, Hand of Ragnaros"                -> LegendarySellInUpdateStrategy
        else                                        -> error("Unknown item")
    }