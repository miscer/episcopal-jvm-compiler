package episcopal;

import episcopal.discrete.DiscreteSample;
import org.junit.Test;

import static org.junit.Assert.*;

public class ObservationsTest {
    @Test
    public void select() throws Exception {
        DiscreteSample<Integer> s = new DiscreteSample<>();
        s.add(123, 0.3f);
        s.add(456, 0.7f);

        int n = Observations.select(s);

        assertTrue(n == 123 || n == 456);
    }

}