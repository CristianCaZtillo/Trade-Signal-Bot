# trade-signal-bot

A modular Java Spring Boot stock trade signal microservice and web dashboard that evaluates 10-day vs. 30-day Simple Moving Average (SMA) crossovers on historical daily closing prices from the Alpaca Market Data API v2 (IEX feed).

## Architecture

* **jar.model.TradeSignal**: Domain model encapsulating the stock ticker, evaluated 'SignalType' ('BUY', 'SELL, 'HOLD'), latest closing price, and evaluation timestamp.
* **jar.service.MarketDataService**: Fetches daily historical stock bars (timeframe=1Day) from Alpaca Market Data API v2 using Spring's RestClient and Jackson JSON parsing.
* **jar.service.StrategyService**: Computes today's and yesterday's 10-day and 30-day Simple Moving Averages to detect exact bullish ('BUY') and bearish ('SELL') crossovers, defaulting to 'HOLD' when no crossover occurs or fewer than 31 trading days are available.
* **jar.controller.DashboardController**: Exposes 'GET /api/signals/{ticker}' to evaluate symbols on demand and return 'TradeSignal' JSON responses.
* **Web Dashboard (src/main/resources/static/index.html)**: Single-page HTML/CSS/JS interface served on 'http://localhost:8080/' featuring custom ticker search, quick-select watchlist chips ('AAPL', 'MSFT', 'NVDA', 'SPY', 'TSLA'), and session scan history.

## Setup & Running Locally

1. Add your Alpaca API credentials to 'src/main/resources/application.properties':
   properties
   alpaca.api.key=YOUR_ALPACA_API_KEY
   alpaca.api.secret=YOUR_ALPACA_API_SECRET
