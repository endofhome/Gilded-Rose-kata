package com.gildedrose

sealed class ItemType(val qualityUpdateStrategy: QualityUpdateStrategy, val sellInUpdateStrategy: SellInUpdateStrategy)

data class DefaultItemType(private val qualityDegradationFactor: QualityDegradationFactor) : ItemType(
    qualityUpdateStrategy = DefaultQualityUpdateStrategy(qualityDegradationFactor),
    sellInUpdateStrategy = DefaultSellInUpdateStrategy
)

object AgedBrieItemType : ItemType(
    qualityUpdateStrategy = IncreasingSteadilyOverTimeQualityUpdateStrategy,
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

fun itemTypeFor(itemName: String): ItemType {
    val prefixAndItem = itemName.split("Conjured")
    val (qualityDegradationFactor, itemNameWithoutPrefix) =
        when {
            prefixAndItem.size == 2 && prefixAndItem.first().isEmpty() -> Pair(ConjuredQualityDegradationFactor, prefixAndItem[1].trim())
            else                                                       -> Pair(DefaultQualityDegradationFactor, prefixAndItem[0].trim())
        }

    return when (itemNameWithoutPrefix) {
            "+5 Dexterity Vest",
            "Elixir of the Mongoose",
            "Mana Cake"                                 -> DefaultItemType(qualityDegradationFactor)
            "Aged Brie"                                 -> AgedBrieItemType
            "Sulfuras, Hand of Ragnaros"                -> LegendaryItemType
            "Backstage passes to a TAFKAL80ETC concert" -> ConcertTicketItemType
            else                                        -> error("Unknown item type: \"$itemNameWithoutPrefix\"")
    }
}