package me.elabee.idea.drupal.theme

import com.intellij.testFramework.PlatformTestUtil
import com.jetbrains.php.lang.PhpFileType
import me.elabee.idea.drupal.DrupalTestCase

class DrupalUnresolvedComponentInspectionTest : DrupalTestCase() {
    fun `test unresolved component`() {
        PlatformTestUtil.dispatchAllEventsInIdeEventQueue()
        myFixture.enableInspections(DrupalUnresolvedComponentInspection::class.java)
        myFixture.configureByText(
            PhpFileType.INSTANCE,
            "<?php ['#component' => '<warning descr=\"Unresolved component 'foo'\">foo</warning>'];",
        )
        myFixture.checkHighlighting()
    }
}
