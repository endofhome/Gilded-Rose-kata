package com.gildedrose

sealed class ItemType(val qualityUpdateStrategy: QualityUpdateStrategy, val sellInUpdateStrategy: SellInUpdateStrategy)

object DefaultItemType : ItemType(
    qualityUpdateStrategy = DefaultQualityUpdateStrategy,
    sellInUpdateStrategy = DefaultSellInUpdateStrategy
)

object AgedBrieItemType : ItemType(
    qualityUpdateStrategy = IncreasingOverTimeQualityUpdateStrategy,
    sellInUpdateStrategy = DefaultSellInUpdateStrategy
)

object LegendaryItemType : ItemType(
    qualityUpdateStrategy = LegendaryQualityUpdateStrategy,
    sellInUpdateStrategy = LegendarySellInUpdateStrategy
)

object ConcertTicketItemType : ItemType(
    qualityUpdateStrategy = ConcertTicketQualityUpdateStrategy,
    sellInUpdateStrategy = DefaultSellInUpdateStrategy
)

fun itemTypeFor(itemName: String): ItemType =
    when (itemName) {
        "+5 Dexterity Vest",
        "Elixir of the Mongoose"                    -> DefaultItemType
        "Aged Brie"                                 -> AgedBrieItemType
        "Sulfuras, Hand of Ragnaros"                -> LegendaryItemType
        "Backstage passes to a TAFKAL80ETC concert" -> ConcertTicketItemType
        else                                        -> error("Unknown item type: \"$itemName\"")
    }