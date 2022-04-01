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
    // TODO the prefix is an empty string when present, which is a bit odd. Is there a better way to express this.
    val prefixAndItem = itemName.split("Conjured").map { it.trim() }
    val (qualityDegradationFactor, itemNameWithoutPrefix) =
        // TODO join non-prefixed items to string, don't take the first. Needs a test.
        when {
            prefixAndItem.size == 2 && prefixAndItem.first().isEmpty() -> Pair(ConjuredQualityDegradationFactor, prefixAndItem[1])
            else                                                       -> Pair(DefaultQualityDegradationFactor, prefixAndItem[0])
        }

    return when (itemNameWithoutPrefix) {
        "Aged Brie"                                 -> AgedBrieItemType
        "Sulfuras, Hand of Ragnaros"                -> LegendaryItemType
        "Backstage passes to a TAFKAL80ETC concert" -> ConcertTicketItemType
        else                                        -> DefaultItemType(qualityDegradationFactor)
    }
}