package com.github.marcopla99.cleancoderearranger

import com.github.marcopla99.cleancoderearranger.rearranger.GraphRearranger
import com.github.marcopla99.cleancoderearranger.util.getSignature
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.jetbrains.kotlin.psi.KtFile

class FunctionRearrangerTest: BasePlatformTestCase() {

    fun testEmptyFile() {
        val ktFile = myFixture.configureByFile("EmptyFile.kt") as KtFile
        val functionCallGraphs = FunctionCallGraphs(ktFile)

        val rearrangedFunctions = functionCallGraphs.graphs.map { graph ->
            GraphRearranger.rearrange(graph)
        }
        assertEquals("[]", rearrangedFunctions.map { functions -> functions.map { it.getSignature() } }.toString())
    }

    fun testFileWithSingleFunction() {
        val ktFile = myFixture.configureByFile("FileWithOneFunction.kt") as KtFile
        val functionCallGraphs = FunctionCallGraphs(ktFile)

        val rearrangedFunctions = functionCallGraphs.graphs.map { graph ->
            GraphRearranger.rearrange(graph)
        }
        assertEquals(
            "[[fun foo(a: Int, b: String): Unit]]",
            rearrangedFunctions.map { functions -> functions.map { it.getSignature() } }.toString()
        )
    }

    fun testFileWithDiamondCalls() {
        val ktFile = myFixture.configureByFile("FileWithDiamondCalls.kt") as KtFile
        val functionCallGraphs = FunctionCallGraphs(ktFile)

        val rearrangedFunctions = functionCallGraphs.graphs.map { graph ->
            GraphRearranger.rearrange(graph)
        }
        assertEquals(
            "[[fun a(): Unit, fun b(): Unit, fun c(): Unit, fun d(): Unit]]",
            rearrangedFunctions.map { functions -> functions.map { it.getSignature() } }.toString()
        )
    }

    fun testRearrangeWithTwoTopLevelFunctions() {
        val ktFile = myFixture.configureByFile("FileWithTwoFunctions.kt") as KtFile
        val functionCallGraphs = FunctionCallGraphs(ktFile)

        val rearrangedFunctions = functionCallGraphs.graphs.flatMap { graph ->
            GraphRearranger.rearrange(graph)
        }

        assertEquals("[fun a(): Unit, fun b(): Unit]", rearrangedFunctions.map { it.getSignature() }.toString())
    }

    fun testRearrangeWithTopLevelFunctionSeparatedByClass() {
        val ktFile = myFixture.configureByFile("FileWithTopLevelFunctionSeparatedByClass.kt") as KtFile
        val functionCallGraphs = FunctionCallGraphs(ktFile)

        val rearrangedFunctions = functionCallGraphs.graphs.map { graph ->
            GraphRearranger.rearrange(graph)
        }
        assertEquals(
            "[[fun a(): Unit, fun b(): Unit], [fun foo(): Unit, fun bar(): Unit]]",
            rearrangedFunctions.map { functions -> functions.map { it.getSignature() } }.toString()
        )
    }

    fun testRearrangeWithManyTopLevelFunctions() {
        val ktFile = myFixture.configureByFile("FileWithManyTopLevelFunctions.kt") as KtFile
        val functionCallGraphs = FunctionCallGraphs(ktFile)

        val rearrangedFunctions = functionCallGraphs.graphs.map { graph ->
            GraphRearranger.rearrange(graph)
        }
        assertEquals(
            "[[fun foo(): Unit, fun bar(): Unit, " +
                    "fun a(): Unit, fun b(): Unit, " +
                    "fun c(): Unit, fun d(): Unit, fun e(): Unit]]",
            rearrangedFunctions.map { functions -> functions.map { it.getSignature() } }.toString()
        )
    }

    override fun getTestDataPath() = "src/test/testData"
}