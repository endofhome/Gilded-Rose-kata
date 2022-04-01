package com.gildedrose

sealed interface SellInUpdateStrategy {
    fun newSellIn(item: Item): Int
}

object DefaultSellInUpdateStrategy : SellInUpdateStrategy {
    override fun newSellIn(item: Item): Int = item.sellIn - 1
}

object LegendarySellInUpdateStrategy : SellInUpdateStrategy {
    override fun newSellIn(item: Item): Int = item.sellIn
}
