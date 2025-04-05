package com.github.marcopla99.cleancoderearranger.actions

import com.github.marcopla99.cleancoderearranger.FunctionCallGraphs
import com.github.marcopla99.cleancoderearranger.rearranger.GraphRearranger
import com.github.marcopla99.cleancoderearranger.util.getSignature
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import org.jetbrains.kotlin.psi.KtFile

class FunctionsRearrangerAction: AnAction() {
    override fun actionPerformed(anActionEvent: AnActionEvent) {
        val file = anActionEvent.getData(LangDataKeys.PSI_FILE) as KtFile
        println("file: ${file.text}")
        val functionCallGraphs = FunctionCallGraphs(file)
        println("functionCallGraphs: $functionCallGraphs")
        val rearrangedFunctions = functionCallGraphs.graphs.map { graph ->
            GraphRearranger.rearrange(graph)
        }
        println("rearrangedFunctions: ${rearrangedFunctions.flatten().map { it.getSignature() }}")
    }
}