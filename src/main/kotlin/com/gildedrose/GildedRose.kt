package com.gildedrose

import com.gildedrose.item.SelfUpdatingItem

class GildedRose(items: Array<Item>) {
    private val selfUpdatingItems = items.map { SelfUpdatingItem(it) }.toMutableList()

    fun updateQuality() {
        selfUpdatingItems.mapIndexed { i, item ->
            selfUpdatingItems[i] = item.update()
        }
    }

    fun items() = selfUpdatingItems.map { it.underlyingItem }
}