package com.gildedrose

class GildedRose(items: Array<Item>) {
    private val qualityUpdatingItems = items.map { QualityUpdatingItem(it) }.toMutableList()

    fun updateQuality() {
        qualityUpdatingItems.mapIndexed { i, item ->
            qualityUpdatingItems[i] = item.updateQuality()
        }
    }

    fun items() = qualityUpdatingItems.map { it.underlyingItem }
}