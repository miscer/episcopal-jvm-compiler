import Syntax
import Compiler

main = putStr $ unlines $ compile program

program = Program "operators4" body []
  where body = ExpOp OpEqual
                 (ExpConst (ConstInt 1))
                 (ExpSample
                   (ExpDist (Bernoulli
                     (ExpConst (ConstFloat 0.3)))))
