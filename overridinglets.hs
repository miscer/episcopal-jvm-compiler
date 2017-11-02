import Syntax
import Compiler

main = putStr $ unlines $ compile program

program = Program "overridinglets" body []
  where body = one
        one = ExpLet [Definition "number" [] [ExpConst (ConstInt 123)]] two
        two = ExpLet [Definition "number" [] [ExpConst (ConstInt 456)]] result
        result = ExpCall "number" []
