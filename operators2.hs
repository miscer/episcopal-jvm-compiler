import Syntax
import Compiler

main = putStr $ unlines $ compile program

program = Program "operators2" body []
  where body = ExpOp OpPlus (ExpConst (ConstFloat 3)) (ExpSample (ExpDist (Bernoulli (ExpConst (ConstFloat 0.7)))))
