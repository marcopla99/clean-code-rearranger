package com.github.marcopla99.cleancoderearranger

import com.github.marcopla99.cleancoderearranger.graph.FunctionCallGraphs
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.jetbrains.kotlin.psi.KtFile

class FunctionCallGraphsTest: BasePlatformTestCase() {

    fun testEmpty() {
        val ktFile = myFixture.configureByFile("Empty.kt") as KtFile

        val functionCallGraphs = FunctionCallGraphs(ktFile)

        assertEquals("[]", functionCallGraphs.toString())
    }

    fun testOneFunction() {
        val ktFile = myFixture.configureByFile("OneFunction.kt") as KtFile

        val functionCallGraphs = FunctionCallGraphs(ktFile)

        assertEquals("[{foo=[]}]", functionCallGraphs.toString())
    }

    fun testTwoFunctions() {
        val ktFile = myFixture.configureByFile("TwoFunctions.kt") as KtFile

        val functionCallGraphs = FunctionCallGraphs(ktFile)

        assertEquals("[{b=[], a=[b]}]", functionCallGraphs.toString())
    }

    fun testWithinClass() {
        val ktFile = myFixture.configureByFile("WithinClass.kt") as KtFile

        val functionCallGraphs = FunctionCallGraphs(ktFile)

        assertEquals("[{b=[], a=[b]}]", functionCallGraphs.toString())
    }

    fun testOuterFunctionCalledFromClass() {
        val ktFile = myFixture.configureByFile("OuterFunctionCalledFromClass.kt") as KtFile

        val functionCallGraphs = FunctionCallGraphs(ktFile)

        assertEquals("[{b=[], a=[b]}, {d=[], e=[d]}]", functionCallGraphs.toString())
    }

    fun testClassWithInitializer() {
        val ktFile = myFixture.configureByFile("ClassWithInitializer.kt") as KtFile

        val functionCallGraphs = FunctionCallGraphs(ktFile)

        assertEquals("[{a=[], b=[], c=[]}]", functionCallGraphs.toString())
    }

    fun testFunctionsOutsideClass() {
        val ktFile = myFixture.configureByFile("FunctionsOutsideClass.kt") as KtFile

        val functionCallGraphs = FunctionCallGraphs(ktFile)

        assertEquals("[{b=[], a=[b]}, {d=[], e=[d]}]", functionCallGraphs.toString())
    }

    fun testDomainSpecificLanguage() {
        val ktFile = myFixture.configureByFile("DomainSpecificLanguage.kt") as KtFile

        val functionCallGraphs = FunctionCallGraphs(ktFile)

        assertEquals("[{a=[b, c], b=[d], c=[e], d=[], e=[]}]", functionCallGraphs.toString())
    }

    fun testFileWithOtherFile() {
        val ktFile = myFixture.configureByFile("FileReferencingAnotherFile.kt") as KtFile
        myFixture.createFile("AnotherFile.kt", "public inline fun shouldNotBeIncluded() {}")

        val functionCallGraphs = FunctionCallGraphs(ktFile)

        assertEquals("[{a=[]}]", functionCallGraphs.toString())
    }

    fun testFileWithOtherFileAndNestedCall() {
        val ktFile = myFixture.configureByFile("FileReferencingAnotherFileAndNestedCall.kt") as KtFile
        myFixture.createFile("OtherFile.kt", "public inline fun shouldNotBeIncluded(p: () -> Unit) {}")

        val functionCallGraphs = FunctionCallGraphs(ktFile)

        assertEquals("[{a=[b], b=[]}]", functionCallGraphs.toString())
    }

    fun testFunctionWithExpressionBody() {
        val ktFile = myFixture.configureByFile("FunctionWithExpressionBody.kt") as KtFile

        val functionCallGraphs = FunctionCallGraphs(ktFile)

        assertEquals("[{a=[b], b=[]}]", functionCallGraphs.toString())
    }

    override fun getTestDataPath() = "src/test/testData"
}