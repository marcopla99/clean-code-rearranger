package com.github.marcopla99.cleancoderearranger.actions

import com.github.marcopla99.cleancoderearranger.FunctionCallGraphs
import com.github.marcopla99.cleancoderearranger.rearranger.GraphRearranger
import com.github.marcopla99.cleancoderearranger.util.getSignature
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.util.DocumentUtil
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.psiUtil.endOffset

class FunctionsRearrangerAction: AnAction() {
    override fun actionPerformed(anActionEvent: AnActionEvent) {
        val file = anActionEvent.getData(LangDataKeys.PSI_FILE) as? KtFile ?: return
        println("file: ${file.text}")
        val functionCallGraphs = FunctionCallGraphs(file)
        println("functionCallGraphs: $functionCallGraphs")
        val rearrangedFunctions = functionCallGraphs.graphs.map { graph ->
            GraphRearranger.rearrange(graph)
        }
        println("rearrangedFunctions: ${rearrangedFunctions.flatten().map { it.getSignature() }}")
        val document = anActionEvent.getData(CommonDataKeys.EDITOR)?.document ?: return
        val roots = rearrangedFunctions.mapNotNull { it.firstOrNull() }
        val children = rearrangedFunctions.map { it.filterNot { function -> function in roots } }
        DocumentUtil.writeInRunUndoTransparentAction {
            children.flatten().forEach { it.delete() }
            println("deleted")
            roots.forEachIndexed { index, root ->
                var offset = root.endOffset
                println("root offset: $offset")
                val elementsToInsertBack = children[index]
                println("elementsToInsertBack: ${elementsToInsertBack.joinToString { it.text }}")
                elementsToInsertBack.forEach {
                    document.insertString(offset, it.text)
                    offset += it.textRange.endOffset
                }
            }
        }
    }
}