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

-- | Identifier
type Id = String

-- | Episcopal Program
data Program
  -- | Program with a name, program expression and a number of queries
  = Program Id Expression [Query]
    deriving Show

-- | Program query
data Query
  -- | Query with a name, parameter names and the query expression
  = Query Id [Id] [Expression]
             deriving Show

-- | Expression
data Expression
  -- | Constant
  = ExpConst Constant
  -- | Local definitions scoped to the expression
  | ExpLet [Definition] Expression
  -- | Observation
  -- | The first expression should be a sample
  | ExpObserve Expression Expression
  -- | Sample of a distribution
  | ExpSample Expression
  -- | Distribution
  | ExpDist Distribution
  -- | Function call for the specified function with the specified arguments
  -- | This is also used to call queries and access local variables
  | ExpCall Id [Expression]
  -- | Binary operator with the specified expressions on the left and right hand side
  | ExpOp Operator Expression Expression
    deriving Show

-- | Constant
data Constant
  -- | Integer constant
  = ConstInt Int
  -- | Floating point Constant
  -- | Also used to store percentages (i.e. probability)
  | ConstFloat Float
  -- | Boolean constant
  | ConstBool Bool
    deriving Show

-- | Definition
data Definition
  -- | Local variable/function definition with the specified name, list of parameters and the value expression
  = Definition Id [Id] [Expression]
    deriving Show

-- | Distribution definition
data Distribution
  -- | Bernoulli distribution with the specified probability (p)
  = Bernoulli Expression
  -- | Beta distribution with pseudocounts a and b
  | Beta Expression Expression
  -- | Normal distribution with mean (m) and standard deviation (sd)
  | Normal Expression Expression
  -- | Flip distribution with the specified probability (p)
  | Flip Expression
    deriving Show

-- | Binary operators
data Operator = OpPlus | OpMinus | OpTimes | OpOver | OpOr | OpAnd | OpLessThan | OpGreaterThan | OpEqual
                deriving Show
