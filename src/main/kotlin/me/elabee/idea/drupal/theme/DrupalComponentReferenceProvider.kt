package me.elabee.idea.drupal.theme

import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.icons.AllIcons
import com.intellij.openapi.util.TextRange
import com.intellij.patterns.PatternCondition
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiPolyVariantReference
import com.intellij.psi.PsiPolyVariantReferenceBase
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceProvider
import com.intellij.psi.ResolveResult
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.ProcessingContext
import com.intellij.util.indexing.FileBasedIndex
import com.jetbrains.php.lang.parser.PhpElementTypes
import com.jetbrains.php.lang.patterns.PhpPatterns
import com.jetbrains.php.lang.psi.elements.ArrayHashElement
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import me.elabee.idea.drupal.indexing.DrupalIndexIds

class DrupalComponentReferenceProvider : PsiReferenceProvider() {
    companion object {
        val PATTERN = PhpPatterns.phpLiteralExpression().withParent(
            PhpPatterns.phpElement().withElementType(PhpElementTypes.ARRAY_VALUE).withParent(
                PhpPatterns.phpElement().withElementType(PhpElementTypes.HASH_ARRAY_ELEMENT).with(HashKey("#component")),
            ),
        )
    }

    override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<out PsiReference?> {
        val stringLiteral = element as? StringLiteralExpression ?: return PsiReference.EMPTY_ARRAY

        if (stringLiteral.contents.isEmpty()) {
            return PsiReference.EMPTY_ARRAY
        }

        // Create reference with text range (excluding quotes)
        val range = TextRange(1, stringLiteral.textLength - 1)
        return arrayOf(DrupalComponentReference(stringLiteral, range))
    }

    private class DrupalComponentReference(element: StringLiteralExpression, textRange: TextRange) :
        PsiPolyVariantReferenceBase<StringLiteralExpression>(element, textRange), PsiPolyVariantReference {

        private val componentKey: String get() = element.contents

        override fun multiResolve(incompleteCode: Boolean): Array<ResolveResult> {
            val project = element.project
            val index = FileBasedIndex.getInstance()

            // Find files that contain this component key
            val files = mutableListOf<PsiFile>()
            index.processValues(
                DrupalIndexIds.Component,
                componentKey,
                null,
                { file, _ ->
                    PsiManager.getInstance(project).findFile(file)?.let { psiFile ->
                        files.add(psiFile)
                    }
                    true
                },
                GlobalSearchScope.allScope(project),
            )

            return files.map { PsiElementResolveResult(it) }.toTypedArray()
        }

        override fun getVariants(): Array<Any> {
            val project = element.project

            val variants = mutableListOf<LookupElementBuilder>()

            FileBasedIndex.getInstance().processAllKeys(
                DrupalIndexIds.Component,
                { key ->
                    variants.add(
                        LookupElementBuilder.create(key).withIcon(AllIcons.Nodes.Tag).bold(),
                    )

                    true  // Continue processing
                },
                project,
            )

            return variants.toTypedArray()
        }
    }

    private class HashKey(private val keyName: String) : PatternCondition<ArrayHashElement>("hashKey") {
        override fun accepts(element: ArrayHashElement, context: ProcessingContext?): Boolean {
            return (element.key as? StringLiteralExpression)?.contents == keyName
        }
    }
}
