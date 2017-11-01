module Jasmin (Identifier,
               ClassSpec(..),
               FieldSpec(..),
               MethodSpec(..),
               TypeSpec(..),
               Method(..),
               jsource,
               jinterface,
               jclass,
               jsuper,
               jimplements,
               jfield,
               jfieldvalue,
               jmethod,
               jstack,
               jlocals,
               jinstr,
               jinstrargs,
               jmethodend) where

type Identifier = String

-- | Jasmin class directive attributes
data ClassSpec = ClassPublic | ClassFinal | ClassSuper | ClassInterface | ClassAbstract

instance Show ClassSpec where
  show ClassPublic = "public"
  show ClassFinal = "final"
  show ClassSuper = "super"
  show ClassInterface = "interface"
  show ClassAbstract = "abstract"

-- | Jasmin field directive attributes
data FieldSpec = FieldPublic | FieldPrivate | FieldProtected | FieldStatic | FieldFinal | FieldVolatile | FieldTransient

instance Show FieldSpec where
  show FieldPublic = "public"
  show FieldPrivate = "private"
  show FieldProtected = "protected"
  show FieldStatic = "static"
  show FieldFinal = "final"
  show FieldVolatile = "volatile"
  show FieldTransient = "transient"

-- | Jasmin method directive attributes
data MethodSpec = MethodPublic | MethodPrivate | MethodProtected | MethodStatic | MethodFinal | MethodSynchronized | MethodNative | MethodAbstract

instance Show MethodSpec where
  show MethodPublic = "public"
  show MethodPrivate = "private"
  show MethodProtected = "protected"
  show MethodStatic = "static"
  show MethodFinal = "final"
  show MethodSynchronized = "synchronized"
  show MethodNative = "native"
  show MethodAbstract = "abstract"

-- | JVM types
data TypeSpec = TypeByte | TypeChar | TypeDouble | TypeFloat | TypeInt | TypeLong | TypeShort | TypeBoolean | TypeVoid | TypeArray TypeSpec | TypeObject Identifier

instance Show TypeSpec where
  show TypeByte = "B"
  show TypeChar = "C"
  show TypeDouble = "D"
  show TypeFloat = "F"
  show TypeInt = "I"
  show TypeLong = "L"
  show TypeShort = "S"
  show TypeBoolean = "Z"
  show TypeVoid = "V"
  show (TypeArray t) = "[" ++ (show t)
  show (TypeObject i) = "L" ++ i ++ ";"

-- | JVM method with the name, types of parameters and return type
data Method = Method Identifier [TypeSpec] TypeSpec

instance Show Method where
  show (Method name parameters returntype) = concat [name, "(", (concat $ map show parameters), ")", show returntype]

-- | Jasmin source directive
jsource :: Identifier -- | File name
        -> String
jsource name = unwords [".source", name]

-- | Jasmin interface directive
jinterface :: Identifier -- | Interface name
           -> String
jinterface name = unwords [".interface", name]

-- | Jasmin class directive
jclass :: [ClassSpec] -- | Class attributes
       -> Identifier -- | Class name
       -> String
jclass spec name = unwords [".class", unwords $ map show spec, name]

-- | Jasmin super directive
jsuper :: Identifier -- | Parent class name
       -> String
jsuper name = unwords [".super", name]

-- | Jasmin implements directive
jimplements :: Identifier -- | Interface name
            -> String
jimplements name = unwords [".implements", name]

-- | Jasmin field directive (without default value)
jfield :: [FieldSpec] -- | Field attributes
       -> Identifier -- | Field name
       -> TypeSpec -- | Field type
       -> String
jfield spec name jtype = unwords [".field", unwords $ map show spec, name, show jtype]

-- | Jasmin field directive (with default value)
jfieldvalue :: [FieldSpec] -- | Field attributes
            -> Identifier -- | Field name
            -> TypeSpec -- | Field type
            -> String -- | Default value
            -> String
jfieldvalue spec name jtype value = unwords [jfield spec name jtype, "=", value]

-- | Jasmin method directive
jmethod :: [MethodSpec] -- | Method attributes
        -> Method -- | Method definition
        -> String
jmethod spec method = unwords [".method", unwords $ map show spec, show method]

-- | Jasmin stack size directive
jstack :: Int -> String
jstack n = unwords [".limit stack", show n]

-- | Jasmin locals size directive
jlocals :: Int -> String
jlocals n = unwords [".limit locals", show n]

-- | Jasmin instruction with no arguments
jinstr :: Identifier -- | Instruction name
       -> String
jinstr name = name

-- | Jasmin instruction with arguments
jinstrargs :: Identifier -- | Instruction name
           -> [String] -- | Instruction arguments
           -> String
jinstrargs name arguments = unwords [jinstr name, unwords arguments]

-- | Jasmin method end directive
jmethodend :: String
jmethodend = ".end method"

-- | Example program
jexample =
  [jsource "source.java",

   jinterface "stand/Interface",

   jclass [ClassPublic] "MyClass",
   jsuper "stand/ParentClass",
   jimplements "stand/Interface",

   jfieldvalue [FieldPublic, FieldStatic, FieldFinal] "someField" TypeInt "123",
   jfield [FieldPrivate] "otherField" (TypeObject "acme/Value"),

   jmethod [MethodPublic] (Method "doSomething" [TypeInt, TypeBoolean] TypeFloat),
   jstack 10,
   jlocals 20,

   jinstr "aload_0",
   jinstrargs "invokevirtual" [show (Method "MyClass/somethingElse" [] TypeVoid)],
   jinstr "return",
   jmethodend,

   jmethod [MethodPrivate] (Method "somethingElse" [] TypeVoid),
   jinstr "return",
   jmethodend]
