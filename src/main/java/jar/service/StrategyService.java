package jar.service;

// tell Spring Boot to manage this class as your business-logic layer
import java.util.List;

import org.springframework.stereotype.Service;

import jar.model.TradeSignal;
import jar.model.TradeSignal.SignalType;

// Business-logic service that evaluates a Simple Moving Average (SMA) crossover strategy to generate BUY, SELL, or HOLD trade signals.
@Service
public class StrategyService{ 

    /**
     * Evaluates a 10-day vs. 30-day SMA crossover using chronological closing prices.
     * Safely defaults to HOLD if fewer than 30 data points are available.
     *
     * @param ticker        Stock symbol being analyzed
     * @param closingPrices Chronological list of historical daily closing prices
     * @return a {@link TradeSignal} containing the recommended action and latest price
     */
    public TradeSignal evaluateStrategy(String ticker, List<Double> closingPrices){
        // Extract the most recent closing price, defaulting to 0.0 if the list is empty
        double currPrice = (closingPrices != null && !closingPrices.isEmpty()) ? (closingPrices.get(closingPrices.size() - 1)) : 0.0;

        // Need 31 bars to compare yesterday's 30-day SMA with today's 10-day SMA
        if (closingPrices == null || closingPrices.size() < 31){
            return new TradeSignal(ticker, SignalType.HOLD, currPrice);
        }

        int totalSize = closingPrices.size();

        // Today's SMAs (ending at totalBars)
        double currfastAverage = calculateAverage(closingPrices, 10, totalSize);
        double currslowAverage = calculateAverage(closingPrices, 30, totalSize);

        // Yesterday's SMAs (ending at totalBars - 1)
        double prevFastAverage = calculateAverage(closingPrices, 10, totalSize-1);
        double preSlowAverage = calculateAverage(closingPrices, 30, totalSize-1);

        // Golden Cross: 10-day SMA just crossed ABOVE the 30-day SMA
        if (prevFastAverage <= preSlowAverage && currfastAverage > currslowAverage){
            return new TradeSignal(ticker, SignalType.BUY, currPrice);
        }  
        // Death Cross: 10-day SMA just crossed BELOW the 30-day SMA
        else if (prevFastAverage >= preSlowAverage && currfastAverage < currslowAverage){
            return new TradeSignal(ticker, SignalType.SELL, currPrice);
        }

        // Averages are equal; maintain current position
        return new TradeSignal(ticker, SignalType.HOLD, currPrice);
    }

    /**
     * Calculates the Simple Moving Average (SMA) over the trailing {@code period} prices.
     *
     * @param prices Chronological list of closing prices
     * @param period Number of trailing days to include in the average
     * @return arithmetic mean of the most recent {@code period} prices
     */
    private double calculateAverage(List<Double> prices, int period, int endIndex){
        double sum = 0;

        for (int i = endIndex - period; i < endIndex; i++){
            sum += prices.get(i);
        }

        return sum / period;
    } 
}