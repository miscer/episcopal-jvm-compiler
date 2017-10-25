import com.sun.org.apache.xpath.internal.operations.Bool;

import static org.junit.Assert.*;

public class OperatorsTest {
    @org.junit.Test
    public void testSingleValueAnd() throws Exception {
        Result<Boolean> left = Result.create(true);
        Result<Boolean> right = Result.create(true);
        Result<Boolean> result = Operators.and(left, right);
        assertEquals(result.get(true), 1, 0.001);
    }

    @org.junit.Test
    public void testMultipleValuesAnd() throws Exception {
        Result<Boolean> left = new Result<Boolean>().add(true, 0.3f).add(false, 0.7f);
        Result<Boolean> right = new Result<Boolean>().add(true, 0.2f).add(false, 0.8f);
        Result<Boolean> result = Operators.and(left, right);

        assertEquals(result.get(true), 0.3 * 0.2, 0.001);
        assertEquals(result.get(false), 0.3 * 0.8 + 0.7 * 0.2 + 0.7 * 0.8, 0.001);
    }

    @org.junit.Test
    public void testSingleValueOr() throws Exception {
        Result<Boolean> left = Result.create(true);
        Result<Boolean> right = Result.create(false);
        Result<Boolean> result = Operators.or(left, right);
        assertEquals(result.get(true), 1, 0.001);
    }

    @org.junit.Test
    public void testMultipleValuesOr() throws Exception {
        Result<Boolean> left = new Result<Boolean>().add(true, 0.3f).add(false, 0.7f);
        Result<Boolean> right = new Result<Boolean>().add(true, 0.2f).add(false, 0.8f);
        Result<Boolean> result = Operators.or(left, right);

        assertEquals(result.get(true), 0.3 * 0.2 + 0.3 * 0.8 + 0.7 * 0.2, 0.001);
        assertEquals(result.get(false), 0.7 * 0.8, 0.001);
    }

}