---
title: CS4201 Practical 1
author: 140015533
date: 3 November 2017
toc: true
---

# Design

## Distributions and samples

Episcopal supports two types of distributions: discrete and continuous.

The Flip and Bernoulli distributions are discrete. This means that they contain a finite set of values with their corresponding probabilities, for example:

- 60% 100
- 20% 200
- 10% 300
- 10% 400

When a discrete distribution is sampled, these values are captured in a _discrete sample_. This contains all values of the distribution with their probabilities. We can query the sample for the probability of a specific value, and for all values in the sample.

The Flip distribution with probability $p$ generates a sample containing `true` with probability $p$ and `false` with probability $1 - p$. Similarly the Bernoulli distribution contains values 0 and 1.

The other two distributions, Normal and Beta are continuous distributions. They do not contain any specific values, and the probability of a specific value is 0%. When these distributions are sampled, a _continuous sample_ is created. It does not contain any specific values, we can however query for the _cumulative probability_ of a certain value, i.e. the probability of a value occurring that less than or equal to the value.

A cumulative sample contains two boolean values - `true` with the cumulative probability $p$, and `false` with probability $1 - p$.

## Constants

Every constant in the program is turned into a discrete sample that contains the constant with 100% probability. This allows us to handle constants the same way as samples from distributions, simplifying the design and leading to some interesting properties of operators.

## Operators

My implementation supports all the required operators. Some of them have a special meaning in some contexts.

The arithmetic (plus, minus, times, over) and logic (and, or) operators work on constants as expected, e.g. `1 + 2 = 3`. Internally, the 1 and 2 in the expression are stored as two discrete samples, as described above.

The operators take values from both samples, calculate the probability of each combination of values occurring, and create a new sample containing the results of the operators. Let's take two samples for example:

- 10 (40%), 20 (60%)
- 30 (30%), 40 (70%)

When we add them, we calculate the probability of each combination of values:

- 10 + 30 ($40% \times 30%$)
- 10 + 40 ($40% \times 70%$)
- 20 + 30 ($60% \times 30%$)
- 20 + 40 ($60% \times 70%$)

And the resulting sample is:

- 40 (12%), 50 (46%), 60 (42%)

Arithmetic operators can be used only on values of the same type, i.e. integers with integers, and floats with floats.

We can also use arithmetic operators on discrete and continuous samples. The limitation is that the discrete sample can have only one value. This is used to shift and scale the continuous sample. For example, if we have a continuous sample that for value 10 gives cumulative probability 30%, and we add 3 to the sample and multiply it by 2, it will now give the same probability for the value 23 ($10 \times 2 + 3$).

The comparison operators (equals, less than, greater than) can be used with discrete samples the same way as arithmetic and logic operators. The result is a discrete sample containing boolean values.

We can also use these operators with continuous samples. This is where the cumulative probability comes into play - if we compare a continuous sample with a discrete sample, it will calculate the cumulative probability for each value in the discrete sample. For example, if we have two samples:

- Sample $A$ of normal distribution with $m = 100$ and $sd = 15$
- Discrete sample $B$ with values 70 (30%), and 130 (70%)

The result of $A < B$ will be the combination of cumulative probabilities for 70 and 130 with the correct probabilities calculated.

The cumulative probability of 70 is 2.2%, the probability of a 70 occurring is 30%, cumulative probability of 130 is 97.7% and the probability of it occurring is 70%. Adding that up we get the discrete sample

- true ($2.2 * 30 + 97.7 * 70 = 69%$), false ($97.8 * 30 + 2.3 * 70 = 31%$)

The greater than operator is simply a negation of the less than operator when comparing continuous samples. The equals operator will always return false with 100% probability, as the probability of a specific value in a continuous distribution is 0%.

## Observations

Only a sample can be observed, and only a distribution can be sampled. This is because a sample represents all values in the distribution, even if it is continuous.

Observing a sample should choose one value from it, but since there are multiple values in the sample, I decided to choose one value from the sample randomly. The probabilities in the sample are taken into consideration - e.g. if one value has 90% probability, it will be chosen randomly 90% of the time.

I understood the observe expression as a form of an if statement: If the observation is true, execute the observation expression. This means that the observed sample has to be a discrete sample of boolean values - any other value will throw a runtime error.

