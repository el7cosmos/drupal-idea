package me.elabee.idea.drupal.template

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.ModificationTracker
import com.intellij.openapi.util.io.FileUtilRt
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.util.Processor
import com.intellij.util.indexing.FileBasedIndex
import fr.adrienbrault.idea.symfony2plugin.templating.path.TwigPath
import me.elabee.idea.drupal.indexing.DrupalIndexIds

class DrupalTwigNamespaceCachedValueProvider(private val project: Project) : CachedValueProvider<Collection<TwigPath>> {
    companion object {
        val key = Key<CachedValue<Collection<TwigPath>>>("DRUPAL_TWIG_TEMPLATES_CACHE")
    }

    override fun compute(): CachedValueProvider.Result<Collection<TwigPath>> {
        return CachedValueProvider.Result.create(
            getNamespaces(),
            ModificationTracker { FileBasedIndex.getInstance().getIndexModificationStamp(DrupalIndexIds.extension, project) },
        )
    }

    private fun getNamespaces(): Collection<TwigPath> {
        val namespaces = mutableListOf<TwigPath>()

        val index = FileBasedIndex.getInstance()
        index.processAllKeys(
            DrupalIndexIds.extension,
            { key ->
                index.processFilesContainingAllKeys(
                    DrupalIndexIds.extension,
                    listOf(key),
                    GlobalSearchScope.allScope(project),
                    null,
                    Processor {
                        val templates = it.parent.findChild("templates") ?: return@Processor true
                        namespaces.add(TwigPath(templates.path, it.extensionName))
                        true
                    },
                )
                true
            },
            project,
        )

        return namespaces
    }

    private val VirtualFile.extensionName: String
        get() = FileUtilRt.getNameWithoutExtension(this.nameWithoutExtension)

}
