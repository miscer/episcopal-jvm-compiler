package episcopal.runtime;

import episcopal.Operators;
import episcopal.discrete.DiscreteSample;

public class Runtime {

    public static RuntimeValue constant(Object value) throws RuntimeException {
        if (value instanceof Integer) {
            return new RuntimeValue(RuntimeValue.Type.DISCRETE_INT_SAMPLE, DiscreteSample.create(value));
        }

        if (value instanceof Float) {
            return new RuntimeValue(RuntimeValue.Type.DISCRETE_FLOAT_SAMPLE, DiscreteSample.create(value));
        }

        if (value instanceof Boolean) {
            return new RuntimeValue(RuntimeValue.Type.DISCRETE_BOOL_SAMPLE, DiscreteSample.create(value));
        }

        throw new RuntimeException("Incompatible constant");
    }

    public static RuntimeValue add(RuntimeValue left, RuntimeValue right) throws RuntimeException {
        if (isDiscreteIntSamples(left, right)) {
            return new RuntimeValue(
                    RuntimeValue.Type.DISCRETE_INT_SAMPLE,
                    Operators.addIntegers(left.getDiscreteIntSample(), right.getDiscreteIntSample())
            );
        }

        if (isDiscreteFloatSamples(left, right)) {
            return new RuntimeValue(
                    RuntimeValue.Type.DISCRETE_FLOAT_SAMPLE,
                    Operators.addFloats(left.getDiscreteFloatSample(), right.getDiscreteFloatSample())
            );
        }

        throw new RuntimeException("Adding incompatible types");
    }

    public static RuntimeValue subtract(RuntimeValue left, RuntimeValue right) throws RuntimeException {
        if (isDiscreteIntSamples(left, right)) {
            return new RuntimeValue(
                    RuntimeValue.Type.DISCRETE_INT_SAMPLE,
                    Operators.subtractIntegers(left.getDiscreteIntSample(), right.getDiscreteIntSample())
            );
        }

        if (isDiscreteFloatSamples(left, right)) {
            return new RuntimeValue(
                    RuntimeValue.Type.DISCRETE_FLOAT_SAMPLE,
                    Operators.subtractFloats(left.getDiscreteFloatSample(), right.getDiscreteFloatSample())
            );
        }

        throw new RuntimeException("Subtracting incompatible types");
    }

    public static RuntimeValue multiply(RuntimeValue left, RuntimeValue right) throws RuntimeException {
        if (isDiscreteIntSamples(left, right)) {
            return new RuntimeValue(
                    RuntimeValue.Type.DISCRETE_INT_SAMPLE,
                    Operators.multiplyIntegers(left.getDiscreteIntSample(), right.getDiscreteIntSample())
            );
        }

        if (isDiscreteFloatSamples(left, right)) {
            return new RuntimeValue(
                    RuntimeValue.Type.DISCRETE_FLOAT_SAMPLE,
                    Operators.multiplyFloats(left.getDiscreteFloatSample(), right.getDiscreteFloatSample())
            );
        }

        throw new RuntimeException("Multiplying incompatible types");
    }

    public static RuntimeValue divide(RuntimeValue left, RuntimeValue right) throws RuntimeException {
        if (isDiscreteIntSamples(left, right)) {
            return new RuntimeValue(
                    RuntimeValue.Type.DISCRETE_INT_SAMPLE,
                    Operators.divideIntegers(left.getDiscreteIntSample(), right.getDiscreteIntSample())
            );
        }

        if (isDiscreteFloatSamples(left, right)) {
            return new RuntimeValue(
                    RuntimeValue.Type.DISCRETE_FLOAT_SAMPLE,
                    Operators.divideFloats(left.getDiscreteFloatSample(), right.getDiscreteFloatSample())
            );
        }

        throw new RuntimeException("Dividing incompatible types");
    }

    public static RuntimeValue and(RuntimeValue left, RuntimeValue right) throws RuntimeException {
        if (isDiscreteBoolSamples(left, right)) {
            return new RuntimeValue(
                    RuntimeValue.Type.DISCRETE_BOOL_SAMPLE,
                    Operators.and(left.getDiscreteBoolSample(), right.getDiscreteBoolSample())
            );
        }

        throw new RuntimeException("AND-ing incompatible types");
    }

