private class C {
    fun foo() {
        bar()
    }

    fun bar() {}
}

private fun a() {
    b()
}

private fun b() {}