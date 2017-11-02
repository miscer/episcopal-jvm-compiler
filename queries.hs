import Syntax
import Compiler

main = putStr $ unlines $ compile program

program = Program "queries" body [query1, query2]
  where body = ExpCall "query1" [(ExpConst (ConstInt 123))]
        query1 = Query "query1" ["x"] [(ExpOp OpPlus (ExpCall "x" []) (ExpCall "query2" []))]
        query2 = Query "query2" [] [(ExpConst (ConstInt 345))]