    public static RuntimeValue or(RuntimeValue left, RuntimeValue right) throws RuntimeException {
        if (isDiscreteBoolSamples(left, right)) {
            return new RuntimeValue(
                    RuntimeValue.Type.DISCRETE_BOOL_SAMPLE,
                    Operators.or(left.getDiscreteBoolSample(), right.getDiscreteBoolSample())
            );
        }

        throw new RuntimeException("OR-ing incompatible types");
    }

    public static RuntimeValue equals(RuntimeValue left, RuntimeValue right) throws RuntimeException {
        if (isDiscreteIntSamples(left, right)) {
            return new RuntimeValue(
                    RuntimeValue.Type.DISCRETE_INT_SAMPLE,
                    Operators.equalIntegers(left.getDiscreteIntSample(), right.getDiscreteIntSample())
            );
        }

        if (isDiscreteFloatSamples(left, right)) {
            return new RuntimeValue(
                    RuntimeValue.Type.DISCRETE_FLOAT_SAMPLE,
                    Operators.equalFloats(left.getDiscreteFloatSample(), right.getDiscreteFloatSample())
            );
        }

        if (isDiscreteBoolSamples(left, right)) {
            return new RuntimeValue(
                    RuntimeValue.Type.DISCRETE_BOOL_SAMPLE,
                    Operators.equalBooleans(left.getDiscreteBoolSample(), right.getDiscreteBoolSample())
            );
        }

        throw new RuntimeException("Comparing incompatible types");
    }

    public static RuntimeValue lessThan(RuntimeValue left, RuntimeValue right) throws RuntimeException {
        if (isDiscreteIntSamples(left, right)) {
            return new RuntimeValue(
                    RuntimeValue.Type.DISCRETE_INT_SAMPLE,
                    Operators.lessThanIntegers(left.getDiscreteIntSample(), right.getDiscreteIntSample())
            );
        }

        if (isDiscreteFloatSamples(left, right)) {
            return new RuntimeValue(
                    RuntimeValue.Type.DISCRETE_FLOAT_SAMPLE,
                    Operators.lessThanFloats(left.getDiscreteFloatSample(), right.getDiscreteFloatSample())
            );
        }

        throw new RuntimeException("Comparing incompatible types");
    }

    public static RuntimeValue greaterThan(RuntimeValue left, RuntimeValue right) throws RuntimeException {
        if (isDiscreteIntSamples(left, right)) {
            return new RuntimeValue(
                    RuntimeValue.Type.DISCRETE_INT_SAMPLE,
                    Operators.greaterThanIntegers(left.getDiscreteIntSample(), right.getDiscreteIntSample())
            );
        }

        if (isDiscreteFloatSamples(left, right)) {
            return new RuntimeValue(
                    RuntimeValue.Type.DISCRETE_FLOAT_SAMPLE,
                    Operators.greaterThanFloats(left.getDiscreteFloatSample(), right.getDiscreteFloatSample())
            );
        }

        throw new RuntimeException("Comparing incompatible types");
    }

    private static boolean isDiscreteIntSamples(RuntimeValue left, RuntimeValue right) {
        return left.getType() == RuntimeValue.Type.DISCRETE_INT_SAMPLE && right.getType() == RuntimeValue.Type.DISCRETE_INT_SAMPLE;
    }

    private static boolean isDiscreteFloatSamples(RuntimeValue left, RuntimeValue right) {
        return left.getType() == RuntimeValue.Type.DISCRETE_FLOAT_SAMPLE && right.getType() == RuntimeValue.Type.DISCRETE_FLOAT_SAMPLE;
    }

    private static boolean isDiscreteBoolSamples(RuntimeValue left, RuntimeValue right) {
        return left.getType() == RuntimeValue.Type.DISCRETE_BOOL_SAMPLE && right.getType() == RuntimeValue.Type.DISCRETE_BOOL_SAMPLE;
    }

}