When a discrete boolean sample is observed, either true or false is chosen from it. If true is chosen, the expression is executed. If false is chosen, a runtime error is thrown and the program is invalid. For example, this program will return 123 with 60% probability, otherwise it will be invalid:

```
episcopal obs = observe (sample (Flip 60%)) = True in 123
```

We can also use continuous distributions:

```
episcopal obs = observe (sample (Normal 10 5)) < 10 in 456
```

This will result in 456 with 50% probability, since the cumulative probability of 10 in the distribution is 50%.

## Abstract syntax

Identifiers and function calls were merged into one expression type, function call.

The bracketing expression is not included in the abstract tree, as it can be handled by the parser alone.

Function and distribution definition were merged into one, since every function already returns a sample. New distributions can be defined as functions that return a sample, instead of creating a new distribution and sampling it. However, if there was any other operation that can be done with a distribution (other than sampling), it would make sense to have a separate distribution definition type.

The probability constant does not exist in the abstract syntax, as it is essentially the same as a floating point constant. For example 95% is equal to 0.95, and this conversion can be handled by the parser.

# Implementation

## Standard library

The logic of the language described above is implemented in Java. There are classes for the four distributions, discrete and continuous samples, operators and observations.

These classes are not tied to the compiler and can by used by any other application.

The standard library needs to be compiled to JVM bytecode and made available to the compiled Episcopal program.

## Runtime

The compiled programs do not interact with the standard library directly. This is because it is simply not possible. For example, the add method in the `Operators` class in the standard library takes two distributions as arguments and returns another distribution. There are separate methods for adding integers and floats:

```
public static DiscreteSample<Integer> addIntegers(DiscreteSample<Integer> left, DiscreteSample<Integer> right);
public static DiscreteSample<Float> addFloats(DiscreteSample<Float> left, DiscreteSample<Float> right);
```

However when compiling the episcopal program, the compiler only sees that the operator is applied to two expressions:

```
ExpOp OpAdd expr1 expr2
```

These two expression can contain anything, for example a distribution and a local definition. It cannot know whether to use `addIntegers` or `addFloats`. We could use the `instaceof` operator to try and find out the type of the expression at runtime, but this does not work with discrete samples, which use generics, as the exact type is removed during compilation from Java.

Therefore we need a layer between the compiled bytecode and the standard library. This exists in the `Runtime` and `RuntimeValue` classes.

`RuntimeValue` wraps all values in the program: discrete and continuous samples and distributions. The type of the value is stored in a field and can be accessed by other classes.

`Runtime` then works with `RuntimeValue` instances by providing methods for various episcopal commands, such as sampling or comparing two values. These methods take runtime values as arguments and return another runtime value.

This way the bytecode generated by the compiler doesn't deal with types of values - it simply translates expressions into calls to `Runtime` methods.

The `Runtime` class then executes methods in the standard library based on the types of runtime values it receives. If the two types do not make sense (e.g. adding a constant and a distribution) it throws a runtime error.

The generated bytecode is then kept clean and readable by containing mostly calls to methods of the `Runtime` class.

# Testing

## Compiler

I wrote a number of Haskell programs that construct an AST and pass it onto the compiler, printing out the result. When these programs are run and their output is compiled with Jasmin, they can be executed to check that the result is correct.

## Standard library

The classes in the standard library have unit tests defined for their methods. I am using JUnit 4. These tests check that the library is behaving correctly in various cases.

For example, the `DiscreteSample` class is checked to make sure it calculates probabilities correctly, the `Operators` class that it combines discrete samples or that observations return a single value from a sample.

# Extensions

As an extension I have implemented a simple Haskell library for generated Jasmin bytecode. The library contains functions for constructing Jasmin directives such as `.class`, `.field` or `.method` and for instructions inside methods.

This library helps developers avoid mistakes by encoding the bytecode as expressions instead of strings. For example, to generate a `.method` directive we can use a single string:

```
.method public static replicate(ILjava/lang/String;)Ljava/lang/String;
```

However, this form is hard to read and prone to errors. The equivalent Haskell expression is

```
jmethod [MethodPublic, MethodStatic]
  (Method "replicate"
    [TypeInt, TypeObject "java/lang/string"]
    TypeObject "java/lang/String")
```

The library is in `Jasmin.hs` and also contains an example program.
