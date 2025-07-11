package com.github.marcopla99.cleancoderearranger

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
        object : PsiRecursiveElementVisitor() {
            override fun visitElement(element: PsiElement) {
                val container = element.parentOfType<KtClassBody>() ?: file
                if (element is KtNamedFunction) {
                    addCalleeToParentInGraph(graphKey = container, parent = element, callee = null)
                }
                if (element is KtCallExpression) {
                    val callee = element.getChildOfType<KtNameReferenceExpression>()
                        ?.references?.firstNotNullOfOrNull { it.resolve() as? KtFunction }
                    val calleeContainer = callee?.parentOfType<KtClassBody>() ?: file
                    if ((callee?.containingFile as? KtFile) == file && calleeContainer == container) {
                        val parent = element.getFirstKtFunctionParentInFile(file)
                            ?: element.parentOfType<KtNamedFunction>()
                        if (parent != null && callee != parent) {
                            addCalleeToParentInGraph(graphKey = container, parent = parent, callee = callee)
                        } else if (element.parentOfType<KtClassInitializer>() != null) {
                            addCalleeToParentInGraph(graphKey = container, parent = callee, callee = null)
                        }
                    }
                }
                super.visitElement(element)
            }
        }.visitElement(file)
    }

    private fun addCalleeToParentInGraph(
        graphKey: KtDeclarationContainer,
        parent: KtFunction,
        callee: KtFunction?,
    ) {
        val calleesInParent = _graphs[graphKey]?.get(parent).orEmpty().let { if (callee != null) it + callee else it }
        _graphs[graphKey] = _graphs[graphKey].orEmpty() + (parent to calleesInParent)
    }

    override fun toString(): String = "[" + graphs.values.joinToString { graph ->
        buildString {
            append("{")
            append(
                graph.map { entry ->
                    val key = entry.key.name
                    val value = entry.value.joinToString { it.name ?: "<anonymous>" }
                    "$key=[$value]"
                }.joinToString()
            )
            append("}")
        }
    } + "]"
}

private fun PsiElement.getFirstKtFunctionParentInFile(file: KtFile): KtFunction? {
    var parent = getKtFunctionParent()
    while (parent != null && parent.containingFile != file) {
        parent = parent.getKtFunctionParent()
    }
    return parent
}

private fun PsiElement.getKtFunctionParent() = parentOfType<KtCallExpression>()
    ?.getChildOfType<KtNameReferenceExpression>()
    ?.references
    ?.firstNotNullOfOrNull { it.resolve() as? KtFunction }
