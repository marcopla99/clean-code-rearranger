package com.github.marcopla99.cleancoderearranger

import com.github.marcopla99.cleancoderearranger.util.getCalleesInClass
import com.github.marcopla99.cleancoderearranger.util.getSignature
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiRecursiveElementVisitor
import com.intellij.psi.util.parentOfType
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.getChildOfType

typealias Graph = Map<KtFunction, List<KtFunction>>

class FunctionCallGraphs(file: KtFile) {
    private val _graphs: MutableList<Graph> = mutableListOf()
    val graphs: List<Graph> = _graphs

    init {
        buildFromFile(file)
        // todo fix classes
//        buildFromClasses(file)
    }

    private fun buildFromFile(file: KtFile) {
        val visitor = object : PsiRecursiveElementVisitor() {
            override fun visitElement(element: PsiElement) {
                if (element is KtNamedFunction) {
                    if (_graphs.isEmpty()) _graphs.add(mapOf(element to listOf()))
                    _graphs[0] = _graphs[0] + (element to _graphs[0].getOrDefault(element, listOf()))
                }
                if (element is KtCallExpression) {
                    // todo make sure it's part of the file
                    val callee = element.getChildOfType<KtNameReferenceExpression>()?.references?.firstNotNullOfOrNull { it.resolve() as? KtFunction }
                    val parent = element.parentOfType<KtCallExpression>()
                        ?.getChildOfType<KtNameReferenceExpression>()
                        ?.references
                        ?.firstNotNullOfOrNull { it.resolve() as? KtFunction }
                        ?: element.parentOfType<KtNamedFunction>()
                    if (callee != null) {
                        parent?.let { _graphs[0] = _graphs[0] + (parent to _graphs[0][parent].orEmpty() + callee) }
                    }
                }
                super.visitElement(element)
            }
        }
        visitor.visitElement(file)
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