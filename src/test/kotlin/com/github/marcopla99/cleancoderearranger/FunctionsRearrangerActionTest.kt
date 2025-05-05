package com.github.marcopla99.cleancoderearranger

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.jetbrains.kotlin.psi.KtFile

class FunctionsRearrangerActionTest : BasePlatformTestCase() {
    fun testTopLevelFunctionWithMultipleRoots() {
        myFixture.configureByFile("input/TopLevelFunctionsWithMultipleRoots.kt") as KtFile
        myFixture.performEditorAction("com.github.marcopla99.cleancoderearranger.actions.FunctionsRearrangerAction")
        myFixture.checkResultByFile("output/TopLevelFunctionsWithMultipleRoots.kt")
    }

    fun testTopLevelFunctionWithClass() {
        myFixture.configureByFile("input/TopLevelFunctionWithClass.kt") as KtFile
        myFixture.performEditorAction("com.github.marcopla99.cleancoderearranger.actions.FunctionsRearrangerAction")
        myFixture.checkResultByFile("output/TopLevelFunctionWithClass.kt")
    }

    fun testClassWithInitializer() {
        myFixture.configureByFile("input/ClassWithInitializer.kt") as KtFile
        myFixture.performEditorAction("com.github.marcopla99.cleancoderearranger.actions.FunctionsRearrangerAction")
        myFixture.checkResultByFile("output/ClassWithInitializer.kt")
    }

    fun testFileWithManyTrailingWhitespaces() {
        myFixture.configureByFile("input/FileWithManyTrailingWhitespaces.kt") as KtFile
        myFixture.performEditorAction("com.github.marcopla99.cleancoderearranger.actions.FunctionsRearrangerAction")
        myFixture.checkResultByFile("output/FileWithManyTrailingWhitespaces.kt")
    }

    fun testFileWithNoTrailingWhitespaces() {
        myFixture.configureByFile("input/FileWithNoTrailingWhitespaces.kt") as KtFile
        myFixture.performEditorAction("com.github.marcopla99.cleancoderearranger.actions.FunctionsRearrangerAction")
        myFixture.checkResultByFile("output/FileWithNoTrailingWhitespaces.kt")
    }

    override fun getTestDataPath() = "src/test/testData"
}