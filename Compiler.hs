module Compiler (compile) where

import Syntax

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

type Instruction = Output -> Output

instr :: Line -> (Stack -> Stack) -> Instruction
instr instruction updateStack (Output instructions stack) = Output instructions' stack'
  where instructions' = instructions ++ [instruction]
        stack' = updateStack stack

exec :: [Instruction] -> Instruction
exec (instruction:[]) = instruction
exec (instruction:next) = (exec next) . instruction

indent :: [Line] -> [Line]
indent is = ["  " ++ i | i <- is]

compile :: Program -> [Line]
compile program = concat [classHeader program,
                          mainMethod program,
                          initMethod,
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

runMethod :: Program -> [Line]
runMethod (Program _ body _) = compileMethod "public run()Lepiscopal/runtime/RuntimeValue;" methodBody
  where methodBody = exec [compileExpression body, instr "areturn" (shrinkStack 1)]

compileMethod :: Prototype -> Instruction -> [Line]
compileMethod prototype instruction = concat [[".method " ++ prototype],
                                              [".limit stack " ++ (show $ maxStackSize stack)],
                                              indent body,
                                              [".end method"]]
  where (Output body stack) = instruction emptyOutput

compileExpression :: Expression -> Instruction
compileExpression (ExpConst constant) = exec [compileConstant constant,
                                              createDiscreteSample]
compileExpression (ExpOp operator left right) = exec [compileExpression right,
                                                      compileExpression left,
                                                      compileOperator operator]
compileExpression (ExpDist distribution) = compileDistribution distribution

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
  where descriptor = "(Lepiscopal/runtime/RuntimeValue;Lepiscopal/runtime/RuntimeValue;)Lepiscopal/runtime/RuntimeValue;"
        method = "episcopal/runtime/Runtime/" ++ (operatorMethodName operator)
        instruction = "invokestatic " ++ method ++ descriptor

compileDistribution :: Distribution -> Instruction
compileDistribution (Bernoulli p) = exec [compileExpression p,
                                          instr "invokestatic episcopal/runtime/Runtime/bernoulli(Lepiscopal/runtime/RuntimeValue;)Lepiscopal/runtime/RuntimeValue;" id]
compileDistribution (Beta a b) = exec [compileExpression b,
                                       compileExpression a,
                                       instr "invokestatic episcopal/runtime/Runtime/beta(Lepiscopal/runtime/RuntimeValue;Lepiscopal/runtime/RuntimeValue;)Lepiscopal/runtime/RuntimeValue;" (expandStack 1 . shrinkStack 2)]
compileDistribution (Normal m sd) = exec [compileExpression sd,
                                          compileExpression m,
                                          instr "invokestatic episcopal/runtime/Runtime/normal(Lepiscopal/runtime/RuntimeValue;Lepiscopal/runtime/RuntimeValue;)Lepiscopal/runtime/RuntimeValue;" (expandStack 1 . shrinkStack 2)]
compileDistribution (Flip p) = exec [compileExpression p,
                                     instr "invokestatic episcopal/runtime/Runtime/flip(Lepiscopal/runtime/RuntimeValue;)Lepiscopal/runtime/RuntimeValue;" id]
