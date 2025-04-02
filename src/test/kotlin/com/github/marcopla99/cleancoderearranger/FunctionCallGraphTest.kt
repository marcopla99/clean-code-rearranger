package com.github.marcopla99.cleancoderearranger

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.jetbrains.kotlin.psi.KtFile

class FunctionCallGraphTest: BasePlatformTestCase() {

    fun testFileWithoutFunctions() {
        val ktFile = myFixture.configureByFile("EmptyFile.kt") as KtFile

        val functionCallGraph = FunctionCallGraph(ktFile)

        assertEquals("[]", functionCallGraph.toString())
    }

    fun testFileWithOneFunction() {
        val ktFile = myFixture.configureByFile("FileWithOneFunction.kt") as KtFile

        val functionCallGraph = FunctionCallGraph(ktFile)

        assertEquals("[{fun foo(a: Int, b: String): Unit=[]}]", functionCallGraph.toString())
    }

    fun testFileWithTwoFunctions() {
        val ktFile = myFixture.configureByFile("FileWithTwoFunctions.kt") as KtFile

        val functionCallGraph = FunctionCallGraph(ktFile)

        assertEquals("[{fun b(): Unit=[], fun a(): Unit=[fun b(): Unit]}]", functionCallGraph.toString())
    }

    fun testFileWithClass() {
        val ktFile = myFixture.configureByFile("FileWithClass.kt") as KtFile

        val functionCallGraph = FunctionCallGraph(ktFile)

        assertEquals(
            "[{fun b(): Unit=[], fun a(): Unit=[fun b(): Unit]}]",
            functionCallGraph.toString()
        )
    }

    fun testFileWithFunctionsOutsideClass() {
        val ktFile = myFixture.configureByFile("FunctionsOutsideClass.kt") as KtFile

        val functionCallGraph = FunctionCallGraph(ktFile)

        assertEquals(
            "[" +
                    "{fun b(): Unit=[], fun a(): Unit=[fun b(): Unit]}, " +
                    "{fun d(): Unit=[], fun e(): Unit=[fun d(): Unit]}" +
                    "]",
            functionCallGraph.toString()
        )
    }

    override fun getTestDataPath() = "src/test/testData"
}