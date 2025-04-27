package com.github.marcopla99.cleancoderearranger.graph

import com.github.marcopla99.cleancoderearranger.util.getSignature
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiRecursiveElementVisitor
import com.intellij.psi.util.parentOfType
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.getChildOfType

typealias Graph = Map<KtFunction, List<KtFunction>>

class FunctionCallGraphs(file: KtFile) {
    private val _graphs: MutableMap<KtDeclarationContainer, Graph> = mutableMapOf()
    val graphs: Map<KtDeclarationContainer, Graph> = _graphs

    init {
        buildFromFile(file)
    }

    private fun buildFromFile(file: KtFile) {
        val visitor = object : PsiRecursiveElementVisitor() {
            override fun visitElement(element: PsiElement) {
                val graphKey = element.parentOfType<KtClass>() ?: file
                if (element is KtNamedFunction) {
                    addCalleeToParentInGraph(graphKey = graphKey, parent = element, callee = null)
                }
                if (element is KtCallExpression) {
                    // todo make sure it's part of the file?
                    val callee = element.getChildOfType<KtNameReferenceExpression>()?.references?.firstNotNullOfOrNull { it.resolve() as? KtFunction }
                    val parent = element.parentOfType<KtCallExpression>()
                        ?.getChildOfType<KtNameReferenceExpression>()
                        ?.references
                        ?.firstNotNullOfOrNull { it.resolve() as? KtFunction }
                        ?: element.parentOfType<KtNamedFunction>()
                    if (callee != null && parent != null) {
                        addCalleeToParentInGraph(graphKey = graphKey, parent = parent, callee = callee)
                    }
                }
                super.visitElement(element)
            }
        }
        visitor.visitElement(file)
    }

    private fun addCalleeToParentInGraph(
        graphKey: KtDeclarationContainer,
        parent: KtFunction,
        callee: KtFunction?
    ) {
        val calleesInParent = _graphs[graphKey]?.get(parent).orEmpty().let { if (callee != null) it + callee else it }
        _graphs[graphKey] = _graphs[graphKey].orEmpty() + (parent to calleesInParent)
    }

    override fun toString(): String = "[" + graphs.values.joinToString { graph ->
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