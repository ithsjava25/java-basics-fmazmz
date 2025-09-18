package com.example.service;

import com.example.model.PricePoint;

import java.util.Comparator;
import java.util.List;

public class PriceCalculatorService {

  public double calculateMean(List<PricePoint> pricePoints) {
    return pricePoints.stream()
            .mapToDouble(PricePoint::price)
            .average()
            .orElse(0.0);
  }


  public PricePoint findCheapestHour(List<PricePoint> pricePoints) {
    return pricePoints.stream()
            .min(Comparator.comparingDouble(PricePoint::price)
                    .thenComparing(PricePoint::time))
            .orElse(null);
  }

  public PricePoint findMostExpensiveHour(List<PricePoint> pricePoints) {
    return pricePoints.stream()
            .max(Comparator.comparingDouble(PricePoint::price)
                    .thenComparing(PricePoint::time))
            .orElse(null);
  }

  public List<PricePoint> findOptimalChargingWindow(List<PricePoint> pricePoints, int hours) {
    if (pricePoints == null || hours <= 0 || pricePoints.size() < hours) {
      return List.of();
    }

    // Convert double to longs / SEK to öre to avoid incorrect decimal point calculations.
    int n = pricePoints.size();
    var scaled = new long[n];
    for (int i = 0; i < n; i++) {
      scaled[i] = Math.round(pricePoints.get(i).price() * 100);
    }

    int bestStartIndex = findBestWindowStart(scaled, hours);
    return pricePoints.subList(bestStartIndex, bestStartIndex + hours);
  }

  private int findBestWindowStart(long[] scaled, int hours) {

    // First Hour.
    long currentSum = 0;
    for (int i = 0; i < hours; i++) {
      currentSum += scaled[i];
    }

    // Treat first hour as min.
    long minSum = currentSum;

    // Treat first hour as best start.
    int bestStartIndex = 0;

    for (int i = 1; i <= scaled.length - hours; i++) {
      currentSum += scaled[i + hours - 1] - scaled[i - 1];

      // If both sums have the same price, choose the earliest.
      if (currentSum < minSum || (currentSum == minSum && i < bestStartIndex)) {
        minSum = currentSum;
        bestStartIndex = i;
      }
    }

    return bestStartIndex;
  }

}
