package com.github.marcopla99.cleancoderearranger

import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtFunction

class FunctionCallGraph(file: KtFile) {
    private val functionsByRoots = emptyMap<KtFunction, List<KtFunction>>().toMutableMap()

    val roots
        get() = functionsByRoots.keys.toList()

    init {
        val functions = file.children.mapNotNull { it as? KtFunction }
        functions.forEach { function ->
            val callees = function.children.mapNotNull { it as? KtFunction }
            functionsByRoots[function] = functionsByRoots[function].orEmpty() + callees
        }
    }
}
