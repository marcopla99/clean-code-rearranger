package com.github.marcopla99.cleancoderearranger

import com.github.marcopla99.cleancoderearranger.util.getSignature
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.psiUtil.referenceExpression

class FunctionCallGraph(file: KtFile) {
    private val functionsByRoots = emptyMap<KtFunction, List<KtFunction>>().toMutableMap()

    val roots
        get() = functionsByRoots.keys.toList()

    init {
        val functions = file.children.mapNotNull { it as? KtFunction }
        functions.forEach { function ->
            val callees = function.getCalleesInFile(file = file)
            functionsByRoots[function] = functionsByRoots[function].orEmpty() + callees
        }
        val classes = file.children.mapNotNull { it as? KtClass }
        classes.forEach { ktClass ->
            val methods = ktClass.body?.children?.mapNotNull { it as? KtFunction } ?: emptyList()
            methods.forEach { function ->
                val callees = function.getCalleesInFile(file = file)
                functionsByRoots[function] = functionsByRoots[function].orEmpty() + callees
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

private fun KtFunction.getCalleesInFile(file: KtFile): List<KtFunction> {
    return bodyExpression?.children?.flatMap { psiElement ->
        val referenceExpression = (psiElement as? KtCallExpression)?.referenceExpression()
        referenceExpression?.references
            ?.mapNotNull { (it.resolve() as? KtFunction) }
            ?.filter { (it.containingFile as? KtFile) == file } ?: emptyList()
    } ?: emptyList()
}
