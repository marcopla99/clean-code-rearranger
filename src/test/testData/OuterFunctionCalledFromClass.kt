fun b () {}

class C {
    fun d() {
    }

    fun e() {
        d()
        a()
    }
}

fun a() {
    b()
}