package com.github.marcopla99.cleancoderearranger

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.jetbrains.kotlin.psi.KtFile

class FunctionsRearrangerActionTest : BasePlatformTestCase() {
    fun testFunctionsWithMultipleRoots() {
        checkFile("FunctionsWithMultipleRoots.kt")
    }

    fun testTopLevelFunctionWithClass() {
        checkFile("TopLevelFunctionWithClass.kt")
    }

    fun testClassWithInitializer() {
        checkFile("ClassWithInitializer.kt")
    }

    fun testInnerClass() {
        checkFile("InnerClass.kt")
    }

    fun testAlreadyArranged() {
        checkFile("AlreadyArranged.kt")
    }

    override fun getTestDataPath() = "src/test/testData"

    private fun checkFile(fileName: String) {
        myFixture.configureByFile("input/$fileName") as KtFile
        myFixture.performEditorAction("com.github.marcopla99.cleancoderearranger.FunctionsRearrangerAction")
        myFixture.checkResultByFile("output/$fileName")
    }
}