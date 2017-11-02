import Syntax
import Compiler

main = putStr $ unlines $ compile program

program = Program "errors1" body []
  where body = ExpSample (ExpConst (ConstInt 1))
