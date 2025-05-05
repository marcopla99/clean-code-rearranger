package com.github.marcopla99.cleancoderearranger.actions

import com.github.marcopla99.cleancoderearranger.graph.FunctionCallGraphs
import com.github.marcopla99.cleancoderearranger.rearranger.GraphRearranger
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.PsiElement
import com.intellij.refactoring.extractMethod.newImpl.ExtractMethodHelper.addSiblingAfter
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtFunction

class FunctionsRearrangerAction : AnAction() {
    override fun actionPerformed(anActionEvent: AnActionEvent) {
        val file = anActionEvent.getData(LangDataKeys.PSI_FILE) as? KtFile ?: return
        val project = anActionEvent.project ?: return
        val functionCallGraphs = FunctionCallGraphs(file)
        val rearrangedFunctions = functionCallGraphs.graphs.values.map { graph -> GraphRearranger.rearrange(graph) }
        val roots = rearrangedFunctions.mapNotNull { it.firstOrNull() }
        val children = rearrangedFunctions.map { it.filterNot { function -> function in roots } }
        WriteCommandAction.runWriteCommandAction(project, "Rearrange Functions", "RearrangeFunctions", {
            val childrenCopy = children.map { it.map(PsiElement::copy) }
            children.flatten().forEach(KtFunction::delete)
            roots.forEachIndexed { index, root ->
                val elementsToInsertBack = childrenCopy[index]
                var previous: PsiElement = root
                for (function in elementsToInsertBack) {
                    previous = previous.addSiblingAfter(function)
                }
            }
        }, file)
    }
}