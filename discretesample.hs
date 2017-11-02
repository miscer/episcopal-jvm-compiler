import Syntax
import Compiler

main = putStr $ unlines $ compile program

program = Program "discretesample" body []
  where body = ExpSample (ExpDist (Flip (ExpConst (ConstFloat 0.3))))
