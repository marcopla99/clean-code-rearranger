package com.github.marcopla99.cleancoderearranger

import com.intellij.lang.Language
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.refactoring.extractMethod.newImpl.ExtractMethodHelper.addSiblingAfter
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtFunction

class FunctionsRearrangerAction : AnAction() {
    override fun update(anActionEvent: AnActionEvent) {
        val file = anActionEvent.getData(LangDataKeys.PSI_FILE)
        val enabled = file != null && file.language.`is`(Language.findLanguageByID("kotlin")) && file.isWritable
        anActionEvent.presentation.isEnabled = enabled
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    override fun actionPerformed(anActionEvent: AnActionEvent) {
        val file = anActionEvent.getData(LangDataKeys.PSI_FILE) as? KtFile ?: return
        val project = anActionEvent.project ?: return
        val document = anActionEvent.getData(CommonDataKeys.EDITOR)?.document ?: return
        val documentManager = PsiDocumentManager.getInstance(project)
        val functionCallGraphs = FunctionCallGraphs(file)
        val rearrangedFunctions = functionCallGraphs.graphs.values.map { graph -> GraphRearranger.rearrange(graph) }
        val tops = rearrangedFunctions.mapNotNull { it.firstOrNull() }
        val bottoms = rearrangedFunctions.map { it.filterNot { function -> function in tops } }
        WriteCommandAction.runWriteCommandAction(project, null, null, {
            val bottomsCopy = bottoms.map { it.map(PsiElement::copy) }
            bottoms.flatten().forEach(KtFunction::delete)
            tops.forEachIndexed { index, root ->
                val elementsToInsertBack = bottomsCopy[index]
                var previous: PsiElement = root
                for (function in elementsToInsertBack) {
                    previous = previous.addSiblingAfter(function)
                }
            }
            documentManager.doPostponedOperationsAndUnblockDocument(document)
            anActionEvent.reformatWithUserPreferences()
        }, file)
    }
}

private fun AnActionEvent.reformatWithUserPreferences() {
    ActionManager.getInstance().getAction("ReformatCode")?.actionPerformed(this)
}
