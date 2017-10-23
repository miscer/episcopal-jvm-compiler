module Syntax (
  Id,
  Program (..),
  Query (..),
  Expression (..),
  Constant (..),
  Definition (..),
  Distribution (..),
  Operator (..)
) where

type Id = String

data Program = Program Id Expression [Query]
               deriving Show

data Query = Query Id [Id] [Expression]
             deriving Show

data Expression = ExpConst Constant
                | ExpLet [Definition] Expression
                | ExpObserve Expression Expression
                | ExpSample Expression
                | ExpDist Distribution
                | ExpId Id
                | ExpCall Id [Expression]
                | ExpOp Operator Expression Expression
                  deriving Show

data Constant = ConstInt Int
              | ConstFloat Float
              | ConstPercent Float
              | ConstBool Bool
                deriving Show

data Definition = DefFunc Id [Id] [Expression]
                | DefDist Id [Id] [Expression]
                  deriving Show

data Distribution = Bernoulli Expression
                  | Beta Expression Expression
                  | Normal Expression Expression
                  | Flip Expression
                  | Distribution Id [Expression]
                    deriving Show

data Operator = OpPlus | OpMinus | OpTimes | OpOver | OpOr | OpAnd | OpLessThan | OpGreaterThan | OpEqual
                deriving Show
