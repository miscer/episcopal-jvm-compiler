import Syntax
import Compiler

main = putStr $ unlines $ compile program

program = Program "operators1" body []
  where body = ExpOp OpLessThan (ExpConst (ConstFloat 0.5)) (ExpConst (ConstFloat 0.75))
