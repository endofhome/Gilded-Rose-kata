package com.gildedrose

class SelfUpdatingItem(private val item: Item) {
    val underlyingItem = Item(item.name, item.sellIn, item.quality)
    fun update(): SelfUpdatingItem = SelfUpdatingItem(
            Item(
                item.name,
                itemType.sellInUpdateStrategy.newSellIn(item),
                itemType.qualityUpdateStrategy.newQuality(item)
            )
    )

    private val itemType = itemTypeFor(item.name)
}