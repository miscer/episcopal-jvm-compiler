import Syntax
import Compiler

main = putStr $ unlines $ compile program

program = Program "operators2" body []
  where body = ExpOp OpPlus (ExpConst (ConstInt 1)) (ExpConst (ConstInt 2))
