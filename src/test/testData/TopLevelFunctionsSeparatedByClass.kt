fun b() {}

class C {
    fun bar() {}
    fun foo() {
        bar()
    }
}

fun a() {
    b()
}