package me.elabee.idea.drupal.template

import com.intellij.codeInspection.InspectionSuppressor
import com.intellij.codeInspection.SuppressQuickFix
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import com.intellij.util.indexing.FileBasedIndex
import com.jetbrains.twig.elements.TwigElementTypes
import fr.adrienbrault.idea.symfony2plugin.templating.TwigPattern
import me.elabee.idea.drupal.indexing.DrupalIndexIds

class DrupalTwigInspectionSuppressor : InspectionSuppressor {
    override fun isSuppressedFor(element: PsiElement, toolId: String): Boolean {
        if (toolId != "TwigTemplateMissingInspection") {
            return false
        }

        val pattern = PlatformPatterns.or(
            TwigPattern.getTemplateFileReferenceTagPattern(),
            TwigPattern.getTagTernaryPattern(TwigElementTypes.EXTENDS_TAG),
            TwigPattern.getPrintBlockOrTagFunctionPattern("include", "source"),
            TwigPattern.getIncludeTagArrayPattern(),
            TwigPattern.getTagTernaryPattern(TwigElementTypes.INCLUDE_TAG),
        )
        if (!pattern.accepts(element)) {
            return false
        }

        return FileBasedIndex.getInstance().getAllKeys(DrupalIndexIds.Component, element.project).contains(element.text)
    }

    override fun getSuppressActions(element: PsiElement?, toolId: String): Array<SuppressQuickFix> {
        return SuppressQuickFix.EMPTY_ARRAY
    }
}
