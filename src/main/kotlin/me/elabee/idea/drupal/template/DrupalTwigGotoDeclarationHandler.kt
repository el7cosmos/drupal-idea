package me.elabee.idea.drupal.template

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.vfs.findPsiFile
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.Processor
import com.intellij.util.indexing.FileBasedIndex
import com.jetbrains.twig.elements.TwigElementTypes
import fr.adrienbrault.idea.symfony2plugin.templating.TwigPattern
import me.elabee.idea.drupal.indexing.DrupalIndexIds

class DrupalTwigGotoDeclarationHandler : GotoDeclarationHandler {
    override fun getGotoDeclarationTargets(sourceElement: PsiElement?, offset: Int, editor: Editor?): Array<PsiElement> {
        if (sourceElement == null) {
            return emptyArray()
        }

        val pattern = PlatformPatterns.or(
            TwigPattern.getTemplateFileReferenceTagPattern(),
            TwigPattern.getTagTernaryPattern(TwigElementTypes.EXTENDS_TAG),
            TwigPattern.getPrintBlockOrTagFunctionPattern("include", "source"),
            TwigPattern.getIncludeTagArrayPattern(),
            TwigPattern.getTagTernaryPattern(TwigElementTypes.INCLUDE_TAG),
        )
        if (!pattern.accepts(sourceElement)) {
            return emptyArray()
        }

        val split = sourceElement.text.split(":")
        if (split.count() != 2) {
            return emptyArray()
        }

        val (_, component) = split
        val elements = mutableListOf<PsiFile>()

        FileBasedIndex.getInstance().getFilesWithKey(
            DrupalIndexIds.Component,
            setOf(sourceElement.text),
            Processor {
                val twig = it.parent.findChild("$component.twig") ?: return@Processor true
                val psiFile = twig.findPsiFile(sourceElement.project) ?: return@Processor true
                elements.add(psiFile)
                true
            },
            GlobalSearchScope.allScope(sourceElement.project),
        )

        return elements.toTypedArray()
    }
}
