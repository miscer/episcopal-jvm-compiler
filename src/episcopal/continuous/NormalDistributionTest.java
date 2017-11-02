package episcopal.continuous;

import org.junit.Test;

import static org.junit.Assert.*;

public class NormalDistributionTest {
    @Test
    public void cumulative() throws Exception {
        NormalDistribution d = new NormalDistribution(12, 5);
        assertEquals(0.0968, d.cumulative(5.5f), 0.0001);
    }

}