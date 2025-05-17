private class Class {
    private fun b() {

    }

    inner class InnerClass {
        private fun b1() {

        }

        private fun a1() {
            b1()
        }
    }

    private fun a() {
        b()
    }
}