package com.github.marcopla99.cleancoderearranger

import com.github.marcopla99.cleancoderearranger.graph.FunctionCallGraphs
import com.github.marcopla99.cleancoderearranger.rearranger.GraphRearranger
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.jetbrains.kotlin.psi.KtFile

class FunctionRearrangerTest: BasePlatformTestCase() {

    fun testEmptyFile() {
        val ktFile = myFixture.configureByFile("EmptyFile.kt") as KtFile
        val functionCallGraphs = FunctionCallGraphs(ktFile)

        val rearrangedFunctions = functionCallGraphs.graphs.values.map { graph ->
            GraphRearranger.rearrange(graph)
        }
        assertEquals("[]", rearrangedFunctions.map { functions -> functions.map { it.name } }.toString())
    }

    fun testFileWithSingleFunction() {
        val ktFile = myFixture.configureByFile("FileWithOneFunction.kt") as KtFile
        val functionCallGraphs = FunctionCallGraphs(ktFile)

        val rearrangedFunctions = functionCallGraphs.graphs.values.map { graph ->
            GraphRearranger.rearrange(graph)
        }
        assertEquals(
            "[[foo]]",
            rearrangedFunctions.map { functions -> functions.map { it.name } }.toString()
        )
    }

    fun testFileWithDiamondCalls() {
        val ktFile = myFixture.configureByFile("FileWithDiamondCalls.kt") as KtFile
        val functionCallGraphs = FunctionCallGraphs(ktFile)

        val rearrangedFunctions = functionCallGraphs.graphs.values.map { graph ->
            GraphRearranger.rearrange(graph)
        }
        assertEquals(
            "[[a, b, c, d]]",
            rearrangedFunctions.map { functions -> functions.map { it.name } }.toString()
        )
    }

    fun testRearrangeWithTwoTopLevelFunctions() {
        val ktFile = myFixture.configureByFile("FileWithTwoFunctions.kt") as KtFile
        val functionCallGraphs = FunctionCallGraphs(ktFile)

        val rearrangedFunctions = functionCallGraphs.graphs.values.flatMap { graph ->
            GraphRearranger.rearrange(graph)
        }

        assertEquals("[a, b]", rearrangedFunctions.map { it.name }.toString())
    }

    fun testRearrangeWithTopLevelFunctionSeparatedByClass() {
        val ktFile = myFixture.configureByFile("FileWithTopLevelFunctionSeparatedByClass.kt") as KtFile
        val functionCallGraphs = FunctionCallGraphs(ktFile)

        val rearrangedFunctions = functionCallGraphs.graphs.values.map { graph ->
            GraphRearranger.rearrange(graph)
        }
        assertEquals(
            "[[a, b], [foo, bar]]",
            rearrangedFunctions.map { functions -> functions.map { it.name } }.toString()
        )
    }

    fun testRearrangeWithManyTopLevelFunctions() {
        val ktFile = myFixture.configureByFile("FileWithManyTopLevelFunctions.kt") as KtFile
        val functionCallGraphs = FunctionCallGraphs(ktFile)

        val rearrangedFunctions = functionCallGraphs.graphs.values.map { graph ->
            GraphRearranger.rearrange(graph)
        }
        assertEquals(
            "[[foo, bar, a, b, c, d, e]]",
            rearrangedFunctions.map { functions -> functions.map { it.name } }.toString()
        )
    }

    fun testRearrangeClassWithInitializer() {
        val ktFile = myFixture.configureByFile("FileWithClassWithInitializer.kt") as KtFile
        val functionCallGraphs = FunctionCallGraphs(ktFile)

        val rearrangedFunctions = functionCallGraphs.graphs.values.map { graph ->
            GraphRearranger.rearrange(graph)
        }
        assertEquals(
            "[[a, b, c]]",
            rearrangedFunctions.map { functions -> functions.map { it.name } }.toString()
        )
    }

    override fun getTestDataPath() = "src/test/testData"
}