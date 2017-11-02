package episcopal.discrete;

import org.junit.Test;

import static org.junit.Assert.*;

public class BernoulliDistributionTest {
    @Test
    public void sample() throws Exception {
        DiscreteSample<Integer> s = new BernoulliDistribution(0.7f).sample();

        assertEquals(0.7f, s.get(1), 0.1);
        assertEquals(0.3f, s.get(0), 0.1);
    }

}