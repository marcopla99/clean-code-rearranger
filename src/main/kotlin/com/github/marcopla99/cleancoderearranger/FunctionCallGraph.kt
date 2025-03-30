package com.github.marcopla99.cleancoderearranger

import com.github.marcopla99.cleancoderearranger.util.getCalleesInClass
import com.github.marcopla99.cleancoderearranger.util.getCalleesInFile
import com.github.marcopla99.cleancoderearranger.util.getSignature
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtFunction

class FunctionCallGraph(file: KtFile) {
    private val graphs: MutableList<Map<KtFunction, List<KtFunction>>> = mutableListOf()

    init {
        buildFromFile(file)
        buildFromClasses(file)
    }

    private fun buildFromFile(file: KtFile) {
        val functions = file.children.mapNotNull { it as? KtFunction }
        val graph = buildMap<KtFunction, List<KtFunction>> {
            functions.forEach { function ->
                val callees = function.getCalleesInFile(file = file)
                put(function, get(function).orEmpty() + callees)
            }
        }
        if (graph.isNotEmpty()) graphs.add(graph)
    }

    private fun buildFromClasses(file: KtFile) {
        val classes = file.children.mapNotNull { it as? KtClass }
        classes.forEach { ktClass ->
            val methods = ktClass.body?.children?.mapNotNull { it as? KtFunction } ?: emptyList()
            val graph = buildMap<KtFunction, List<KtFunction>> {
                methods.forEach { method ->
                    val callees = method.getCalleesInClass(ktClass = ktClass)
                    put(method, get(method).orEmpty() + callees)
                }
            }
            if (graph.isNotEmpty()) graphs.add(graph)
        }
    }

    override fun toString(): String = "[" + graphs.joinToString { graph ->
        buildString {
            append("{")
            append(
                graph.map { entry ->
                    val key = entry.key.getSignature()
                    val value = entry.value.joinToString { it.getSignature() }
                    "$key=[$value]"
                }.joinToString()
            )
            append("}")
        }
    } + "]"
}