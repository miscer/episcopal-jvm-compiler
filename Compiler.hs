module Compiler (compile) where

import Syntax
import Jasmin
import Data.List

-- | JVM method stack
-- | Used to keep track of the stack size needed for the method. The current
-- | stack size is expanded and shrunk as the method is compiled, and the
-- | maximum stack size is stored.
data Stack = Stack { currentStackSize :: Int, maxStackSize :: Int }
             deriving (Show)

-- | Creates an empty stack
emptyStack :: Stack
emptyStack = Stack { currentStackSize = 0, maxStackSize = 0 }

-- | Expands the stack by the specified amount
expandStack :: Int -- | Number of operands added to the stack
            -> Stack -- | Current stack
            -> Stack -- | Updated stack
expandStack n (Stack {currentStackSize = c, maxStackSize = m}) =
  Stack { currentStackSize = c + n, maxStackSize = max (c + n) m }

-- | Expands the stack by the specified amount
shrinkStack :: Int -- | Number of operands removed from the stack
            -> Stack -- | Current stack
            -> Stack -- | Updated stack
shrinkStack n (Stack {currentStackSize = c, maxStackSize = m})
  | c >= n = Stack { currentStackSize = c - n, maxStackSize = m }
  | otherwise = error "Shrinking stack beyond its size"

-- | Program output line
type Line = String

-- | Compiler output for one method in the class
-- | Contains the instructions in the method and the stack state at the end of
-- | the method. The stack size limit is the maximum stack size.
data Output = Output [Line] Stack
              deriving (Show)

-- | Creates empty output, with no instructions and empty stack
emptyOutput :: Output
emptyOutput = Output [] emptyStack

-- | Episcopal function
data Function
  -- | Function defined as a query, with the name of the query and the number
  -- | of its parameters. This will get translated into a method in the compiled
  -- | class
  = QueryFunction Id Int
  -- | Locally defined function (using the let expression) with the name, list
  -- | of parameters, the expression and the environment of the let expression
  -- | that defined it. The expression is copied when the local function is called.
  | LocalFunction Id [Id] Expression Environment
    deriving (Show)

-- | Expression environment, containing the program name and the list of
-- | functions defined in the expression's scope
data Environment = Environment Id [Function] deriving (Show)

-- | Creates the starting environment for program methods. This contains the
-- | Program name and a list of all query functions
programEnvironment :: Program -> Environment
programEnvironment (Program name _ queries) = Environment name functions
  where functions = [QueryFunction name (length arguments) | (Query name arguments _) <- queries]

-- | Tries to find a function with the specified name in the environment
lookupFunction :: Id -> Environment -> Maybe Function
lookupFunction id (Environment _ functions) = find match functions
  where match (QueryFunction name _) = name == id
        match (LocalFunction name _ _ _) = name == id

-- | Instruction is a function that modifies an output. It usually adds an
-- | instruction line and modifies the stack
type Instruction = Output -> Output

-- | Single instruction
instr :: Line -- | String containing the whole instruction line
      -> (Stack -> Stack) -- | Stack change function, this can expand or shrink the stack
      -> Instruction -- | Function that adds the instruction to the output and modifies the stack
instr instruction updateStack (Output instructions stack) = Output instructions' stack'
  where instructions' = instructions ++ [instruction]
        stack' = updateStack stack

-- | Combination of multiple instructions
-- | Takes a list of instructions and applies them in sequence.
exec :: [Instruction] -> Instruction
exec (instruction:[]) = instruction
exec (instruction:next) = (exec next) . instruction
exec [] = id

-- | Indents a list of program lines by two spaces
-- | Used for method instructions to make the program output more readable
indent :: [Line] -> [Line]
indent is = ["  " ++ i | i <- is]

-- | Helper function for constructing a method that only takes episcopal runtime
-- | values and returns a value as well.
method :: Id -- | Name of the method
       -> Int -- | Number of parameters (arity)
       -> Method -- | Episcopal method
method name arity = Method name arguments (TypeObject "episcopal/runtime/RuntimeValue")
  where arguments = replicate arity (TypeObject "episcopal/runtime/RuntimeValue")

-- | Compiles an episcopal program into Jasmin bytecode
-- | The output is a list of lines of the compiled bytecode
compile :: Program -> [Line]
compile program = concat [classHeader program,
                          mainMethod program,
                          initMethod,
                          queryMethods program,
                          runMethod program]

