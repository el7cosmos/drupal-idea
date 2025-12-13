package me.elabee.idea.drupal.template

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.ModificationTracker
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.util.ProcessingContext
import com.intellij.util.Processor
import com.intellij.util.indexing.FileBasedIndex
import fr.adrienbrault.idea.symfony2plugin.templating.TemplateLookupElement
import me.elabee.idea.drupal.indexing.DrupalIndexIds

class DrupalTwigCompletionProvider : CompletionProvider<CompletionParameters>() {
    companion object {
        val key: Key<CachedValue<Set<TemplateLookupElement>>> = Key("DRUPAL_TWIG_COMPONENT_CACHE")
    }

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet,
    ) {
        result.addAllElements(getLookupElementsFromIndex(parameters.position.project))
    }

    private fun getLookupElementsFromIndex(project: Project): Set<TemplateLookupElement> {
        val projectDir = project.guessProjectDir() ?: return emptySet()

        return CachedValuesManager.getManager(project).getCachedValue(
            project,
            key,
            {
                val index = FileBasedIndex.getInstance()
                val elements = mutableSetOf<TemplateLookupElement>()

                index.processAllKeys(
                    DrupalIndexIds.component,
                    { name ->
                        index.processFilesContainingAllKeys(
                            DrupalIndexIds.component,
                            setOf(name),
                            GlobalSearchScope.allScope(project),
                            null,
                            Processor {
                                val twig = it.parent.findChild("${it.parent.name}.twig") ?: return@Processor true
                                elements.add(TemplateLookupElement(name, twig, projectDir, true))
                                true
                            },
                        )
                        true
                    },
                    project,
                )

                CachedValueProvider.Result.create(
                    elements,
                    ModificationTracker { index.getIndexModificationStamp(DrupalIndexIds.component, project) },
                )
            },
            false,
        )
    }
}
