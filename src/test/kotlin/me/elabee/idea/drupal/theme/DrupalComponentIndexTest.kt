package me.elabee.idea.drupal.theme

import com.intellij.util.indexing.FileBasedIndex
import me.elabee.idea.drupal.DrupalTestCase
import me.elabee.idea.drupal.indexing.DrupalIndexIds

class DrupalComponentIndexTest : DrupalTestCase() {

    override fun setUp() {
        super.setUp()

        myFixture.copyDirectoryToProject(
            "drupal/core/modules/system/tests/modules/sdc_test",
            "drupal/core/modules/system/tests/modules/sdc_test",
        )
    }

    fun `test index accepts valid component`() {
        val fileBasedIndex = FileBasedIndex.getInstance()
        val keys = fileBasedIndex.getAllKeys(DrupalIndexIds.Component, project)
        assertNotEmpty(keys)
        assertSize(5, keys)
    }
}
