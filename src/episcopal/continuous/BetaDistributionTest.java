package episcopal.continuous;

import org.junit.Test;

import static org.junit.Assert.*;

public class BetaDistributionTest {
    @Test
    public void cumulative() throws Exception {
        BetaDistribution d = new BetaDistribution(1, 2);
        assertEquals(0.91, d.cumulative(0.7f), 0.001);
    }

}