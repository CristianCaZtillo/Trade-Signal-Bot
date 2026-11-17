package jar.model;

import java.time.LocalDateTime;

// Domain model representing the output of a trade strategy evaluation for a single stock. Serialized into JSON by Spring's REST controller layer.
public class TradeSignal{
    // Supported trading actions produced by the strategy engine.
    public enum SignalType {
        BUY, SELL, HOLD
    }
    
    // private fields to answer each market event
    private String ticker; // Stock ticker symbols (AAPL, NVDA, ...)
    private SignalType signal; // Evaluated action (BUY, SELL, or HOLD) 
    private double price; // Most recent closing price at evaluation time
    private LocalDateTime timestamp; // Exact date and time when the signal was generated

    /**
     * Constructs a new TradeSignal and automatically records the current timestamp.
     *
     * @param ticker Stock symbol being evaluated
     * @param signal Resulting trading action
     * @param price  Latest closing price of the stock
     */
    public TradeSignal(String ticker, SignalType signal, double price){
        this.ticker = ticker;
        this.signal = signal;
        this.price = price;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * @return the stock ticker symbol
     */
    public String getTicker() {
        return ticker;
    }

    /**
     * @return the evaluated {@link SignalType}
     */
    public SignalType getSignal() {
        return signal;
    }

    /**
     * @return the latest closing price
     */
    public double getPrice() {
        return price;
    }

    /**
     * @return the timestamp when this signal was created
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}