-- | Generates a Jasmin class header for the specified program
-- | The class will have the same name as the program and it will contain all
-- | code of the program
classHeader :: Program -> [Line]
classHeader (Program name _ _) = [jclass [ClassPublic] name,
                                  jsuper "episcopal/runtime/Program"]

-- | Generates the main method of the class
-- | This method is called when the program is run. It simply creates a new
-- | instance of the program class, runs the program and prints out the result.
mainMethod :: Program -> [Line]
mainMethod (Program name _ _) = concat [[jmethod [MethodStatic, MethodPublic] (Method "main" [(TypeArray (TypeObject "java/lang/String"))] TypeVoid)],
                                        [jstack 2],
                                        indent [jinstrargs "new" [name],
                                                jinstr "dup",
                                                jinstrargs "invokenonvirtual" [show (Method (name ++ "/<init>") [] TypeVoid)],
                                                jinstrargs "invokenonvirtual" [show (Method (name ++ "/print") [] TypeVoid)],
                                                jinstr "return"],
                                        [jmethodend]]

-- | Generates the init method for the class
-- | This method simply calls the parent class init method.
initMethod :: [Line]
initMethod = concat [[jmethod [MethodPublic] (Method "<init>" [] TypeVoid)],
                     [jstack 1],
                     indent [jinstr "aload_0",
                             jinstrargs "invokenonvirtual" [show (Method "episcopal/runtime/Program/<init>" [] TypeVoid)],
                             jinstr "return"],
                     [jmethodend]]

-- | Generates methods for all queries in the program
queryMethods :: Program -> [Line]
queryMethods program@(Program _ _ queries) = concat $ map (queryMethod program) queries

-- | Generates a method for the program query
-- | The method evaluates the query's expression and returns the resulting value
queryMethod :: Program -> Query -> [Line]
queryMethod program (Query name parameters body) = compileMethod methodPrototype methodLocals methodBody
  where methodPrototype = method name (length parameters) -- method prototype with the query's name and number of parameters
        methodLocals = (length parameters) + 1 -- we need locals for each argument, plus one for this
        methodBody = exec [compileExpression (head body) (programEnvironment program), -- compile the query's expression using the program scope
                           instr (jinstr "areturn") (shrinkStack 1)] -- return the resulting value

-- | Generates the run method
-- | This method executes the program's expression and returns its value
runMethod :: Program -> [Line]
runMethod program@(Program _ body _) = compileMethod methodPrototype 1 methodBody
  where methodPrototype = Method "run" [] (TypeObject "episcopal/runtime/RuntimeValue") -- method called "run" with no parameters, returning a value
        methodBody = exec [compileExpression body (programEnvironment program), -- compile the program expression using the program scope
                           instr (jinstr "areturn") (shrinkStack 1)] -- return the resulting value

-- | Generates the Jasmin directives defining the specified method
compileMethod :: Method -- | Method descriptor (includes name, parameters and return type)
              -> Int -- | Number of local variables needed for the method
              -> Instruction -- | Function that generates the body of the method and the stack
              -> [Line] -- | Bytecode for the method
compileMethod prototype locals instruction = concat [[jmethod [MethodPublic] prototype], -- public method with the specified params
                                                     [jstack (maxStackSize stack)], -- stack size is the maximum stack size used by the instructions
                                                     [jlocals locals], -- number of local variables
                                                     indent body, -- method body
                                                     [jmethodend]]
  where (Output body stack) = instruction emptyOutput -- apply the instruction to empty output, returning method instructions and stack

-- | Generates Jasmin instructions for the specified expression in the specified environment
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

-- | Creates a discrete sample from a constant
-- | The constant should be on the stack and an instance of Integer, Float or Boolean
createDiscreteSample :: Instruction
createDiscreteSample = instr (jinstrargs "invokestatic" [show constant]) id -- call Runtime.constant
  where constant = Method "episcopal/runtime/Runtime/constant" [TypeObject "java/lang/Object"] (TypeObject "episcopal/runtime/RuntimeValue")

-- | Compiles a constant
-- | For each constant type creates a corresponding instance of Integer, Float or Boolean
compileConstant :: Constant -> Instruction

compileConstant (ConstInt n) = exec [instr (jinstrargs "ldc" [show n]) (expandStack 1), -- Load the integer onto the stack
                                     instr (jinstrargs "invokestatic" [show valueOf]) id] -- Create an Integer instance
  where valueOf = Method "java/lang/Integer/valueOf" [TypeInt] (TypeObject "java/lang/Integer")

