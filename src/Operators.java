public class Operators {

    public static Result<Boolean> and(Result<Boolean> left, Result<Boolean> right) {
        return combine(left, right, (a, b) -> a && b);
    }

    public static Result<Boolean> or(Result<Boolean> left, Result<Boolean> right) {
        return combine(left, right, (a, b) -> a || b);
    }

    public static Result<Integer> addIntegers(Result<Integer> left, Result<Integer> right) {
        return combine(left, right, (a, b) -> a + b);
    }

    public static Result<Float> addFloats(Result<Float> left, Result<Float> right) {
        return combine(left, right, (a, b) -> a + b);
    }

    public static Result<Integer> subtractIntegers(Result<Integer> left, Result<Integer> right) {
        return combine(left, right, (a, b) -> a - b);
    }

    public static Result<Float> subtractFloats(Result<Float> left, Result<Float> right) {
        return combine(left, right, (a, b) -> a - b);
    }

    public static Result<Integer> multiplyIntegers(Result<Integer> left, Result<Integer> right) {
        return combine(left, right, (a, b) -> a * b);
    }

    public static Result<Float> multiplyFloats(Result<Float> left, Result<Float> right) {
        return combine(left, right, (a, b) -> a * b);
    }

    public static Result<Integer> divideIntegers(Result<Integer> left, Result<Integer> right) {
        return combine(left, right, (a, b) -> a / b);
    }

    public static Result<Float> divideFloats(Result<Float> left, Result<Float> right) {
        return combine(left, right, (a, b) -> a / b);
    }

    public static Result<Boolean> equalIntegers(Result<Integer> left, Result<Integer> right) {
        return combine(left, right, Integer::equals);
    }

    public static Result<Boolean> equalFloats(Result<Float> left, Result<Float> right) {
        return combine(left, right, Float::equals);
    }

    public static Result<Boolean> equalBooleans(Result<Boolean> left, Result<Boolean> right) {
        return combine(left, right, Boolean::equals);
    }

    public static Result<Boolean> lessThanIntegers(Result<Integer> left, Result<Integer> right) {
        return combine(left, right, (a, b) -> a < b);
    }

    public static Result<Boolean> lessThanFloats(Result<Float> left, Result<Float> right) {
        return combine(left, right, (a, b) -> a < b);
    }

    public static Result<Boolean> greaterThanIntegers(Result<Integer> left, Result<Integer> right) {
        return combine(left, right, (a, b) -> a > b);
    }

    public static Result<Boolean> greaterThanFloats(Result<Float> left, Result<Float> right) {
        return combine(left, right, (a, b) -> a > b);
    }

    interface Operator<T, U, V> {
        T combine(U a, V b);
    }

    private static <T, U, V> Result<T> combine(Result<U> left, Result<V> right, Operator<T, U, V> operator) {
        Result<T> result = new Result<>();

        for (U a : left.values()) {
            for (V b : right.values()) {
                result.add(operator.combine(a, b), left.get(a) * right.get(b));
            }
        }

        return result;
    }

}
