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

data ClassSpec = ClassPublic | ClassFinal | ClassSuper | ClassInterface | ClassAbstract

instance Show ClassSpec where
  show ClassPublic = "public"
  show ClassFinal = "final"
  show ClassSuper = "super"
  show ClassInterface = "interface"
  show ClassAbstract = "abstract"

data FieldSpec = FieldPublic | FieldPrivate | FieldProtected | FieldStatic | FieldFinal | FieldVolatile | FieldTransient

instance Show FieldSpec where
  show FieldPublic = "public"
  show FieldPrivate = "private"
  show FieldProtected = "protected"
  show FieldStatic = "static"
  show FieldFinal = "final"
  show FieldVolatile = "volatile"
  show FieldTransient = "transient"

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

data Method = Method Identifier [TypeSpec] TypeSpec

instance Show Method where
  show (Method name parameters returntype) = concat [name, "(", (concat $ map show parameters), ")", show returntype]

jsource :: Identifier -> String
jsource name = unwords [".source", name]

jinterface :: Identifier -> String
jinterface name = unwords [".interface", name]

jclass :: [ClassSpec] -> Identifier -> String
jclass spec name = unwords [".class", unwords $ map show spec, name]

jsuper :: Identifier -> String
jsuper name = unwords [".super", name]

jimplements :: Identifier -> String
jimplements name = unwords [".implements", name]

jfield :: [FieldSpec] -> Identifier -> TypeSpec -> String
jfield spec name jtype = unwords [".field", unwords $ map show spec, name, show jtype]

jfieldvalue :: [FieldSpec] -> Identifier -> TypeSpec -> String -> String
jfieldvalue spec name jtype value = unwords [jfield spec name jtype, "=", value]

jmethod :: [MethodSpec] -> Method -> String
jmethod spec method = unwords [".method", unwords $ map show spec, show method]

jstack :: Int -> String
jstack n = unwords [".limit stack", show n]

jlocals :: Int -> String
jlocals n = unwords [".limit locals", show n]

jinstr :: Identifier -> String
jinstr name = name

jinstrargs :: Identifier -> [String] -> String
jinstrargs name arguments = unwords [jinstr name, unwords arguments]

jmethodend :: String
jmethodend = ".end method"

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
