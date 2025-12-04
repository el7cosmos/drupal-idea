package me.elabee.idea.drupal.indexing

import com.intellij.util.indexing.ID
import me.elabee.idea.drupal.theme.component.DrupalComponentMetadata

object DrupalIndexIds {
    val Component = ID.create<String, DrupalComponentMetadata>("me.elabee.idea.drupal.theme.component")
}
