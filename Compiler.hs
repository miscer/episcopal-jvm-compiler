module Compiler (compile) where

import Syntax
import Data.List

-- Stack

data Stack = Stack { currentStackSize :: Int, maxStackSize :: Int }
             deriving (Show)

emptyStack :: Stack
emptyStack = Stack { currentStackSize = 0, maxStackSize = 0 }

expandStack :: Int -> Stack -> Stack
expandStack n (Stack {currentStackSize = c, maxStackSize = m}) =
  Stack { currentStackSize = c + n, maxStackSize = max (c + n) m }

shrinkStack :: Int -> Stack -> Stack
shrinkStack n (Stack {currentStackSize = c, maxStackSize = m})
  | c >= n = Stack { currentStackSize = c - n, maxStackSize = m }
  | otherwise = error "Shrinking stack beyond its size"

-- Compiler

type Line = String
type Prototype = String

data Output = Output [Line] Stack
              deriving (Show)

emptyOutput :: Output
emptyOutput = Output [] emptyStack

data Function = QueryFunction Id Int
                deriving (Show)

data Environment = Environment Id [Function]

programEnvironment :: Program -> Environment
programEnvironment (Program name _ queries) = Environment name functions
  where functions = [QueryFunction name (length arguments) | (Query name arguments _) <- queries]

lookupFunction :: Id -> Environment -> Maybe Function
lookupFunction id (Environment _ functions) = find match functions
  where match (QueryFunction name _) = name == id

type Instruction = Output -> Output

instr :: Line -> (Stack -> Stack) -> Instruction
instr instruction updateStack (Output instructions stack) = Output instructions' stack'
  where instructions' = instructions ++ [instruction]
        stack' = updateStack stack

exec :: [Instruction] -> Instruction
exec (instruction:[]) = instruction
exec (instruction:next) = (exec next) . instruction
exec [] = id

indent :: [Line] -> [Line]
indent is = ["  " ++ i | i <- is]

method :: Id -> Int -> String
method name arity = concat [name, "(", arguments, ")Lepiscopal/runtime/RuntimeValue;"]
  where arguments = concat (replicate arity "Lepiscopal/runtime/RuntimeValue;")

compile :: Program -> [Line]
compile program = concat [classHeader program,
                          mainMethod program,
                          initMethod,
                          queryMethods program,
                          runMethod program]

classHeader :: Program -> [Line]
classHeader (Program name _ _) = [".class public " ++ name,
                                  ".super episcopal/runtime/Program"]

mainMethod :: Program -> [Line]
mainMethod (Program name _ _) = concat [[".method static public main([Ljava/lang/String;)V"],
                                        [".limit stack 2"],
                                        indent ["new " ++ name,
                                                "dup",
                                                "invokenonvirtual " ++ name ++ "/<init>()V",
                                                "invokevirtual " ++ name ++ "/print()V",
                                                "return"],
                                        [".end method"]]

initMethod :: [Line]
initMethod = concat [[".method public <init>()V"],
                     [".limit stack 1"],
                     indent ["aload_0",
                             "invokenonvirtual episcopal/runtime/Program/<init>()V",
                             "return"],
                     [".end method"]]

queryMethods :: Program -> [Line]
queryMethods program@(Program _ _ queries) = concat $ map (queryMethod program) queries

queryMethod :: Program -> Query -> [Line]
queryMethod program (Query name arguments body) = compileMethod methodPrototype methodBody
  where methodPrototype = "public " ++ (method name (length arguments))
        methodBody = exec [compileExpression (head body) (programEnvironment program),
                           instr "areturn" (shrinkStack 1)]

runMethod :: Program -> [Line]
runMethod program@(Program _ body _) = compileMethod "public run()Lepiscopal/runtime/RuntimeValue;" methodBody
  where methodBody = exec [compileExpression body (programEnvironment program),
                           instr "areturn" (shrinkStack 1)]

