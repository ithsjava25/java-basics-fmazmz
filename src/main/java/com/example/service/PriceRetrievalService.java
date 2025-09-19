package com.example.service;

import com.example.api.ElpriserAPI;
import com.example.model.PricePoint;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PriceRetrievalService {
  private final ElpriserAPI elpriserAPI;

  public PriceRetrievalService(ElpriserAPI elpriserAPI) {
    this.elpriserAPI = elpriserAPI;
  }

  public List<PricePoint> getPricePoints(ElpriserAPI.Prisklass zone, LocalDate date) {
    List<PricePoint> pricePoints = new ArrayList<>();

    // Collect raw entries for today and tomorrow, normalize the data later to handle large datasets.
    List<ElpriserAPI.Elpris> raw = new ArrayList<>();
    for (LocalDate d : List.of(date, date.plusDays(1))) {
      List<ElpriserAPI.Elpris> dayData = elpriserAPI.getPriser(d, zone);
      if (dayData != null) {
        raw.addAll(dayData);
      }
    }

    // Group all price points to its hour, to handle quarterly price points.
    // This will map each LocalDateTime to its own bucket of 1 or many price points.
    Map<LocalDateTime, List<ElpriserAPI.Elpris>> grouped =
            raw.stream().collect(Collectors.groupingBy(r ->
                    r.timeStart().toLocalDateTime().truncatedTo(ChronoUnit.HOURS)));

    // Average each hour (bucket) and create an internal PricePoint DTO as the extra fields are not required.
    grouped.forEach((hourStart, list) -> {
      double avg = list.stream().mapToDouble(ElpriserAPI.Elpris::sekPerKWh).average().orElse(0.0);
      pricePoints.add(new PricePoint(hourStart, avg));
    });

    // Sort by time to keep output consistent.
    pricePoints.sort(Comparator.comparing(PricePoint::time));

    return pricePoints;
  }
}
