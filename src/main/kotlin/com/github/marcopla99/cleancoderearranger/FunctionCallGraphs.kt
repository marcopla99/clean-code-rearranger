package com.github.marcopla99.cleancoderearranger

import com.github.marcopla99.cleancoderearranger.util.getCalleesInClass
import com.github.marcopla99.cleancoderearranger.util.getCalleesInFile
import com.github.marcopla99.cleancoderearranger.util.getSignature
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtFunction

typealias Graph = Map<KtFunction, List<KtFunction>>

class FunctionCallGraphs(file: KtFile) {
    private val _graphs: MutableList<Graph> = mutableListOf()
    val graphs: List<Graph> = _graphs

    init {
        buildFromFile(file)
        buildFromClasses(file)
    }

    private fun buildFromFile(file: KtFile) {
        val functions = file.children.mapNotNull { it as? KtFunction }
        val graph = buildGraph {
            functions.forEach { function ->
                val callees = function.getCalleesInFile(file = file)
                put(function, get(function).orEmpty() + callees)
            }
        }
        if (graph.isNotEmpty()) _graphs.add(graph)
    }

    private fun buildFromClasses(file: KtFile) {
        val classes = file.children.mapNotNull { it as? KtClass }
        classes.forEach { ktClass ->
            val declarations = ktClass.body?.children?.mapNotNull { it as? KtDeclaration } ?: emptyList()
            val graph = buildGraph {
                declarations.forEach { declaration ->
                    val callees = declaration.getCalleesInClass(ktClass = ktClass)
                    if (declaration !is KtFunction) {
                        callees.forEach { put(it, emptyList()) }
                    } else {
                        put(declaration, get(declaration).orEmpty() + callees)
                    }
                }
            }
            if (graph.isNotEmpty()) _graphs.add(graph)
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

private inline fun buildGraph(
    builderAction: MutableMap<KtFunction, List<KtFunction>>.() -> Unit
) = buildMap(builderAction)