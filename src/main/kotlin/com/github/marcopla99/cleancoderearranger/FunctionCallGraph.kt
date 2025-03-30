package com.github.marcopla99.cleancoderearranger

import com.github.marcopla99.cleancoderearranger.util.getCalleesInClass
import com.github.marcopla99.cleancoderearranger.util.getCalleesInFile
import com.github.marcopla99.cleancoderearranger.util.getSignature
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtFunction

class FunctionCallGraph(file: KtFile) {
    private val functionsByRoots = emptyMap<KtFunction, List<KtFunction>>().toMutableMap()

    val roots
        get() = functionsByRoots.keys.toList()

    init {
        buildFromFile(file)
        buildFromClass(file)
    }

    private fun buildFromFile(file: KtFile) {
        val functions = file.children.mapNotNull { it as? KtFunction }
        functions.forEach { function ->
            val callees = function.getCalleesInFile(file = file)
            functionsByRoots[function] = functionsByRoots[function].orEmpty() + callees
        }
    }

    private fun buildFromClass(file: KtFile) {
        val classes = file.children.mapNotNull { it as? KtClass }
        classes.forEach { ktClass ->
            val methods = ktClass.body?.children?.mapNotNull { it as? KtFunction } ?: emptyList()
            methods.forEach { method ->
                val callees = method.getCalleesInClass(ktClass = ktClass)
                functionsByRoots[method] = functionsByRoots[method].orEmpty() + callees
            }
        }
    }

    override fun toString(): String = "{" +
            functionsByRoots.entries.joinToString { entry ->
                val key = entry.key.getSignature()
                val value = entry.value.joinToString { it.getSignature() }
                "$key=[$value]"
            } +
            "}"
}