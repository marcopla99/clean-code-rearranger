private fun a() {
    object : AbstractClass() {
        override fun c() {
            d()
        }

        override fun d() {

        }
    }
    b()
}

private fun b() {

}