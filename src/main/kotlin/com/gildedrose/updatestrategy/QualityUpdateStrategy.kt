package com.gildedrose.updatestrategy

import com.gildedrose.Item

sealed interface QualityUpdateStrategy {
    fun newQuality(item: Item): Int
}

class DefaultQualityUpdateStrategy(private val qualityDegradationFactor: QualityDegradationFactor) : QualityUpdateStrategy {
    override fun newQuality(item: Item): Int = Integer.max(0, item.quality - qualityDegradationFactor.value)
}

object LegendaryQualityUpdateStrategy : QualityUpdateStrategy {
    override fun newQuality(item: Item): Int = item.quality
}

sealed interface IncreasingOverTimeQualityUpdateStrategy : QualityUpdateStrategy
object IncreasingSteadilyOverTimeQualityUpdateStrategy : IncreasingOverTimeQualityUpdateStrategy {
    override fun newQuality(item: Item): Int = increaseUpToDefaultMaxQuality(item.quality + 1)
}

object ConcertTicketQualityUpdateStrategy : IncreasingOverTimeQualityUpdateStrategy {
    override fun newQuality(item: Item): Int =
        when (item.sellIn) {
            in 6..10 -> increaseUpToDefaultMaxQuality(item.quality + 2)
            in 1..5  -> increaseUpToDefaultMaxQuality( item.quality + 3)
            0        -> 0
            else     -> increaseUpToDefaultMaxQuality(item.quality + 1)
        }
}

sealed interface QualityDegradationFactor {
    val value: Int
}

object DefaultQualityDegradationFactor : QualityDegradationFactor {
    override val value = 1
}

object ConjuredQualityDegradationFactor : QualityDegradationFactor {
    override val value = 2
}

private const val defaultMaxQuality = 50
private fun increaseUpToDefaultMaxQuality(tryIncreaseTo: Int) = Integer.min(tryIncreaseTo, defaultMaxQuality)
