fun a() {
    b()
    c()
}

fun d() {}

fun c() {
    d()
}

fun b() {
    d()
}