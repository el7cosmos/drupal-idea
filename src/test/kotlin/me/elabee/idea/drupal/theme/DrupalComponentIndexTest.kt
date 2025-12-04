package me.elabee.idea.drupal.theme

import com.intellij.util.indexing.FileBasedIndex
import me.elabee.idea.drupal.indexing.DrupalIndexIds

class DrupalComponentIndexTest : DrupalComponentTestCase() {

    fun `test index accepts valid component`() {
        val fileBasedIndex = FileBasedIndex.getInstance()
        val keys = fileBasedIndex.getAllKeys(DrupalIndexIds.Component, project)
        assertNotEmpty(keys)
        assertContainsElements(keys, COMPONENTS)
    }
}
