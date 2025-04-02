fun b(): Unit {
}

fun foo(): Unit {
    bar()
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

fun e(): Unit {
}