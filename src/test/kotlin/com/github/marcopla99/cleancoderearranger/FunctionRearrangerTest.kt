package com.github.marcopla99.cleancoderearranger

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.jetbrains.kotlin.psi.KtFile

class FunctionRearrangerTest: BasePlatformTestCase() {

    fun testEmpty() {
        val ktFile = myFixture.configureByFile("Empty.kt") as KtFile
        val functionCallGraphs = FunctionCallGraphs(ktFile)

        val rearrangedFunctions = functionCallGraphs.graphs.values.map { graph ->
            GraphRearranger.rearrange(graph)
        }
        assertEquals("[]", rearrangedFunctions.map { functions -> functions.map { it.name } }.toString())
    }

    fun testOneFunction() {
        val ktFile = myFixture.configureByFile("OneFunction.kt") as KtFile
        val functionCallGraphs = FunctionCallGraphs(ktFile)

        val rearrangedFunctions = functionCallGraphs.graphs.values.map { graph ->
            GraphRearranger.rearrange(graph)
        }
        assertEquals(
            "[[foo]]",
            rearrangedFunctions.map { functions -> functions.map { it.name } }.toString()
        )
    }

    fun testFunctionsWithDiamondCalls() {
        val ktFile = myFixture.configureByFile("FunctionsWithDiamondCalls.kt") as KtFile
        val functionCallGraphs = FunctionCallGraphs(ktFile)

        val rearrangedFunctions = functionCallGraphs.graphs.values.map { graph ->
            GraphRearranger.rearrange(graph)
        }
        assertEquals(
            "[[a, b, c, d]]",
            rearrangedFunctions.map { functions -> functions.map { it.name } }.toString()
        )
    }

    fun testTwoFunctions() {
        val ktFile = myFixture.configureByFile("TwoFunctions.kt") as KtFile
        val functionCallGraphs = FunctionCallGraphs(ktFile)

        val rearrangedFunctions = functionCallGraphs.graphs.values.flatMap { graph ->
            GraphRearranger.rearrange(graph)
        }

        assertEquals("[a, b]", rearrangedFunctions.map { it.name }.toString())
    }

    fun testTopLevelFunctionsSeparatedByClass() {
        val ktFile = myFixture.configureByFile("TopLevelFunctionsSeparatedByClass.kt") as KtFile
        val functionCallGraphs = FunctionCallGraphs(ktFile)

        val rearrangedFunctions = functionCallGraphs.graphs.values.map { graph ->
            GraphRearranger.rearrange(graph)
        }
        assertEquals(
            "[[a, b], [foo, bar]]",
            rearrangedFunctions.map { functions -> functions.map { it.name } }.toString()
        )
    }

    fun testManyTopLevelFunctions() {
        val ktFile = myFixture.configureByFile("ManyTopLevelFunctions.kt") as KtFile
        val functionCallGraphs = FunctionCallGraphs(ktFile)

        val rearrangedFunctions = functionCallGraphs.graphs.values.map { graph ->
            GraphRearranger.rearrange(graph)
        }
        assertEquals(
            "[[foo, bar, a, b, c, d, e]]",
            rearrangedFunctions.map { functions -> functions.map { it.name } }.toString()
        )
    }

    fun testClassWithInitializer() {
        val ktFile = myFixture.configureByFile("ClassWithInitializer.kt") as KtFile
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