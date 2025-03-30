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

        assertEquals(listOf("fun foo(a: Int, b: String): Unit"), functionCallGraph.roots.map { it.getSignature() })
    }

    fun testFileWithTwoFunctions() {
        val ktFile = myFixture.configureByFile("FileWithTwoFunctions.kt") as KtFile

        val functionCallGraph = FunctionCallGraph(ktFile)

        assertEquals("{fun foo(): Unit=[fun bar(): Unit], fun bar(): Unit=[]}", functionCallGraph.toString())
    }

    fun testFileWithClass() {
        val ktFile = myFixture.configureByFile("FileWithClass.kt") as KtFile

        val functionCallGraph = FunctionCallGraph(ktFile)

        assertEquals(
            "{fun b(): Unit=[], fun a(): Unit=[fun b(): Unit]}",
            functionCallGraph.toString()
        )
    }

    override fun getTestDataPath() = "src/test/testData"
}