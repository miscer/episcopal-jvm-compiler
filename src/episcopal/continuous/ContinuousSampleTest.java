package episcopal.continuous;

import episcopal.discrete.DiscreteSample;
import org.junit.Test;

import static org.junit.Assert.*;

public class ContinuousSampleTest {
    @Test
    public void add() throws Exception {
        ContinuousSample cs = new TestDistribution().sample().add(0.2f);
        DiscreteSample<Boolean> ds = cs.cumulative(0.7f);

        assertEquals(0.9f, ds.get(true), 0.1f);
        assertEquals(0.1f, ds.get(false), 0.1f);
    }

    @Test
    public void multiply() throws Exception {
        ContinuousSample cs = new TestDistribution().sample().multiply(2);
        DiscreteSample<Boolean> ds = cs.cumulative(0.3f);

        assertEquals(0.6f, ds.get(true), 0.1f);
        assertEquals(0.4f, ds.get(false), 0.1f);
    }

    @Test
    public void cumulative() throws Exception {
        ContinuousSample cs = new TestDistribution().sample();
        DiscreteSample<Boolean> ds = cs.cumulative(0.7f);

        assertEquals(0.7f, ds.get(true), 0.1f);
        assertEquals(0.3f, ds.get(false), 0.1f);
    }

    private class TestDistribution extends ContinuousDistribution {
        @Override
        public float cumulative(float n) {
            return n;
        }
    }

}