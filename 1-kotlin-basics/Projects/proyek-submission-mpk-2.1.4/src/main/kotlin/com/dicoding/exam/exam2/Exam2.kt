package com.dicoding.exam.exam2

// TODO 1
fun calculate(valueA: Int, valueB: Int, valueC: Int?): Int {
    var result: Int = 0

    if (valueC === null) {
        result = valueA + valueB - 50
        return result
    }

    result = valueA + valueB - valueC

    return result
}

// TODO 2
fun result(result: Int): String {
    return "Result is $result"
}
