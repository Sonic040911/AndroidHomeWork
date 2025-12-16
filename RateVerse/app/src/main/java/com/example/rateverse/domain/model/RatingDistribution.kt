package com.example.rateverse.domain.model

data class RatingDistribution(
    val score1Count: Int = 0,
    val score2Count: Int = 0,
    val score3Count: Int = 0,
    val score4Count: Int = 0,
    val score5Count: Int = 0
) {
    val totalReviews = score1Count + score2Count + score3Count + score4Count + score5Count

    fun getPercentage(scoreCount: Int): Float {
        return if (totalReviews == 0) 0f else scoreCount.toFloat() / totalReviews
    }
}