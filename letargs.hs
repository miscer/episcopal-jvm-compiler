import Syntax
import Compiler

main = putStr $ unlines $ compile program

program = Program "letargs" body []
  where body = foo
        foo = ExpLet [Definition "foo" ["x"] [ExpOp OpPlus (ExpCall "x" []) (ExpConst (ConstInt 123))]] bar
        bar = ExpLet [Definition "bar" [] [ExpCall "foo" [(ExpConst (ConstInt 456))]]] result
        result = ExpCall "bar" []