compileMethod :: Prototype -> Instruction -> [Line]
compileMethod prototype instruction = concat [[".method " ++ prototype],
                                              [".limit stack " ++ (show $ maxStackSize stack)],
                                              indent body,
                                              [".end method"]]
  where (Output body stack) = instruction emptyOutput

compileExpression :: Expression -> Environment -> Instruction
compileExpression (ExpConst constant) _ = exec [compileConstant constant,
                                                createDiscreteSample]
compileExpression (ExpOp operator left right) env = exec [compileExpression right env,
                                                          compileExpression left env,
                                                          compileOperator operator]
compileExpression (ExpDist distribution) env = compileDistribution distribution env
compileExpression (ExpSample expression) env = exec [compileExpression expression env,
                                                     sampleDistribution]
compileExpression (ExpCall name arguments) env = compileCall name arguments env

createDiscreteSample :: Instruction
createDiscreteSample = instr "invokestatic episcopal/runtime/Runtime/constant(Ljava/lang/Object;)Lepiscopal/runtime/RuntimeValue;" id

compileConstant :: Constant -> Instruction
compileConstant (ConstInt n) = exec [instr ("ldc " ++ show n) (expandStack 1),
                                     instr "invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;" id]
compileConstant (ConstFloat n) = exec [instr ("ldc " ++ show n) (expandStack 1),
                                       instr "invokestatic java/lang/Float/valueOf(F)Ljava/lang/Float;" id]
compileConstant (ConstBool n) = if n
                                  then instr "getstatic java/lang/Boolean/TRUE Ljava/lang/Boolean;" (expandStack 1)
                                  else instr "getstatic java/lang/Boolean/FALSE Ljava/lang/Boolean;" (expandStack 1)

operatorMethodName :: Operator -> String
operatorMethodName OpPlus = "add"
operatorMethodName OpMinus = "subtract"
operatorMethodName OpTimes = "multiply"
operatorMethodName OpOver = "divide"
operatorMethodName OpOr = "or"
operatorMethodName OpAnd = "and"
operatorMethodName OpLessThan = "lessThan"
operatorMethodName OpGreaterThan = "greaterThan"
operatorMethodName OpEqual = "equal"

compileOperator :: Operator -> Instruction
compileOperator operator = instr instruction (expandStack 1 . shrinkStack 2)
  where instruction = "invokestatic " ++ (method methodName 2)
        methodName = "episcopal/runtime/Runtime/" ++ (operatorMethodName operator)

compileDistribution :: Distribution -> Environment -> Instruction
compileDistribution (Bernoulli p) env = exec [compileExpression p env,
                                              instr ("invokestatic " ++ (method "episcopal/runtime/Runtime/bernoulli" 1)) (expandStack 1 . shrinkStack 1)]
compileDistribution (Beta a b) env = exec [compileExpression b env,
                                           compileExpression a env,
                                           instr ("invokestatic " ++ (method "episcopal/runtime/Runtime/beta" 2)) (expandStack 1 . shrinkStack 2)]
compileDistribution (Normal m sd) env = exec [compileExpression sd env,
                                              compileExpression m env,
                                              instr ("invokestatic " ++ (method "episcopal/runtime/Runtime/normal" 2)) (expandStack 1 . shrinkStack 2)]
compileDistribution (Flip p) env = exec [compileExpression p env,
                                         instr ("invokestatic " ++ (method "episcopal/runtime/Runtime/flip" 1)) id]

sampleDistribution :: Instruction
sampleDistribution = instr ("invokestatic " ++ (method "episcopal/runtime/Runtime/sample" 1)) id

compileCall :: Id -> [Expression] -> Environment -> Instruction
compileCall name arguments env@(Environment program _) = case lookupFunction name env of
                                                           (Just function) -> exec [operands, compile function]
                                                           Nothing -> error ("Function " ++ name ++ " does not exist")
  where compile (QueryFunction _ arity) = instr ("invokevirtual " ++ (method (program ++ "/" ++ name) arity)) id
        operands = exec [exec $ reverse [compileExpression argument env | argument <- arguments],
                         instr "aload_0" (expandStack 1)]
