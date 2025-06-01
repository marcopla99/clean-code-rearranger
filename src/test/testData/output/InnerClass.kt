private class Class {

    inner class InnerClass {

        private fun a1() {
            b1()
        }

        private fun b1() {

        }
    }

    private fun a() {
        b()
    }

    private fun b() {

    }
}