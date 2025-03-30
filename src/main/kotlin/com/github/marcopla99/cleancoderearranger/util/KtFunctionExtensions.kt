package com.github.marcopla99.cleancoderearranger.util

import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.psiUtil.containingClass
import org.jetbrains.kotlin.psi.psiUtil.referenceExpression

fun KtFunction.getSignature(): String {
    val name = this.name ?: "<anonymous>"
    val parameters = this.valueParameters.joinToString { param ->
        val type = param.typeReference?.text ?: "Any"
        "${param.name ?: "_"}: $type"
    }
    val returnType = this.typeReference?.text ?: "Unit"
    return "fun $name($parameters): $returnType"
}

fun KtFunction.getCalleesInFile(file: KtFile): List<KtFunction> {
    return bodyExpression?.children?.flatMap { psiElement ->
        val referenceExpression = (psiElement as? KtCallExpression)?.referenceExpression()
        referenceExpression?.references
            ?.mapNotNull { (it.resolve() as? KtFunction) }
            ?.filter { (it.containingFile as? KtFile) == file } ?: emptyList()
    } ?: emptyList()
}

fun KtFunction.getCalleesInClass(ktClass: KtClass): List<KtFunction> {
    return bodyExpression?.children?.flatMap { psiElement ->
        val referenceExpression = (psiElement as? KtCallExpression)?.referenceExpression()
        referenceExpression?.references
            ?.mapNotNull { (it.resolve() as? KtFunction) }
            ?.filter { it.containingClass() == ktClass } ?: emptyList()
    } ?: emptyList()
}
