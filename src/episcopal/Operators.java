package episcopal;

import episcopal.continuous.ContinuousSample;
import episcopal.discrete.DiscreteSample;

/**
 * Implementations for Episcopal operators. If two discrete samples are used as operands, the operator is applied to
 * each combination of values in the sample and their probabilities are combined.
 */
public class Operators {

    /**
     * AND of two boolean discrete samples
     * @param left Left sample
     * @param right Right sample
     * @return ANDed sample
     */
    public static DiscreteSample<Boolean> and(DiscreteSample<Boolean> left, DiscreteSample<Boolean> right) {
        return combine(left, right, (a, b) -> a && b);
    }

    /**
     * OR of two boolean discrete samples
     * @param left Left sample
     * @param right Right sample
     * @return ORed sample
     */
    public static DiscreteSample<Boolean> or(DiscreteSample<Boolean> left, DiscreteSample<Boolean> right) {
        return combine(left, right, (a, b) -> a || b);
    }

    /**
     * Negation of a boolean discrete sample
     * @param sample Boolean sample
     * @return Negated sample
     */
    public static DiscreteSample<Boolean> not(DiscreteSample<Boolean> sample) {
        return new DiscreteSample<Boolean>().add(true, sample.get(false)).add(false, sample.get(true));
    }

    /**
     * Addition of two integer discrete samples
     * @param left Left sample
     * @param right Right sample
     * @return Added sample
     */
    public static DiscreteSample<Integer> addIntegers(DiscreteSample<Integer> left, DiscreteSample<Integer> right) {
        return combine(left, right, (a, b) -> a + b);
    }

    /**
     * Addition of two float discrete samples
     * @param left Left sample
     * @param right Right sample
     * @return Added sample
     */
    public static DiscreteSample<Float> addFloats(DiscreteSample<Float> left, DiscreteSample<Float> right) {
        return combine(left, right, (a, b) -> a + b);
    }

    /**
     * Subtraction of two integer discrete samples
     * @param left Left sample
     * @param right Right sample
     * @return Subtracted sample
     */
    public static DiscreteSample<Integer> subtractIntegers(DiscreteSample<Integer> left, DiscreteSample<Integer> right) {
        return combine(left, right, (a, b) -> a - b);
    }

    /**
     * Subtraction of two float discrete samples
     * @param left Left sample
     * @param right Right sample
     * @return Subtracted sample
     */
    public static DiscreteSample<Float> subtractFloats(DiscreteSample<Float> left, DiscreteSample<Float> right) {
        return combine(left, right, (a, b) -> a - b);
    }

    /**
     * Multiplication of two integer discrete samples
     * @param left Left sample
     * @param right Right sample
     * @return Multiplied sample
     */
    public static DiscreteSample<Integer> multiplyIntegers(DiscreteSample<Integer> left, DiscreteSample<Integer> right) {
        return combine(left, right, (a, b) -> a * b);
    }

    /**
     * Multiplication of two float discrete samples
     * @param left Left sample
     * @param right Right sample
     * @return Multiplied sample
     */
    public static DiscreteSample<Float> multiplyFloats(DiscreteSample<Float> left, DiscreteSample<Float> right) {
        return combine(left, right, (a, b) -> a * b);
    }

    /**
     * Division of two integer discrete samples
     * @param left Left sample
     * @param right Right sample
     * @return Divided sample
     */
    public static DiscreteSample<Integer> divideIntegers(DiscreteSample<Integer> left, DiscreteSample<Integer> right) {
        return combine(left, right, (a, b) -> a / b);
    }

    /**
     * Division of two float discrete samples
     * @param left Left sample
     * @param right Right sample
     * @return Divided sample
     */
    public static DiscreteSample<Float> divideFloats(DiscreteSample<Float> left, DiscreteSample<Float> right) {
        return combine(left, right, (a, b) -> a / b);
    }

