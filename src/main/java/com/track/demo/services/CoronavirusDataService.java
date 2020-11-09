package com.track.demo.services;


import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.track.demo.models.LocationStats;

@Service
public class CoronavirusDataService 
{
	private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
	private List<LocationStats> allStats = new ArrayList<>();
	public List<LocationStats> getAllStats() {
		return allStats;
	}
	
	@PostConstruct
	@Scheduled(cron = "* * 1 * * *")
	public void fetchVirusData() throws IOException, InterruptedException
	{
		List<LocationStats> newStats = new ArrayList<>();
		/*HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(VIRUS_DATA_URL)).build();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		System.out.println(response.body());*/
		 HttpClient client = HttpClientBuilder.create().build();
	        HttpGet request = new HttpGet(VIRUS_DATA_URL);
	        String content ="";
	        try {
	            HttpResponse response = client.execute(request);
	            HttpEntity entity = response.getEntity();

	            // Read the contents of an entity and return it as a String.
	            content = EntityUtils.toString(entity);
	            
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        StringReader csvBodyReader = new StringReader(content);
	        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
	        
	        for (CSVRecord record : records) {
	        	LocationStats locationStat = new LocationStats();
	        	locationStat.setState(record.get("Province/State"));
	        	locationStat.setCountry(record.get("Country/Region"));
	        	int latestCases = Integer.parseInt(record.get(record.size()-1));
	        	int previousCases = Integer.parseInt(record.get(record.size()-2));
	           	locationStat.setLatestTotal(latestCases);
	        	locationStat.setDiffFromPrevDay(latestCases-previousCases);
	            newStats.add(locationStat);
	        }
	        
	        this.allStats=newStats;
	}


}
