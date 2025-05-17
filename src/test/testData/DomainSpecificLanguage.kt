fun a() {
    b {
        d()
    }
    c {
        e()
    }
}

fun d() {

}

fun c(p1: () -> Unit) {

}

fun e() {

}

fun b(p1: () -> Unit) {

}
