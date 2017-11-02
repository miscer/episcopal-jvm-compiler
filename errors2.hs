import Syntax
import Compiler

main = putStr $ unlines $ compile program

program = Program "errors2" body []
  where body = ExpOp OpPlus (ExpConst (ConstInt 1)) (ExpConst (ConstFloat 1))