compileConstant (ConstFloat n) = exec [instr (jinstrargs "ldc" [show n]) (expandStack 1), -- Load the float onto the stack
                                       instr (jinstrargs "invokestatic" [show valueOf]) id] -- Create a Float instance
  where valueOf = Method "java/lang/Float/valueOf" [TypeFloat] (TypeObject "java/lang/Float")

compileConstant (ConstBool n) = instr (jinstrargs "getstatic" [field, show boolean]) (expandStack 1) -- Get either Boolean.TRUE or Boolean.FALSE
  where boolean = TypeObject "java/lang/Boolean"
        true = "java/lang/Boolean/TRUE"
        false = "java/lang/Boolean/FALSE"
        field = if n then true else false

-- | Translates an episcopal operator into a method of the Runtime class
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

-- | Generates Jasmin instructions for the specified operator
-- | Assumes that the two operands are on the stack
compileOperator :: Operator -> Instruction
compileOperator operator = instr instruction (expandStack 1 . shrinkStack 2)
  where instruction = jinstrargs "invokestatic" [show (method methodName 2)] -- calls the corresponding method
        methodName = "episcopal/runtime/Runtime/" ++ (operatorMethodName operator) -- get the Runtime method name for the operator

-- | Generates Jasmin instructions for instantiating a distribution of the specified type
-- | Calls one of bernoulli, beta, normal or flip methods of the Runtime class
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

-- | Generates Jasmin instruction for calling the Runtime.sample method
sampleDistribution :: Instruction
sampleDistribution = instr (jinstrargs "invokestatic" [show (method "episcopal/runtime/Runtime/sample" 1)]) id

-- | Compiles a function call
compileCall :: Id -- | Name of the function
            -> [Expression] -- | Arguments for the function
            -> Environment -- | Current environment
            -> Instruction
compileCall name arguments env = case lookupFunction name env of -- try to find the function by its name
                                   (Just function) -> case function of -- function exists
                                     (QueryFunction _ _) -> compileQueryCall function arguments env
                                     (LocalFunction _ _ _ _) -> compileLocalCall function arguments env
                                   Nothing -> error $ "Function " ++ name ++ " does not exist" -- function does not exist

-- | Compiles a call to the specified query function
compileQueryCall :: Function -- | Query function
                 -> [Expression] -- | Arguments for the function
                 -> Environment -- | Current environment
                 -> Instruction
compileQueryCall function arguments env = exec [operands, object, call] -- load function operands and this onto the stack, call the method
  where operands = exec [compileExpression argument env | argument <- arguments] -- compile expression of each argument
        object = instr (jinstr "aload_0") (expandStack 1) -- load this onto the stack
        call = instr (jinstrargs "invokevirtual" [show query]) (expandStack 1 . shrinkStack 1) -- call the query method
        query = method (program ++ "/" ++ name) arity -- query method is defined in the program class
        (QueryFunction name arity) = function
        (Environment program _) = env

-- | Compiles a call to the specified local function
compileLocalCall :: Function  -- | Local function
                 -> [Expression] -- | Arguments for the function
                 -> Environment -- | Current environment
                 -> Instruction
compileLocalCall function arguments env = compileExpression expression env' -- compile the expression in the local function, using the modified environment
  where env' = Environment program functions' -- the modified environment is based on the environment of the let expression that defined the function
        functions' = (zipWith bind parameters arguments) ++ functions -- it contains all the functions in the environment, plus call arguments defined as new local functions
        bind parameter argument = LocalFunction parameter [] argument env -- arguments are bound to the environment of the call expression, not the let expression
        (LocalFunction _ parameters expression fenv) = function
        (Environment program functions) = fenv

-- | Compiles a local definition expression
compileLet :: [Definition] -- | Defined functions
           -> Expression -- | Expression that can use the definitions
           -> Environment -- | Parent environment for the expression
           -> Instruction
compileLet definitions expression env = compileExpression expression env' -- let expression modifies the environment of the contained expression
  where env' = Environment program functions' -- the new environment contains all previously defined functions, plus the function defined by let
        functions' = defined ++ functions'
        defined = [LocalFunction name arguments (head expressions) env | -- for each definition in the let expression a new function is created, capturing the
                   (Definition name arguments expressions) <- definitions] -- environment of the let expression so that it can be reused when calling the defined function
        (Environment program functions) = env

-- | Generates an instruction for observing a sample
observeSample :: Instruction
observeSample = instr (jinstrargs "invokestatic" [show (method "episcopal/runtime/Runtime/observe" 2)]) (expandStack 1 . shrinkStack 2)
