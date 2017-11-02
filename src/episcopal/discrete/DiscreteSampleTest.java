package episcopal.discrete;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.*;

public class DiscreteSampleTest {
    @Test
    public void create() throws Exception {
        DiscreteSample<Integer> s = DiscreteSample.create(123);
        assertEquals(1, s.get(123), 0.1f);
    }

    @Test
    public void add() throws Exception {
        DiscreteSample<Integer> s = new DiscreteSample<>();
        s.add(123, 0.2f);
        s.add(123, 0.4f);
        s.add(456, 0.4f);

        assertEquals(0.6f, s.get(123), 0.1f);
    }

    @Test
    public void values() throws Exception {
        DiscreteSample<Integer> s = new DiscreteSample<>();
        s.add(123, 0.3f);
        s.add(456, 0.6f);
        s.add(789, 0.1f);

        assertEquals(new HashSet<>(Arrays.asList(123, 456, 789)), s.values());
    }

    @Test
    public void single() throws Exception {
        DiscreteSample<Integer> s1 = DiscreteSample.create(123);
        assertEquals(123, (int) s1.single());

        DiscreteSample<Integer> s2 = new DiscreteSample<>();
        s2.add(123, 0.3f);
        s2.add(456, 0.7f);
        assertNull(s2.single());
    }

}