    /**
     * Comparison of two integer discrete samples
     * @param left Left sample
     * @param right Right sample
     * @return Boolean sample
     */
    public static DiscreteSample<Boolean> equalIntegers(DiscreteSample<Integer> left, DiscreteSample<Integer> right) {
        return combine(left, right, Integer::equals);
    }

    /**
     * Comparison of two float discrete samples
     * @param left Left sample
     * @param right Right sample
     * @return Boolean sample
     */
    public static DiscreteSample<Boolean> equalFloats(DiscreteSample<Float> left, DiscreteSample<Float> right) {
        return combine(left, right, Float::equals);
    }

    /**
     * Comparison of two boolean discrete samples
     * @param left Left sample
     * @param right Right sample
     * @return Boolean sample
     */
    public static DiscreteSample<Boolean> equalBooleans(DiscreteSample<Boolean> left, DiscreteSample<Boolean> right) {
        return combine(left, right, Boolean::equals);
    }

    /**
     * Comparison of two integer discrete samples
     * @param left Left sample
     * @param right Right sample
     * @return Boolean sample
     */
    public static DiscreteSample<Boolean> lessThanIntegers(DiscreteSample<Integer> left, DiscreteSample<Integer> right) {
        return combine(left, right, (a, b) -> a < b);
    }

    /**
     * Comparison of two float discrete samples
     * @param left Left sample
     * @param right Right sample
     * @return Boolean sample
     */
    public static DiscreteSample<Boolean> lessThanFloats(DiscreteSample<Float> left, DiscreteSample<Float> right) {
        return combine(left, right, (a, b) -> a < b);
    }

    /**
     * Comparison of a discrete float and continuous sample. Calculates the cumulative probabilities and combines them.
     * @param left Left sample
     * @param right Right sample
     * @return Boolean sample
     */
    public static DiscreteSample<Boolean> lessThanContinuous(ContinuousSample left, DiscreteSample<Float> right) {
        DiscreteSample<Boolean> result = new DiscreteSample<Boolean>();

        for (Float a : right.values()) {
            DiscreteSample<Boolean> c = left.cumulative(a);

            for (Boolean b : c.values()) {
                result.add(b, right.get(a) * c.get(b));
            }
        }

        return result;
    }

    /**
     * Comparison of two integer discrete samples
     * @param left Left sample
     * @param right Right sample
     * @return Boolean sample
     */
    public static DiscreteSample<Boolean> greaterThanIntegers(DiscreteSample<Integer> left, DiscreteSample<Integer> right) {
        return combine(left, right, (a, b) -> a > b);
    }

    /**
     * Comparison of two float discrete samples
     * @param left Left sample
     * @param right Right sample
     * @return Boolean sample
     */
    public static DiscreteSample<Boolean> greaterThanFloats(DiscreteSample<Float> left, DiscreteSample<Float> right) {
        return combine(left, right, (a, b) -> a > b);
    }

    /**
     * Interface for a binary operator
     * @param <T> Operator return type
     * @param <U> Left operand type
     * @param <V> Right operand type
     */
    interface Operator<T, U, V> {
        T combine(U a, V b);
    }

    /**
     * Applies a binary operator to two discrete samples. Creates combinations of values from both samples, applies the
     * operator to them and calculates the probability of the operator result.
     * @param left Left operand discrete sample
     * @param right Right operand discrete sample
     * @param operator Binary operator
     * @param <T> Operator return type
     * @param <U> Left operand type
     * @param <V> Right operand type
     * @return Discrete sample with operator results
     */
    private static <T, U, V> DiscreteSample<T> combine(DiscreteSample<U> left, DiscreteSample<V> right, Operator<T, U, V> operator) {
        DiscreteSample<T> result = new DiscreteSample<T>();

        for (U a : left.values()) {
            for (V b : right.values()) {
                result.add(operator.combine(a, b), left.get(a) * right.get(b));
            }
        }

        return result;
    }

}
