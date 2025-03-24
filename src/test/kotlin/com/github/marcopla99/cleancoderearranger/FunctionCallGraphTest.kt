package com.github.marcopla99.cleancoderearranger

import com.github.marcopla99.cleancoderearranger.util.getSignature
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtFunction

class FunctionCallGraphTest: BasePlatformTestCase() {

    fun testFileWithoutFunctions() {
        val ktFile = myFixture.configureByFile("EmptyFile.kt") as KtFile

        val functionCallGraph = FunctionCallGraph(ktFile)

        assertEquals(emptyList<KtFunction>(), functionCallGraph.roots)
    }

    fun testFileWithOneFunction() {
        val ktFile = myFixture.configureByFile("FileWithOneFunction.kt") as KtFile

        val functionCallGraph = FunctionCallGraph(ktFile)

        assertEquals(listOf("fun foo(): Unit"), functionCallGraph.roots.map { it.getSignature() })
    }

    override fun getTestDataPath() = "src/test/testData"
}