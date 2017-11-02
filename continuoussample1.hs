import Syntax
import Compiler

main = putStr $ unlines $ compile program

program = Program "continuoussample1" body []
  where body = ExpOp OpLessThan
    (ExpConst (ConstFloat 5.5))
    (ExpSample
      (ExpDist (Normal
        (ExpConst (ConstFloat 12))
        (ExpConst (ConstFloat 5)))))
