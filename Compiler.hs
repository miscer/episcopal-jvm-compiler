module Compiler (compile) where

import Syntax

type Instruction = String

compile :: Program -> [Instruction]
compile program = concat [classHeader program, mainMethod program, initMethod, runMethod program]

classHeader :: Program -> [Instruction]
classHeader (Program name _ _) = [".class public " ++ name,
                                  ".super episcopal/Program"]

mainMethod :: Program -> [Instruction]
mainMethod (Program name _ _) = [".method static public main([Ljava/lang/String;)V",
                                 "new " ++ name,
                                 "dup",
                                 "invokenonvirtual " ++ name ++ "/<init>()V",
                                 "invokevirtual " ++ name ++ "/print()V",
                                 "return",
                                 ".end method"]

initMethod :: [String]
initMethod = [".method public <init>()V",
              "aload_0",
              "invokenonvirtual episcopal/Program/<init>()V",
              "return",
              ".end method"]

runMethod :: Program -> [Instruction]
runMethod (Program _ body _) = concat [[".method public run()Lepiscopal/Sample;"],
                                       compileExpression body,
                                       ["areturn"],
                                       [".end method"]]

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
