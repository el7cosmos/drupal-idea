package me.elabee.idea.drupal.core.extension

import com.intellij.openapi.util.Key
import com.intellij.openapi.util.io.FileUtilRt
import com.intellij.util.indexing.DataIndexer
import com.intellij.util.indexing.FileBasedIndex
import com.intellij.util.indexing.FileBasedIndexExtension
import com.intellij.util.indexing.FileContent
import com.intellij.util.indexing.ID
import com.intellij.util.io.DataExternalizer
import com.intellij.util.io.EnumeratorStringDescriptor
import com.intellij.util.io.KeyDescriptor
import com.jetbrains.php.drupal.DrupalFeatureUsageProvider
import me.elabee.idea.drupal.indexing.DrupalIndexIds
import org.jetbrains.yaml.YAMLFileType
import org.jetbrains.yaml.psi.YAMLDocument
import org.jetbrains.yaml.psi.YAMLFile
import org.jetbrains.yaml.psi.YAMLMapping
import org.jetbrains.yaml.psi.YamlPsiElementVisitor
import java.util.Collections

class DrupalExtensionIndex : FileBasedIndexExtension<String, String>() {
    override fun getName(): ID<String, String> = DrupalIndexIds.extension

    override fun getInputFilter(): FileBasedIndex.InputFilter =
        FileBasedIndex.InputFilter { it.fileType is YAMLFileType && FileUtilRt.extensionEquals(it.nameWithoutExtension, "info") }

    override fun dependsOnFileContent(): Boolean = true

    override fun getIndexer(): DataIndexer<String, String, FileContent> = DrupalExtensionIndexDataIndexer

    override fun getKeyDescriptor(): KeyDescriptor<String> = EnumeratorStringDescriptor.INSTANCE

    override fun getValueExternalizer(): DataExternalizer<String> = EnumeratorStringDescriptor.INSTANCE

    override fun getVersion(): Int = 1

    private object DrupalExtensionIndexDataIndexer : DataIndexer<String, String, FileContent> {
        val userDataKey = Key<String>("me.elabee.idea.drupal.core.extension.type")

        override fun map(inputData: FileContent): Map<String?, String?> {
            if (!DrupalFeatureUsageProvider().isEnabled(inputData.project)) {
                return emptyMap()
            }

            val psiFile = inputData.psiFile as YAMLFile

            psiFile.documents.forEach { it.accept(DrupalExtensionIndexElementVisitor) }
            val type = psiFile.getUserData(userDataKey) ?: return emptyMap()
            return Collections.singletonMap(type, FileUtilRt.getNameWithoutExtension(inputData.file.nameWithoutExtension))
        }
    }

    private object DrupalExtensionIndexElementVisitor : YamlPsiElementVisitor() {
        override fun visitDocument(document: YAMLDocument) {
            document.topLevelValue?.accept(this)
        }

        override fun visitMapping(mapping: YAMLMapping) {
            val extensionPackage = mapping.getKeyValueByKey("package")?.valueText
            if (extensionPackage != null && arrayOf("Testing", "test").any { it.equals(extensionPackage, true) }) {
                return
            }

            val hidden = mapping.getKeyValueByKey("hidden")?.valueText
            if (hidden != null && arrayOf("true", "on", "yes").any { it.equals(hidden, true) }) {
                return
            }

            val type = mapping.getKeyValueByKey("type") ?: return
            mapping.containingFile.putUserData(DrupalExtensionIndexDataIndexer.userDataKey, type.valueText)
        }
    }
}
