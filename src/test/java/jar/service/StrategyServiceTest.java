package jar.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import jar.model.TradeSignal;
import jar.model.TradeSignal.SignalType;

class StrategyServiceTest {

    private final StrategyService strategyService = new StrategyService();

    @Test
    void returnsHold_whenFewerThan31Prices() {
        List<Double> prices = new ArrayList<>(Collections.nCopies(30, 100.0));
        TradeSignal result = strategyService.evaluateStrategy("AAPL", prices);

        assertEquals(SignalType.HOLD, result.getSignal());
        assertEquals(100.0, result.getPrice());
    }

    @Test
    void returnsBuy_onExactBullishCrossover() {
        // 30 days flat at 100.0 -> yesterday's SMA10 = 100.0, SMA30 = 100.0 (prevFast <= prevSlow)
        List<Double> prices = new ArrayList<>(Collections.nCopies(30, 100.0));
        // Day 31 jumps to 130.0 -> today's SMA10 = 103.0, SMA30 = 101.0 (currFast > currSlow)
        prices.add(130.0);

        TradeSignal result = strategyService.evaluateStrategy("AAPL", prices);

        assertEquals(SignalType.BUY, result.getSignal());
        assertEquals(130.0, result.getPrice());
    }

    @Test
    void returnsHold_whenAlreadyInUptrendWithoutNewCrossover() {
        // 30 days flat at 100.0, then two days at 130.0
        List<Double> prices = new ArrayList<>(Collections.nCopies(30, 100.0));
        prices.add(130.0); // Crossover happened here
        prices.add(130.0); // Already above -> should NOT re-trigger BUY

        TradeSignal result = strategyService.evaluateStrategy("AAPL", prices);

        assertEquals(SignalType.HOLD, result.getSignal());
    }

    @Test
    void returnsSell_onExactBearishCrossover() {
        // 30 days flat at 100.0 -> yesterday's SMA10 = 100.0, SMA30 = 100.0 (prevFast >= prevSlow)
        List<Double> prices = new ArrayList<>(Collections.nCopies(30, 100.0));
        // Day 31 drops to 70.0 -> today's SMA10 = 97.0, SMA30 = 99.0 (currFast < currSlow)
        prices.add(70.0);

        TradeSignal result = strategyService.evaluateStrategy("AAPL", prices);

        assertEquals(SignalType.SELL, result.getSignal());
        assertEquals(70.0, result.getPrice());
    }
}
