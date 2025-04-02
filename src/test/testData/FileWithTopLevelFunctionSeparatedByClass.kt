fun a() {
    b()
}

class C {
    fun bar() {}
    fun foo() {
        bar()
    }
}

fun b() {}