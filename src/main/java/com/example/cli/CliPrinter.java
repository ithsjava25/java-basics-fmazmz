package com.example.cli;

import com.example.model.PricePoint;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class CliPrinter {

    private static final DecimalFormatSymbols SV =
            DecimalFormatSymbols.getInstance(Locale.of("sv", "SE"));

    private static final DecimalFormat ORE_2 = new DecimalFormat("0.00", SV);
    private static final DateTimeFormatter HM = DateTimeFormatter.ofPattern("HH:mm");


    private static String hourRange(LocalDateTime start) {
        int s = start.getHour();
        int e = (s + 1) % 24;
        return String.format("%02d-%02d", s, e);
    }

    private static String formatOre(double sekPerKWh) {
        return ORE_2.format(sekPerKWh * 100.0);
    }

    public static void printPrices(List<PricePoint> prices, String zone, LocalDate date, boolean sorted) {
        if (prices.isEmpty()) {
            System.out.println("No data available for zone " + zone + " and date " + date);
            return;
        }

        System.out.println("...Electricity prices for zone " + zone + " (starting " + date + ")...");

        // Sort by price descending if sorted flag is detected in args options.
        if (sorted) {
            prices.stream()
                    .sorted(Comparator.comparingDouble(PricePoint::price))
                    .forEach(p ->
                            System.out.println(hourRange((p.time())) + " " + formatOre(p.price()) + " öre")
                    );
        } else {
            prices.forEach(p ->
                    System.out.printf("%s   :   %.3f SEK/kWh%n", p.time(), p.price())
            );
        }
    }

    public static void printCheapestAndMostExpensiveHours(PricePoint cheapest, PricePoint expensive) {
        if (cheapest != null) {
            System.out.println("Lägsta pris: " + hourRange(cheapest.time()) + " -> " + formatOre(cheapest.price()) + " öre");
        }
        if (expensive != null) {
            System.out.println("Högsta pris: " + hourRange(expensive.time()) + " -> " + formatOre(expensive.price()) + " öre");
        }
    }

    public static void printMeanPrice(double meanPrice) {
        System.out.println("Medelpris: " + formatOre(meanPrice) + " öre");
    }


    public static void printChargingWindow(List<PricePoint> window, int hours) {
        if (window == null || window.isEmpty()) {
            System.out.println("Ingen laddningsperiod hittades för " + hours + " timmar.");
            return;
        }

        String startStr = window.getFirst().time().format(HM);
        double avgSek = window.stream().mapToDouble(PricePoint::price).average().orElse(0.0);

        System.out.printf("%n... CHARGING ...%n");
        System.out.println("Påbörja laddning kl " + startStr);
        System.out.println("Medelpris för fönster: " + formatOre(avgSek) + " öre");
    }

}
