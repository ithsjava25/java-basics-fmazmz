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

    // Make sure hours requested do not exceed amount of PricePoints available.
    if (pricePoints.size() < hours) {
      return List.of();
    }


    double currentSum = 0;
    for (int i = 0; i < hours; i++) {
      currentSum += pricePoints.get(i).price();
    }

    double minSum = currentSum;
    int bestStartIndex = 0;

    for (int i = 1; i <= pricePoints.size() - hours; i++) {
      currentSum = currentSum - pricePoints.get(i - 1).price() + pricePoints.get(i + hours - 1).price();

      if (currentSum < minSum) {
        minSum = currentSum;
        bestStartIndex = i;
      }
    }
    return pricePoints.subList(bestStartIndex, bestStartIndex + hours);
  }

}
