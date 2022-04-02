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
    val (itemNameWithoutPrefix, qualityDegradationFactor) =
        if (itemName.startsWith(conjuredPrefix)) {
            Pair(itemName.substringAfter(conjuredPrefix), ConjuredQualityDegradationFactor)
        } else {
            Pair(itemName, DefaultQualityDegradationFactor)
        }

    return when (itemNameWithoutPrefix) {
        "Aged Brie"                                 -> AgedBrieItemType
        "Sulfuras, Hand of Ragnaros"                -> LegendaryItemType
        "Backstage passes to a TAFKAL80ETC concert" -> ConcertTicketItemType
        else                                        -> DefaultItemType(qualityDegradationFactor)
    }
}

private const val conjuredPrefix = "Conjured "
