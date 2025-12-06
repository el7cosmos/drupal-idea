package me.elabee.idea.drupal.theme

import me.elabee.idea.drupal.DrupalTestCase

abstract class DrupalComponentTestCase : DrupalTestCase() {
    companion object {
        val COMPONENTS = listOf(
            "sdc_test:array-to-object",
            "sdc_test:my-banner",
            "sdc_test:my-button",
            "sdc_test:my-cta",
            "sdc_test:no-props",
        )
    }

    override fun setUp() {
        super.setUp()

        myFixture.copyDirectoryToProject(
            "drupal/core/modules/system/tests/modules/sdc_test",
            "drupal/core/modules/system/tests/modules/sdc_test",
        )
    }
}
