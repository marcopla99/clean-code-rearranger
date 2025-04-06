private fun b() {}
private class C {
    fun bar() {}
    fun foo() {
        bar()
    }
}

private fun a() {
    b()
}
