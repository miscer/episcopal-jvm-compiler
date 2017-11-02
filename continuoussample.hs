import Syntax
import Compiler

main = putStr $ unlines $ compile program

program = Program "operators3" body []
  where body = ExpOp OpLessThan
    (ExpConst (ConstFloat 5.5))
    (ExpSample
      (ExpDist (Normal
        (ExpConst (ConstFloat 12))
        (ExpConst (ConstFloat 5)))))
