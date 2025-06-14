private fun foo() {
    bar()
}

private fun bar() {

}

class C {
    fun a() {
        b()
    }

    fun b() {

    }
}
