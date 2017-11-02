import Syntax
import Compiler

main = putStr $ unlines $ compile program

program = Program "continuoussample2" body []
  where body = ExpOp OpEqual
    (ExpConst (ConstFloat 5.5))
    (ExpSample
      (ExpDist (Normal
        (ExpConst (ConstFloat 12))
        (ExpConst (ConstFloat 5)))))
