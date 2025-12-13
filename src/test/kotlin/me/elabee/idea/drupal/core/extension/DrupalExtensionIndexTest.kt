package me.elabee.idea.drupal.core.extension

import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.indexing.FileBasedIndex
import com.jetbrains.php.drupal.settings.DrupalDataService
import me.elabee.idea.drupal.DrupalTestCase
import me.elabee.idea.drupal.indexing.DrupalIndexIds

class DrupalExtensionIndexTest : DrupalTestCase(false) {
    override fun setUp() {
        super.setUp()

    }

    fun `test with service disabled`() {
        myFixture.copyDirectoryToProject(
            "drupal/core/modules/system/tests/modules",
            "drupal/core/modules/system/tests/modules",
        )

        val fileBasedIndex = FileBasedIndex.getInstance()
        val values = fileBasedIndex.getValues(DrupalIndexIds.extension, "module", GlobalSearchScope.allScope(project))
        assertEmpty(values)
    }

    fun `test with service enabled`() {
        DrupalDataService.getInstance(project).enable()

        myFixture.copyDirectoryToProject(
            "drupal/core/modules/system/tests/modules",
            "drupal/core/modules/system/tests/modules",
        )

        val fileBasedIndex = FileBasedIndex.getInstance()
        val values = fileBasedIndex.getValues(DrupalIndexIds.extension, "module", GlobalSearchScope.allScope(project))
        assertNotEmpty(values)
        assertEquals(listOf("config_mapping_test", "experimental_module_requirements_test", "experimental_module_test"), values)
    }
}
