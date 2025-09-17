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

    public static void printCheapestAndMostExpensiveHours(PricePoint cheapest, PricePoint expensive) {
        if (cheapest != null) {
            System.out.printf("Lägsta pris: %s : %.2f SEK/kWh%n", cheapest.time(), cheapest.price());
        }
        if (expensive != null) {
            System.out.printf("Högsta pris: %s : %.2f SEK/kWh%n", expensive.time(), expensive.price());
        }
    }

    public static void printMeanPrice(double meanPrice) {
        System.out.printf("Medelpris: %.2f SEK/kWh%n", meanPrice);
    }


    public static void printChargingWindow(List<PricePoint> window, int hours) {
        if (window == null || window.isEmpty()) {
            System.out.println("Ingen laddningsperiod hittades för " + hours + " timmar.");
            return;
        }

        PricePoint start = window.getFirst();

        double avg = window.stream()
                .mapToDouble(PricePoint::price)
                .average()
                .orElse(0);

        System.out.printf("Påbörja laddning kl %02d:00%n", start.time().getHour());
        System.out.printf("Medelpris för fönster: %.2f öre%n", avg * 100);
    }

}
