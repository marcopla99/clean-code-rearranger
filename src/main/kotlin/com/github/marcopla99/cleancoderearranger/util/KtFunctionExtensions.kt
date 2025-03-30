package com.github.marcopla99.cleancoderearranger.util

import org.jetbrains.kotlin.psi.KtFunction

fun KtFunction.getSignature(): String {
    val name = this.name ?: "<anonymous>"
    val parameters = this.valueParameters.joinToString { param ->
        val type = param.typeReference?.text ?: "Any"
        "${param.name ?: "_"}: $type"
    }
    val returnType = this.typeReference?.text ?: "Unit"
    return "fun $name($parameters): $returnType"
}