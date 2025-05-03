package kalbe.corp.basicsyntax

fun main() {
    println("Hello World.")
}

// When Expressions
/*
    val value = 20

    when(value){
        6 -> println("value is 6")
        7 -> println("value is 7")
        8 -> println("value is 8")
        else -> println("value cannot be reached")
    }

    val stringOfValue = when (value) {
        6 -> "value is 6"
        7 -> "value is 7"
        8 -> "value is 8"
        else -> "value cannot be reached"
    }

    val stringOfValue = when (value) {
        6 -> {
            println("Six")
            "value is 6"
        }
        7 -> {
            println("Seven")
            "value is 7"
        }
        8 -> {
            println("Eight")
            "value is 8"
        }
        else -> {
            println("undefined")
            "value cannot be reached"
        }
    }

    when(anyType){
        is Long -> println("the value has a Long type")
        is String -> println("the value has a String type")
        else -> println("undefined")
    }

    when(value){
        in ranges -> println("value is in the range")
        !in ranges -> println("value is outside the range")
    }

    val registerNumber = when(val regis = getRegisterNumber()){
        in 1..50 -> 50 * regis
        in 51..100 -> 100 * regis
        else -> regis
    }
*/

// Functions
/*
    fun printUser(name: String) {
        print("Your name is $name")
    }

    fun printUser(name: String): Unit {
        print("Your name is $name")
    }

    // Named Argument
    fun main() {
        val fullName = getFullName(first = "Kotlin" , middle = " is ", last = "Awesome")
        val fullName = getFullName(middle = " is " , first = "Kotlin", last = "Awesome")
            // bisa bebas posisi argumennya
        print(fullName)
    }

    fun getFullName(first: String, middle: String, last: String): String {
        return "$first $middle $last"
    }

    // Default Argument
    fun getFullName(
            first: String = "Kotlin",
            middle: String = " is ",
            last: String = "Awesome"): String {
        return "$first $middle $last"
    }

    // vararg (Variable Argument)
    fun main() {
        val number = sumNumbers(10, 20, 30, 40)
        print(number)
    }

    fun sumNumbers(vararg number: Int): Int {
        return number.sum()
    }

    // bisa gini juga

    fun main() {
        val number = intArrayOf(10, 20, 30, 40)
        sets(10, 20, 20, *number , 10)
    }

    fun sets(vararg number: Int): Int {
        ...
    }
*/

// Data Types & Variable
/*
    val valueA: Int = 10
    val valueB = 20
    print(valueA + valueB)
 */

// Char
/*
    val character = 'A'
    val character: Char = 'ABC'   // Incorrect character literal

    var vocal = 'A'
    println("Vocal " + vocal++)
    println("Vocal " + vocal++)
    println("Vocal " + vocal++)
    println("Vocal " + vocal--)
    println("Vocal " + vocal--)
    println("Vocal " + vocal--)
    println("Vocal " + vocal--)
 */

// String
/*
    val text  = "Dicoding"
    val firstChar = text[3]

    print("The 4th character of the $text is $firstChar")

    val text  = "Kotlin"
    for (char in text){
        print("$char ")
    }

    Escaped String
    val statement = "Kotlin is "Awesome!"" // wrong
    val statement = "Kotlin is \"Awesome!\"" // correct

    Raw String
    // without raw string
    val line = "Line 1\n" +
        "Line 2\n" +
        "Line 3\n" +
        "Line 4\n"

    // with raw string (triple """)
    val line = """
        Line 1
        Line 2
        Line 3
        Line 4
    """.trimIndent()


   // String Template
   val nama: String = "Raffi"
   println("Halo, nama saya $nama")

   val hour = 7
    print("Office ${if (hour > 7) "already close" else "is open"}")
*/

// If-else statements/expressions
/*
    val openHours = 7
    val now = 5
    val office: String

    office = if (now > 7) {
        "Office already open"
    } else if (now == openHours){
        "Wait a minute, office will be open"
    } else {
        "Office is closed"
    }
*/

// Boolean
/*
    // AND / &&
    val officeOpen = 7
    val officeClosed = 16
    val now = 20

    val isOpen = if (now >= officeOpen && now <= officeClosed){
        true
    } else {
        false
    }

    // OR / ||
    val isClose = now < officeOpen || now > officeClosed

    // NOT / !
    if (!isOpen) {
    print("Office is closed")
    } else {
        print("Office is open")
    }
*/

// Numbers
/*
    Int 32 Bit
    val intNumber = 100

    Long 64 Bit
    val longNumber: Long = 100
    val longNumber = 100L

    Short 16 Bit
    val shortNumber: Short = 10

    Byte (8 Bit)
    val byteNumber = 0b11010010

    Double (64 Bit)
    val doubleNumber: Double = 1.3

    Float (32 Bit)
    val floatNumber: Float = 0.123456789f    // yang terbaca hanya 0.1234567

    val maxInt = Int.MAX_VALUE
    val minInt = Int.MIN_VALUE

    val readableNumber = 1_000_000
    print(readableNumber)

    Conversion Numbers
    toByte(): Byte
    toShort(): Short
    toInt(): Int
    toLong(): Long
    toFloat(): Float
    toDouble(): Double
    toChar(): Char

    val byteNumber: Byte = 10
    val intNumber: Int = byteNumber.toInt() // ready to go
*/

// Arrays
/*
    val array = arrayOf(1, 3, 5, 7)
    val mixArray = arrayOf(1, 3, 5, 7 , "Dicoding" , true)

    val intArray = intArrayOf(1, 3, 5, 7)
*/

// Nullable Types
/*
    val text: String = null // compile time error
    val text: String? = null

    val text: String? = null
    val textLength = text.length // compile time error

    With Safe Calls (?.)
    val text: String? = null
    text?.length

    With Elvis Operator (?:)
    val text: String? = null
    val textLength = text?.length ?: 7

    With non-null assertion (!!) | NOT RECOMMENDED!! |
    val text: String? = null
    val textLength = text!!.length // ready to go ???
*/