package jar.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

// Service class Annotations responsible for fetching historical daily market data from the Alpaca.
@Service
public class MarketDataService{
    // Inject Alpaca credentials from application.properties
    @Value("${alpaca.api.key}")
    private String apiKey;

    @Value("${alpaca.api.secret}")
    private String apiSecret;

    // Synchronous HTTP client and Jackson JSON parser
    private final RestClient restClient = RestClient.create();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Retrieves up to 40 daily closing prices for the given stock ticker.
     *
     * @param ticker Stock symbol (e.g., "AAPL", "MSFT")
     * @return Chronological list of closing prices, or an empty list if unavailable
     */
    public List<Double> getClosingPrices(String ticker){
        List<Double> closingPrices = new ArrayList<>();

        // Look back 75 calendar days to guarantee >= 30 trading days across weekends and holidays
        String startDate = LocalDate.now().minusDays(75).toString();

        try { 
            // Execute GET request to Alpaca's bars endpoint using the free IEX feed
            String jsonResponse = restClient.get()
            .uri("https://data.alpaca.markets/v2/stocks/{ticker}/bars?timeframe=1Day&start={start}&feed=iex&limit=1000", ticker.trim().toUpperCase(), startDate)
                .header("APCA-API-KEY-ID", apiKey)
                .header("APCA-API-SECRET-KEY", apiSecret)
                .retrieve()
                .body(String.class);

            // Parse the raw JSON response and navigate to the "bars" array
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode barsNode = root.path("bars");

            // Extract the closing price ("c") from each daily bar object
            if (barsNode.isArray()) {
                for (JsonNode bar : barsNode) {
                    closingPrices.add(bar.path("c").asDouble());
                }
            }
            else {
                System.out.println("No bars array returned for " + ticker + ". Raw response: " + jsonResponse);
            }
        } catch (Exception e){
            // Log network, authentication, or parsing errors without crashing the endpoint
            System.err.println("Error fetching market data for " + ticker + ": " + e.getMessage());
        }

        return closingPrices;
    }
}