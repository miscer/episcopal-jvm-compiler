import static org.junit.Assert.*;

public class OperatorsTest {
    @org.junit.Test
    public void testSingleValueAnd() throws Exception {
        DiscreteSample<Boolean> left = DiscreteSample.create(true);
        DiscreteSample<Boolean> right = DiscreteSample.create(true);
        DiscreteSample<Boolean> result = Operators.and(left, right);
        assertEquals(result.get(true), 1, 0.001);
    }

    @org.junit.Test
    public void testMultipleValuesAnd() throws Exception {
        DiscreteSample<Boolean> left = new DiscreteSample<Boolean>().add(true, 0.3f).add(false, 0.7f);
        DiscreteSample<Boolean> right = new DiscreteSample<Boolean>().add(true, 0.2f).add(false, 0.8f);
        DiscreteSample<Boolean> result = Operators.and(left, right);

        assertEquals(result.get(true), 0.3 * 0.2, 0.001);
        assertEquals(result.get(false), 0.3 * 0.8 + 0.7 * 0.2 + 0.7 * 0.8, 0.001);
    }

    @org.junit.Test
    public void testSingleValueOr() throws Exception {
        DiscreteSample<Boolean> left = DiscreteSample.create(true);
        DiscreteSample<Boolean> right = DiscreteSample.create(false);
        DiscreteSample<Boolean> result = Operators.or(left, right);
        assertEquals(result.get(true), 1, 0.001);
    }

    @org.junit.Test
    public void testMultipleValuesOr() throws Exception {
        DiscreteSample<Boolean> left = new DiscreteSample<Boolean>().add(true, 0.3f).add(false, 0.7f);
        DiscreteSample<Boolean> right = new DiscreteSample<Boolean>().add(true, 0.2f).add(false, 0.8f);
        DiscreteSample<Boolean> result = Operators.or(left, right);

        assertEquals(result.get(true), 0.3 * 0.2 + 0.3 * 0.8 + 0.7 * 0.2, 0.001);
        assertEquals(result.get(false), 0.7 * 0.8, 0.001);
    }

}