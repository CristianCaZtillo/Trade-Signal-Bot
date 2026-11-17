package jar.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jar.model.TradeSignal;
import jar.service.MarketDataService;
import jar.service.StrategyService;

// REST controller exposing trade signal evaluation endpoints for the web dashboard.
@RestController
@RequestMapping("/api/signals")
public class DashboardController{
    
    private final MarketDataService marketDataService;
    private final StrategyService strategyService;

    /**
     * Constructor-based dependency injection for market data and strategy services.
     *
     * @param marketDataService Service that fetches historical closing prices
     * @param strategyService   Service that computes SMA crossover signals
     */
    public DashboardController(MarketDataService marketDataService, StrategyService strategyService){
        this.marketDataService = marketDataService;
        this.strategyService = strategyService;
    }

    /**
     * Handles GET requests to {@code /api/signals/{ticker}} by fetching recent market data
     * and returning an evaluated trade signal as JSON.
     *
     * @param ticker Stock symbol passed in the URL path (case-insensitive)
     * @return evaluated {@link TradeSignal} object
     */
    @GetMapping("/{ticker}")
    public TradeSignal getTickSignal(@PathVariable String ticker){
        // Normalize ticker symbol to uppercase (e.g., "aapl" -> "AAPL")
        String normalTicker = ticker.toUpperCase();
        
        // Fetch historical closing prices from Alpaca and evaluate the SMA strategy
        List<Double> prices = marketDataService.getClosingPrices(normalTicker);
        
        return strategyService.evaluateStrategy(normalTicker, prices);
    }

}