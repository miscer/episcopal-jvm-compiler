import Syntax
import Compiler

main = putStr $ unlines $ compile program

program = Program "operators3" body []
  where body = ExpOp OpPlus
                 (ExpConst (ConstInt 3))
                 (ExpSample (ExpDist (Bernoulli (ExpConst (ConstFloat 0.7)))))
