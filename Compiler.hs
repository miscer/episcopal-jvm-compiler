module Compiler (compile) where

import Syntax
import Jasmin
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
              | LocalFunction Id [Id] Expression
                deriving (Show)

data Environment = Environment Id [Function]

programEnvironment :: Program -> Environment
programEnvironment (Program name _ queries) = Environment name functions
  where functions = [QueryFunction name (length arguments) | (Query name arguments _) <- queries]

lookupFunction :: Id -> Environment -> Maybe Function
lookupFunction id (Environment _ functions) = find match functions
  where match (QueryFunction name _) = name == id
        match (LocalFunction name _ _) = name == id

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

method :: Id -> Int -> Method
method name arity = Method name arguments (TypeObject "episcopal/runtime/RuntimeValue")
  where arguments = replicate arity (TypeObject "episcopal/runtime/RuntimeValue")

compile :: Program -> [Line]
compile program = concat [classHeader program,
                          mainMethod program,
                          initMethod,
                          queryMethods program,
                          runMethod program]

classHeader :: Program -> [Line]
classHeader (Program name _ _) = [jclass [ClassPublic] name,
                                  jsuper "episcopal/runtime/Program"]

mainMethod :: Program -> [Line]
mainMethod (Program name _ _) = concat [[jmethod [MethodStatic, MethodPublic] (Method "main" [(TypeArray (TypeObject "java/lang/String"))] TypeVoid)],
                                        [jstack 2],
                                        indent [jinstrargs "new" [name],
                                                jinstr "dup",
                                                jinstrargs "invokenonvirtual" [show (Method (name ++ "/<init>") [] TypeVoid)],
                                                jinstrargs "invokenonvirtual" [show (Method (name ++ "/print") [] TypeVoid)],
                                                jinstr "return"],
                                        [jmethodend]]

initMethod :: [Line]
initMethod = concat [[jmethod [MethodPublic] (Method "<init>" [] TypeVoid)],
                     [jstack 1],
                     indent [jinstr "aload_0",
                             jinstrargs "invokenonvirtual" [show (Method "episcopal/runtime/Program/<init>" [] TypeVoid)],
                             jinstr "return"],
                     [jmethodend]]

queryMethods :: Program -> [Line]
queryMethods program@(Program _ _ queries) = concat $ map (queryMethod program) queries

queryMethod :: Program -> Query -> [Line]
queryMethod program (Query name arguments body) = compileMethod methodPrototype methodLocals methodBody
  where methodPrototype = method name (length arguments)
        methodLocals = (length arguments) + 1
        methodBody = exec [compileExpression (head body) (programEnvironment program),
                           instr (jinstr "areturn") (shrinkStack 1)]

runMethod :: Program -> [Line]
runMethod program@(Program _ body _) = compileMethod methodPrototype 1 methodBody
  where methodPrototype = Method "run" [] (TypeObject "episcopal/runtime/RuntimeValue")
        methodBody = exec [compileExpression body (programEnvironment program),
                           instr (jinstr "areturn") (shrinkStack 1)]

compileMethod :: Method -> Int -> Instruction -> [Line]
compileMethod prototype locals instruction = concat [[jmethod [MethodPublic] prototype],
                                                     [jstack (maxStackSize stack)],
                                                     [jlocals locals],
                                                     indent body,
                                                     [jmethodend]]
  where (Output body stack) = instruction emptyOutput

compileExpression :: Expression -> Environment -> Instruction
compileExpression (ExpConst constant) _ = exec [compileConstant constant,
                                                createDiscreteSample]
compileExpression (ExpOp operator left right) env = exec [compileExpression left env,
                                                          compileExpression right env,
                                                          compileOperator operator]
compileExpression (ExpDist distribution) env = compileDistribution distribution env
compileExpression (ExpSample expression) env = exec [compileExpression expression env,
                                                     sampleDistribution]
compileExpression (ExpCall name arguments) env = compileCall name arguments env
compileExpression (ExpLet definitions expression) env = compileLet definitions expression env
compileExpression (ExpObserve sample expression) env = exec [compileExpression sample env,
                                                             compileExpression expression env,
                                                             observeSample]

