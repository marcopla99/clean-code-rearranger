fun b(): Unit {
}

fun foo(): Unit {
    bar()
}

fun e(): Unit {
}

fun d(): Unit {
    e()
}

fun a(): Unit {
    b()
}

fun c(): Unit {
    e()
    d()
}

fun bar(): Unit {
}
