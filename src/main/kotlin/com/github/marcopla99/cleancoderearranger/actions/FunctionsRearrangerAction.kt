package com.github.marcopla99.cleancoderearranger.actions

import com.github.marcopla99.cleancoderearranger.FunctionCallGraphs
import com.github.marcopla99.cleancoderearranger.rearranger.GraphRearranger
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.codeStyle.CodeStyleManager
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.psiUtil.endOffset
import org.jetbrains.kotlin.psi.psiUtil.startOffset

class FunctionsRearrangerAction : AnAction() {
    override fun actionPerformed(anActionEvent: AnActionEvent) {
        val file = anActionEvent.getData(LangDataKeys.PSI_FILE) as? KtFile ?: return
        val project = anActionEvent.project ?: return
        val document = anActionEvent.getData(CommonDataKeys.EDITOR)?.document ?: return
        val documentManager = PsiDocumentManager.getInstance(project)
        val functionCallGraphs = FunctionCallGraphs(file)
        val rearrangedFunctions = functionCallGraphs.graphs.map { graph -> GraphRearranger.rearrange(graph) }
        val roots = rearrangedFunctions.mapNotNull { it.firstOrNull() }
        val children = rearrangedFunctions.map { it.filterNot { function -> function in roots } }
        WriteCommandAction.runWriteCommandAction(project, "Rearrange Functions", "RearrangeFunctions", {
            children.flatten().forEach {
                documentManager.commitDocument(document)
                val lineNumberStart = document.getLineNumber(it.startOffset)
                val lineNumberEnd = document.getLineNumber(it.endOffset) + 1
                val startDeletionOffset = document.getLineStartOffset(lineNumberStart)
                val endDeletionOffset = document.getLineStartOffset(lineNumberEnd.coerceAtMost(document.lineCount-1))
                document.deleteString(startDeletionOffset, endDeletionOffset)
            }
            documentManager.commitDocument(document)
            roots.forEachIndexed { index, root ->
                var offset = root.endOffset
                val elementsToInsertBack = children[index]
                elementsToInsertBack.forEach {
                    documentManager.commitDocument(document)
                    document.insertString(offset, it.text)
                    offset += it.textLength
                }
            }
            documentManager.commitDocument(document)
            CodeStyleManager.getInstance(project).reformat(file)
        }, file)
    }
}