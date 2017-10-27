module Compiler (compile) where

import Syntax

type Instruction = String

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

-- Environment

data Environment = Environment { stack :: Stack }
                   deriving (Show)

emptyEnvironment :: Environment
emptyEnvironment = Environment { stack = emptyStack }

-- Compiler

indent :: [Instruction] -> [Instruction]
indent is = ["  " ++ i | i <- is]

compile :: Program -> [Instruction]
compile program = concat [classHeader program,
                          mainMethod program,
                          initMethod,
                          runMethod program]

classHeader :: Program -> [Instruction]
classHeader (Program name _ _) = [".class public " ++ name,
                                  ".super episcopal/Program"]

mainMethod :: Program -> [Instruction]
mainMethod (Program name _ _) = concat [[".method static public main([Ljava/lang/String;)V"],
                                        [".limit stack 2"],
                                        indent ["new " ++ name,
                                                "dup",
                                                "invokenonvirtual " ++ name ++ "/<init>()V",
                                                "invokevirtual " ++ name ++ "/print()V",
                                                "return"],
                                        [".end method"]]

initMethod :: [Instruction]
initMethod = concat [[".method public <init>()V"],
                     [".limit stack 1"],
                     indent ["aload_0",
                             "invokenonvirtual episcopal/Program/<init>()V",
                             "return"],
                     [".end method"]]

compiledMethod :: String -> [Instruction] -> Environment -> [Instruction]
compiledMethod prototype body environment = concat [[".method " ++ prototype],
                                                    [".limit stack " ++ (show $ maxStackSize $ stack environment)],
                                                    indent body,
                                                    [".end method"]]

runMethod :: Program -> [Instruction]
runMethod (Program _ body _) = compiledMethod "public run()Lepiscopal/Sample;" (methodBody ++ ["areturn"]) emptyEnvironment
                                 where methodBody = compileExpression body

createDiscreteSample :: [Instruction]
createDiscreteSample = ["invokestatic episcopal/discrete/DiscreteSample/create(Ljava/lang/Object;)Lepiscopal/discrete/DiscreteSample;"]

compileExpression :: Expression -> [Instruction]
compileExpression (ExpConst constant) = concat [compileConstant constant,
                                                createDiscreteSample]

compileConstant :: Constant -> [Instruction]
compileConstant (ConstInt n) = ["ldc " ++ show n,
                                "invokestatic java/lang/Integer/valueOf(I)Ljava/lang/Integer;"]
compileConstant (ConstFloat n) = ["ldc " ++ show n,
                                  "invokestatic java/lang/Float/valueOf(F)Ljava/lang/Float;"]
compileConstant (ConstBool n) = [if n
                                 then "getstatic java/lang/Boolean/TRUE Ljava/lang/Boolean;"
                                 else "getstatic java/lang/Boolean/FALSE Ljava/lang/Boolean;"]
