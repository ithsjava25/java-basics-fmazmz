package com.example.cli;

import com.example.model.PricePoint;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

public class CliPrinter {

    public static void printPrices(List<PricePoint> prices, String zone, LocalDate date, boolean sorted) {
        if (prices.isEmpty()) {
            System.out.println("No data available for zone " + zone + " and date " + date);
            return;
        }

        System.out.println("...Electricity prices for zone " + zone + " (starting " + date + ")...");

        // Sort by price descending if sorted flag is detected in args options.
        if (sorted) {
            prices.stream()
                    .sorted(Comparator.comparingDouble(PricePoint::price).reversed())
                    .forEach(p ->
                            System.out.printf("%s   :   %.3f SEK/kWh%n", p.time(), p.price())
                    );
        } else {
            prices.forEach(p ->
                    System.out.printf("%s   :   %.3f SEK/kWh%n", p.time(), p.price())
            );
        }
    }
}
