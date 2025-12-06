package me.elabee.idea.drupal.theme

import com.jetbrains.php.lang.PhpFileType
import com.jetbrains.twig.TwigFile

class DrupalComponentReferenceProviderTest : DrupalComponentTestCase() {
    fun `test completion`() {
        myFixture.configureByText(PhpFileType.INSTANCE, "<?php ['#component' => '<caret>'];")
        myFixture.completeBasic()
        assertNotEmpty(myFixture.lookupElementStrings)
        assertContainsElements(myFixture.lookupElementStrings!!, COMPONENTS)
    }

    fun `test reference`() {
        myFixture.configureByText(PhpFileType.INSTANCE, "<?php ['#component' => 'sdc_test:array-to-object<caret>'];")
        val referenceAtCaret = myFixture.getReferenceAtCaretPositionWithAssertion()
        val file = assertInstanceOf(referenceAtCaret.resolve(), TwigFile::class.java)
        assertEquals("array-to-object.twig", file.name)
    }
}
