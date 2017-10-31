import Syntax
import Compiler

main = putStr $ unlines $ compile program

program = Program "observations" body []
  where body = distr
        distr = ExpLet [Definition "distr" [] [ExpDist (Flip (ExpConst (ConstFloat 0.5)))]] sample
        sample = ExpLet [Definition "sample" [] [ExpSample (ExpCall "distr" [])]] result
        result = ExpCall "sample" []
