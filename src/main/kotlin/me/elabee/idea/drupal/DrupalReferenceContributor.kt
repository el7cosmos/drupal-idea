package me.elabee.idea.drupal

import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceRegistrar
import me.elabee.idea.drupal.theme.DrupalComponentReferenceProvider

class DrupalReferenceContributor : PsiReferenceContributor() {
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(
            DrupalComponentReferenceProvider.PATTERN,
            DrupalComponentReferenceProvider(),
        )
    }
}
