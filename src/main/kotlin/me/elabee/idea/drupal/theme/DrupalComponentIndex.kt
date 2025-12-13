package me.elabee.idea.drupal.theme

import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.util.io.FileUtilRt
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.indexing.DataIndexer
import com.intellij.util.indexing.FileBasedIndex
import com.intellij.util.indexing.FileBasedIndexExtension
import com.intellij.util.indexing.FileContent
import com.intellij.util.indexing.ID
import com.intellij.util.io.DataExternalizer
import com.intellij.util.io.EnumeratorStringDescriptor
import com.intellij.util.io.IOUtil
import com.intellij.util.io.KeyDescriptor
import com.jetbrains.php.drupal.DrupalFeatureUsageProvider
import me.elabee.idea.drupal.indexing.DrupalIndexIds
import me.elabee.idea.drupal.theme.component.DrupalComponentMetadata
import org.jetbrains.yaml.YAMLFileType
import org.jetbrains.yaml.psi.YAMLFile
import org.jetbrains.yaml.psi.YAMLMapping
import java.io.DataInput
import java.io.DataOutput
import java.util.Collections

class DrupalComponentIndex : FileBasedIndexExtension<String, DrupalComponentMetadata>() {
    override fun getName(): ID<String, DrupalComponentMetadata> = DrupalIndexIds.component

    override fun getInputFilter(): FileBasedIndex.InputFilter = DrupalComponentInputFilter

    override fun dependsOnFileContent(): Boolean = true

    override fun getIndexer(): DataIndexer<String, DrupalComponentMetadata, FileContent> = DrupalComponentDataIndexer

    override fun getKeyDescriptor(): KeyDescriptor<String> = EnumeratorStringDescriptor.INSTANCE

    override fun getValueExternalizer(): DataExternalizer<DrupalComponentMetadata> = DrupalComponentMetadataExternalizer

    override fun getVersion(): Int = 1

    object DrupalComponentInputFilter : FileBasedIndex.InputFilter {
        override fun acceptInput(file: VirtualFile): Boolean {
            if (file.fileType !is YAMLFileType) {
                return false
            }

            if (!FileUtilRt.extensionEquals(file.nameWithoutExtension, "component")) {
                return false
            }

            val name = FileUtilRt.getNameWithoutExtension(file.nameWithoutExtension)
            file.parent.findChild("$name.twig") ?: return false

            return true
        }
    }

    object DrupalComponentDataIndexer : DataIndexer<String, DrupalComponentMetadata, FileContent> {
        private const val MAX_PARENT_DEPTH = 5

        override fun map(inputData: FileContent): Map<String, DrupalComponentMetadata> {
            if (!DrupalFeatureUsageProvider().isEnabled(inputData.project)) {
                return emptyMap()
            }

            val components = inputData.file.findParentWithName("components") ?: return emptyMap()
            val infoFile = components.parent.findChild("${components.parent.name}.info.yml") ?: return emptyMap()

            val componentName = FileUtilRt.getNameWithoutExtension(inputData.file.nameWithoutExtension)
            val extensionName = FileUtilRt.getNameWithoutExtension(infoFile.nameWithoutExtension)

            try {
                val psiFile = inputData.psiFile as? YAMLFile ?: return emptyMap()
                val document = psiFile.documents.firstOrNull() ?: return emptyMap()
                val topValue = document.topLevelValue as? YAMLMapping ?: return emptyMap()

                val slots = extractSlots(topValue)
                val props = extractProps(topValue)

                return Collections.singletonMap("$extensionName:$componentName", DrupalComponentMetadata(slots, props))
            } catch (e: Exception) {
                thisLogger().error(e)
            }

            return emptyMap()
        }

        private fun VirtualFile.findParentWithName(name: String, depth: Int = 1): VirtualFile? {
            if (this.parent.name == name) {
                return this.parent
            }

            if (depth > MAX_PARENT_DEPTH) {
                return null
            }

            return this.parent.findParentWithName(name, depth + 1)
        }

        private fun extractSlots(mapping: YAMLMapping): Map<String, String> {
            val slotsMapping = mapping.getKeyValueByKey("slots")?.value as? YAMLMapping ?: return emptyMap()

            return slotsMapping.keyValues.mapNotNull { slotEntry ->
                val slotName = slotEntry.keyText
                val slotValue = slotEntry.value as? YAMLMapping
                val type = slotValue?.getKeyValueByKey("type")?.valueText ?: slotName

                slotName to type
            }.toMap()
        }

        private fun extractProps(mapping: YAMLMapping): Map<String, String> {
            val propsMapping = mapping.getKeyValueByKey("props")?.value as? YAMLMapping ?: return emptyMap()

            val propertiesMapping = propsMapping.getKeyValueByKey("properties")?.value as? YAMLMapping ?: return emptyMap()

            return propertiesMapping.keyValues.mapNotNull { propEntry ->
                val propName = propEntry.keyText
                val propValue = propEntry.value as? YAMLMapping
                val type = propValue?.getKeyValueByKey("type")?.valueText ?: propName

                propName to type
            }.toMap()
        }
    }

    object DrupalComponentMetadataExternalizer : DataExternalizer<DrupalComponentMetadata> {
        override fun save(out: DataOutput, value: DrupalComponentMetadata) {
            writeStringMap(out, value.slots)
            writeStringMap(out, value.props)
        }

        override fun read(input: DataInput): DrupalComponentMetadata {
            val slots = readStringMap(input)
            val props = readStringMap(input)
            return DrupalComponentMetadata(slots, props)
        }

        private fun writeStringMap(out: DataOutput, map: Map<String, String>) {
            out.writeInt(map.size)
            map.forEach { (key, value) ->
                IOUtil.writeUTF(out, key)
                IOUtil.writeUTF(out, value)
            }
        }

        private fun readStringMap(input: DataInput): Map<String, String> {
            val size = input.readInt()
            val map = mutableMapOf<String, String>()
            repeat(size) {
                val key = IOUtil.readUTF(input)
                val value = IOUtil.readUTF(input)
                map[key] = value
            }
            return map
        }
    }
}
