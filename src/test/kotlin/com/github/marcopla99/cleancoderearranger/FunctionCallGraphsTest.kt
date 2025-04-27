package com.github.marcopla99.cleancoderearranger

import com.github.marcopla99.cleancoderearranger.graph.FunctionCallGraphs
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.jetbrains.kotlin.psi.KtFile

class FunctionCallGraphsTest: BasePlatformTestCase() {

    fun testFileWithoutFunctions() {
        val ktFile = myFixture.configureByFile("EmptyFile.kt") as KtFile

        val functionCallGraphs = FunctionCallGraphs(ktFile)

        assertEquals("[]", functionCallGraphs.toString())
    }

    fun testFileWithOneFunction() {
        val ktFile = myFixture.configureByFile("FileWithOneFunction.kt") as KtFile

        val functionCallGraphs = FunctionCallGraphs(ktFile)

        assertEquals("[{fun foo(a: Int, b: String): Unit=[]}]", functionCallGraphs.toString())
    }

    fun testFileWithTwoFunctions() {
        val ktFile = myFixture.configureByFile("FileWithTwoFunctions.kt") as KtFile

        val functionCallGraphs = FunctionCallGraphs(ktFile)

        assertEquals("[{fun b(): Unit=[], fun a(): Unit=[fun b(): Unit]}]", functionCallGraphs.toString())
    }

    fun testFileWithClass() {
        val ktFile = myFixture.configureByFile("FileWithClass.kt") as KtFile

        val functionCallGraphs = FunctionCallGraphs(ktFile)

        assertEquals(
            "[{fun b(): Unit=[], fun a(): Unit=[fun b(): Unit]}]",
            functionCallGraphs.toString()
        )
    }

    fun testFileWithFunctionsOutsideClass() {
        val ktFile = myFixture.configureByFile("FunctionsOutsideClass.kt") as KtFile

        val functionCallGraphs = FunctionCallGraphs(ktFile)

        assertEquals(
            "[" +
                    "{fun b(): Unit=[], fun a(): Unit=[fun b(): Unit]}, " +
                    "{fun d(): Unit=[], fun e(): Unit=[fun d(): Unit]}" +
                    "]",
            functionCallGraphs.toString()
        )
    }

    fun testOuterFunctionCalledFromClass() {
        val ktFile = myFixture.configureByFile("OuterFunctionCalledFromClass.kt") as KtFile

        val functionCallGraphs = FunctionCallGraphs(ktFile)

        assertEquals(
            "[" +
                    "{fun b(): Unit=[], fun a(): Unit=[fun b(): Unit]}, " +
                    "{fun d(): Unit=[], fun e(): Unit=[fun d(): Unit]}" +
                    "]",
            functionCallGraphs.toString()
        )
    }

    fun testFileWithClassWithInitializer() {
        val ktFile = myFixture.configureByFile("FileWithClassWithInitializer.kt") as KtFile

        val functionCallGraphs = FunctionCallGraphs(ktFile)

        assertEquals(
            "[{fun a(): Unit=[], fun b(): Unit=[], fun c(): Unit=[]}]",
            functionCallGraphs.toString()
        )
    }

    fun testFileWithDSL() {
        val ktFile = myFixture.configureByFile("FileWithDSL.kt") as KtFile

        val functionCallGraphs = FunctionCallGraphs(ktFile)

        assertEquals(
            "[{" +
                    "fun a(): Unit=[fun b(p1: () -> Unit): Unit, fun c(p1: () -> Unit): Unit], " +
                    "fun b(p1: () -> Unit): Unit=[fun d(): Unit], " +
                    "fun c(p1: () -> Unit): Unit=[fun e(): Unit], " +
                    "fun d(): Unit=[], " +
                    "fun e(): Unit=[]" +
                    "}]",
            functionCallGraphs.toString()
        )
    }

    override fun getTestDataPath() = "src/test/testData"
}