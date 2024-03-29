package episcopal;

import episcopal.continuous.ContinuousSample;
import episcopal.continuous.NormalDistribution;
import episcopal.discrete.DiscreteSample;

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

    @org.junit.Test
    public void testNot() throws Exception {
        DiscreteSample<Boolean> s1 = new DiscreteSample<Boolean>().add(true, 0.3f).add(false, 0.7f);
        DiscreteSample<Boolean> s2 = Operators.not(s1);

        assertEquals(0.7, s2.get(true), 0.1);
        assertEquals(0.3, s2.get(false), 0.1);
    }

    @org.junit.Test
    public void testSingleContinuousLessThan() throws Exception {
        ContinuousSample left = new NormalDistribution(100, 15).sample();
        DiscreteSample<Float> right = DiscreteSample.create(100f);
        DiscreteSample<Boolean> result = Operators.lessThanContinuous(left, right);

        assertEquals(result.get(true), 0.5, 0.001);
        assertEquals(result.get(false), 0.5, 0.001);
    }

    @org.junit.Test
    public void testMultipleContinuousLessThan() throws Exception {
        ContinuousSample left = new NormalDistribution(100, 15).sample();
        DiscreteSample<Float> right = new DiscreteSample<Float>().add(70f, 0.3f).add(130f, 0.7f);
        DiscreteSample<Boolean> result = Operators.lessThanContinuous(left, right);

        assertEquals(0.6908, result.get(true), 0.001);
        assertEquals(0.3092, result.get(false), 0.001);
    }

}