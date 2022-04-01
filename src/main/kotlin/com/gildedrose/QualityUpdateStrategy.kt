package com.gildedrose

sealed interface QualityUpdateStrategy {
    fun newQuality(item: Item): Int
}

object DefaultQualityUpdateStrategy : QualityUpdateStrategy {
    override fun newQuality(item: Item): Int = Integer.max(0, item.quality - 1)
}

object LegendaryQualityUpdateStrategy : QualityUpdateStrategy {
    override fun newQuality(item: Item): Int = item.quality
}

object IncreasingOverTimeQualityUpdateStrategy : QualityUpdateStrategy {
    override fun newQuality(item: Item): Int = increaseUpToDefaultMaxQuality(item.quality + 1)
}

object ConcertTicketQualityUpdateStrategy : QualityUpdateStrategy {
    override fun newQuality(item: Item): Int =
            when (item.sellIn) {
                in 6..10 -> increaseUpToDefaultMaxQuality(item.quality + 2)
                in 1..5  -> increaseUpToDefaultMaxQuality( item.quality + 3)
                0        -> 0
                else     -> increaseUpToDefaultMaxQuality(item.quality + 1)
            }
}

private const val defaultMaxQuality = 50
private fun increaseUpToDefaultMaxQuality(tryIncreaseTo: Int) = Integer.min(tryIncreaseTo, defaultMaxQuality)
