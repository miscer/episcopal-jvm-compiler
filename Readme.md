# Runtime library

Implemented in Java, requires Commons Math library. The runtime.jar archive
already contains the dependency, so it is ready to be included in the classpath.

# Testing programs

Testing programs need to be compiled with GHC and then run. The output is Jasmin
bytecode, which needs to be compiled into JVM bytecode.

For example, to compile and run the discrete sample program:

    > ghc discretesample.hs
    > ./discretesample > discretesample.j
    > jasmin discretesample.j
    > java -cp runtime.jar:. discretesample

To compile the programs, the `Syntax.hs`, `Jasmin.hs` and `Compiler.hs` files
need to be in the same directory as the program source.
