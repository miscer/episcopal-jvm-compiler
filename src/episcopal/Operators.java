package episcopal;

import episcopal.continuous.ContinuousSample;
import episcopal.discrete.DiscreteSample;

public class Operators {

    public static DiscreteSample<Boolean> and(DiscreteSample<Boolean> left, DiscreteSample<Boolean> right) {
        return combine(left, right, (a, b) -> a && b);
    }

    public static DiscreteSample<Boolean> or(DiscreteSample<Boolean> left, DiscreteSample<Boolean> right) {
        return combine(left, right, (a, b) -> a || b);
    }

    public static DiscreteSample<Boolean> not(DiscreteSample<Boolean> sample) {
        return new DiscreteSample<Boolean>().add(true, sample.get(false)).add(false, sample.get(true));
    }

    public static DiscreteSample<Integer> addIntegers(DiscreteSample<Integer> left, DiscreteSample<Integer> right) {
        return combine(left, right, (a, b) -> a + b);
    }

    public static DiscreteSample<Float> addFloats(DiscreteSample<Float> left, DiscreteSample<Float> right) {
        return combine(left, right, (a, b) -> a + b);
    }

    public static DiscreteSample<Integer> subtractIntegers(DiscreteSample<Integer> left, DiscreteSample<Integer> right) {
        return combine(left, right, (a, b) -> a - b);
    }

    public static DiscreteSample<Float> subtractFloats(DiscreteSample<Float> left, DiscreteSample<Float> right) {
        return combine(left, right, (a, b) -> a - b);
    }

    public static DiscreteSample<Integer> multiplyIntegers(DiscreteSample<Integer> left, DiscreteSample<Integer> right) {
        return combine(left, right, (a, b) -> a * b);
    }

    public static DiscreteSample<Float> multiplyFloats(DiscreteSample<Float> left, DiscreteSample<Float> right) {
        return combine(left, right, (a, b) -> a * b);
    }

    public static DiscreteSample<Integer> divideIntegers(DiscreteSample<Integer> left, DiscreteSample<Integer> right) {
        return combine(left, right, (a, b) -> a / b);
    }

    public static DiscreteSample<Float> divideFloats(DiscreteSample<Float> left, DiscreteSample<Float> right) {
        return combine(left, right, (a, b) -> a / b);
    }

    public static DiscreteSample<Boolean> equalIntegers(DiscreteSample<Integer> left, DiscreteSample<Integer> right) {
        return combine(left, right, Integer::equals);
    }

    public static DiscreteSample<Boolean> equalFloats(DiscreteSample<Float> left, DiscreteSample<Float> right) {
        return combine(left, right, Float::equals);
    }

    public static DiscreteSample<Boolean> equalBooleans(DiscreteSample<Boolean> left, DiscreteSample<Boolean> right) {
        return combine(left, right, Boolean::equals);
    }

    public static DiscreteSample<Boolean> lessThanIntegers(DiscreteSample<Integer> left, DiscreteSample<Integer> right) {
        return combine(left, right, (a, b) -> a < b);
    }

    public static DiscreteSample<Boolean> lessThanFloats(DiscreteSample<Float> left, DiscreteSample<Float> right) {
        return combine(left, right, (a, b) -> a < b);
    }

    public static DiscreteSample<Boolean> lessThanFloats(ContinuousSample left, DiscreteSample<Float> right) {
        DiscreteSample<Boolean> result = new DiscreteSample<Boolean>();

        for (Float a : right.values()) {
            DiscreteSample<Boolean> c = left.cumulative(a);

            for (Boolean b : c.values()) {
                result.add(b, right.get(a) * c.get(b));
            }
        }

        return result;
    }

    public static DiscreteSample<Boolean> greaterThanIntegers(DiscreteSample<Integer> left, DiscreteSample<Integer> right) {
        return combine(left, right, (a, b) -> a > b);
    }

    public static DiscreteSample<Boolean> greaterThanFloats(DiscreteSample<Float> left, DiscreteSample<Float> right) {
        return combine(left, right, (a, b) -> a > b);
    }

    interface Operator<T, U, V> {
        T combine(U a, V b);
    }

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
