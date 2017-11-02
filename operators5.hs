import Syntax
import Compiler

main = putStr $ unlines $ compile program

program = Program "operators5" body []
  where body = ExpOp OpAnd
                 (ExpConst (ConstBool True))
                 (ExpSample
                   (ExpDist (Flip
                     (ExpConst (ConstFloat 0.3)))))
