package com.capstone.merkado.Helpers;

import com.capstone.merkado.Objects.StoresDataObjects.MarketPrice;

import java.util.List;

public class MathFormula {

    /**
     * This sums up all the prices from a list of MarketPrice.
     * @param marketPrices list of the current market prices.
     * @return Float value of the sum of all market prices.
     */
    public static Float getTotalWeightedPrice(List<MarketPrice> marketPrices) {
        Float totalWeightedPrice = 0.0f;
        for (MarketPrice marketPrice : marketPrices) {
            totalWeightedPrice += marketPrice.getMarketPrice();
        }
        return totalWeightedPrice;
    }

    /**
     * Calculates for the inflation rate using the Total Weighted Price (use getTotalWeightedPrice).
     * @param currentTWP TWP of current time.
     * @param pastTWP TWP of past 24 hours.
     * @return decimal format of inflation rate. <i>Note: you must multiply this to 100 to convert it into percentage format.</i>
     */
    public static Float getInflationRate(Float currentTWP, Float pastTWP) {
        return (pastTWP != null && pastTWP != 0)
                ? ((currentTWP - pastTWP) / pastTWP)
                : null;
    }

    /**
     * Calculates for the purchasing power using the Total Weighted Price (use getTotalWeightedPrice).
     * @param currentTWP TWP of current time.
     * @param pastTWP TWP of past 24 hours.
     * @return decimal format of purchasing power. <i>Note: you must multiply this to 100 to convert it into percentage format.</i>
     */
    public static Float getPurchasingPower(Float currentTWP, Float pastTWP) {
        return (pastTWP != null && currentTWP != null && currentTWP != 0)
                ? pastTWP / currentTWP
                : null;
    }

    public static Float getNewPrice(Float currentPrice, Float sensitivityFactor, Integer supply, Integer demand) {
        return currentPrice * (1 + (sensitivityFactor * (demand / supply)));
    }
}
