package com.example.Coronavirus.tracker.service;

import com.example.Coronavirus.tracker.model.LocationStats;
import com.example.Coronavirus.tracker.util.Constant;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class DataService {
    private List<LocationStats> locationStats = new ArrayList<>();

    public List<LocationStats> getLocationStats() {

        return locationStats;
    }

    @PostConstruct
    @Scheduled(cron = "* * 1 * * *")
    public void fetchData() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        List<LocationStats> locationStatsList = new ArrayList<>();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(Constant.BASE_URL))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        StringReader stringReader = new StringReader(response.body());
        Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(stringReader);

        for (CSVRecord record : records) {

            LocationStats stats = new LocationStats();
            stats.setCountry(record.get("Country/Region"));
            stats.setState(record.get("Province/State"));
            int latestCases = Integer.parseInt(record.get(record.size() -1));
            int prevDay = Integer.parseInt(record.get(record.size() -2));

            stats.setLatestTotalCases(latestCases);
            stats.setDiffFromPrevDay(latestCases - prevDay);
            locationStatsList.add(stats);
        }
        this.locationStats = locationStatsList;
    }

}
