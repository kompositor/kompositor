package top.sandwwraith.kompositor

class Greeter() {
    fun greet(whom: String) = "Hello, $whom!"
}

fun main(args: Array<String>) {
    println(Greeter().greet("world"))
}