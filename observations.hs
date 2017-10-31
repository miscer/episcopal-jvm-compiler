import Syntax
import Compiler

main = putStr $ unlines $ compile program

program = Program "observations" body []
  where body = distr
        distr = ExpLet [Definition "distr" [] [ExpDist (Flip (ExpConst (ConstFloat 0.5)))]] observe
        observe = ExpObserve sample onetwothree
        sample = ExpSample (ExpCall "distr" [])
        onetwothree = ExpConst (ConstInt 123)
