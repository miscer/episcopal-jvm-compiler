import Syntax
import Compiler

main = putStr $ unlines $ compile program

program = Program "continuoussample2" body []
  where body = ExpOp OpEqual
                 (ExpSample
                   (ExpDist (Normal
                     (ExpConst (ConstFloat 100))
                     (ExpConst (ConstFloat 15)))))
                 (ExpConst (ConstFloat 100))
