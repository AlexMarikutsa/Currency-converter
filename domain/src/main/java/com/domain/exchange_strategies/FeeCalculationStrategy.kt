package com.domain.exchange_strategies

interface FeeCalculationStrategy {

    fun calculateFee(amount: Double): Double
}