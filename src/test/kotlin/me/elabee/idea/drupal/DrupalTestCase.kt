package me.elabee.idea.drupal

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.jetbrains.php.drupal.DrupalVersion
import com.jetbrains.php.drupal.settings.DrupalDataService

abstract class DrupalTestCase(protected val enableDrupalService: Boolean = true) : BasePlatformTestCase() {

    override fun setUp() {
        super.setUp()

        if (enableDrupalService) {
            DrupalDataService.getInstance(project).enable()
        }
    }

    override fun getTestDataPath(): String {
        return "src/test/testData/"
    }

    fun DrupalDataService.enable() {
        state = DrupalDataService.State(
            true,
            "${testDataPath}drupal",
            DrupalVersion.NINE.number,
            false,
            false,
        )
    }
}
