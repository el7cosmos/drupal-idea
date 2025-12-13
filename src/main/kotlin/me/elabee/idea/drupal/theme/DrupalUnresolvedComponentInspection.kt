package me.elabee.idea.drupal.theme

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.indexing.FileBasedIndex
import com.jetbrains.php.drupal.DrupalFeatureUsageProvider
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.visitors.PhpElementVisitor
import me.elabee.idea.drupal.indexing.DrupalIndexIds

class DrupalUnresolvedComponentInspection : LocalInspectionTool() {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        if (!DrupalFeatureUsageProvider().isEnabled(holder.project)) {
            return super.buildVisitor(holder, isOnTheFly)
        }

        return object : PhpElementVisitor() {
            override fun visitPhpStringLiteralExpression(expression: StringLiteralExpression) {
                if (!DrupalComponentReferenceProvider.PATTERN.accepts(expression)) return

                val element = expression.findElementAt(expression.valueRange.startOffset) ?: return

                val componentKey = expression.contents
                if (componentKey.isEmpty()) return

                // Check if the component exists
                val values = FileBasedIndex.getInstance().getValues(
                    DrupalIndexIds.component,
                    componentKey,
                    GlobalSearchScope.allScope(expression.project),
                )

                if (values.isEmpty()) {
                    holder.registerProblem(
                        element,
                        "Unresolved component '$componentKey'",
                        ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                    )
                }
            }
        }
    }
}
