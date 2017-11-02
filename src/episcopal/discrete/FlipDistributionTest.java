package episcopal.discrete;

import org.junit.Test;

import static org.junit.Assert.*;

public class FlipDistributionTest {
    @Test
    public void sample() throws Exception {
        DiscreteSample<Boolean> s = new FlipDistribution(0.7f).sample();

        assertEquals(0.7f, s.get(true), 0.1);
        assertEquals(0.3f, s.get(false), 0.1);
    }

}