createDiscreteSample :: Instruction
createDiscreteSample = instr (jinstrargs "invokestatic" [show constant]) id
  where constant = Method "episcopal/runtime/Runtime/constant" [TypeObject "java/lang/Object"] (TypeObject "episcopal/runtime/RuntimeValue")

compileConstant :: Constant -> Instruction

compileConstant (ConstInt n) = exec [instr (jinstrargs "ldc" [show n]) (expandStack 1),
                                     instr (jinstrargs "invokestatic" [show valueOf]) id]
  where valueOf = Method "java/lang/Integer/valueOf" [TypeInt] (TypeObject "java/lang/Integer")

compileConstant (ConstFloat n) = exec [instr (jinstrargs "ldc" [show n]) (expandStack 1),
                                       instr (jinstrargs "invokestatic" [show valueOf]) id]
  where valueOf = Method "java/lang/Float/valueOf" [TypeFloat] (TypeObject "java/lang/Float")

compileConstant (ConstBool n) = instr (jinstrargs "getstatic" [field, show boolean]) (expandStack 1)
  where boolean = TypeObject "java/lang/Boolean"
        true = "java/lang/Boolean/TRUE"
        false = "java/lang/Boolean/FALSE"
        field = if n then true else false

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
  where instruction = jinstrargs "invokestatic" [show (method methodName 2)]
        methodName = "episcopal/runtime/Runtime/" ++ (operatorMethodName operator)

compileDistribution :: Distribution -> Environment -> Instruction
compileDistribution (Bernoulli p) env = exec [compileExpression p env,
                                              instr (jinstrargs "invokestatic" [show (method "episcopal/runtime/Runtime/bernoulli" 1)]) (expandStack 1 . shrinkStack 1)]
compileDistribution (Beta a b) env = exec [compileExpression b env,
                                           compileExpression a env,
                                           instr (jinstrargs "invokestatic" [show (method "episcopal/runtime/Runtime/beta" 2)]) (expandStack 1 . shrinkStack 2)]
compileDistribution (Normal m sd) env = exec [compileExpression sd env,
                                              compileExpression m env,
                                              instr (jinstrargs "invokestatic" [show (method "episcopal/runtime/Runtime/normal" 2)]) (expandStack 1 . shrinkStack 2)]
compileDistribution (Flip p) env = exec [compileExpression p env,
                                         instr (jinstrargs "invokestatic" [show (method "episcopal/runtime/Runtime/flip" 1)]) id]

sampleDistribution :: Instruction
sampleDistribution = instr (jinstrargs "invokestatic" [show (method "episcopal/runtime/Runtime/sample" 1)]) id

compileCall :: Id -> [Expression] -> Environment -> Instruction
compileCall name arguments env = case lookupFunction name env of
                                   (Just function) -> case function of
                                     (QueryFunction _ _) -> compileQueryCall function arguments env
                                     (LocalFunction _ _ _) -> compileLocalCall function arguments env
                                   Nothing -> error $ "Function " ++ name ++ " does not exist"

compileQueryCall :: Function -> [Expression] -> Environment -> Instruction
compileQueryCall function arguments env = exec [operands, object, call]
  where operands = exec [compileExpression argument env | argument <- arguments]
        object = instr (jinstr "aload_0") (expandStack 1)
        call = instr (jinstrargs "invokevirtual" [show (method (program ++ "/" ++ name) arity)]) (expandStack 1 . shrinkStack 1)
        (QueryFunction name arity) = function
        (Environment program _) = env

compileLocalCall :: Function -> [Expression] -> Environment -> Instruction
compileLocalCall function arguments env = compileExpression expression env'
  where env' = Environment program functions'
        functions' = zipWith bind parameters arguments
        bind parameter argument = LocalFunction parameter [] argument
        (LocalFunction _ parameters expression) = function
        (Environment program _) = env

compileLet :: [Definition] -> Expression -> Environment -> Instruction
compileLet definitions expression (Environment program functions) = compileExpression expression env'
  where env' = Environment program functions'
        functions' = defined ++ functions'
        defined = [LocalFunction name arguments (head expressions) | (Definition name arguments expressions) <- definitions]

observeSample :: Instruction
observeSample = instr (jinstrargs "invokestatic" [show (method "episcopal/runtime/Runtime/observe" 2)]) (expandStack 1 . shrinkStack 2)
