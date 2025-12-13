package me.elabee.idea.drupal.template

import com.intellij.psi.util.CachedValuesManager
import fr.adrienbrault.idea.symfony2plugin.extension.TwigNamespaceExtension
import fr.adrienbrault.idea.symfony2plugin.extension.TwigNamespaceExtensionParameter
import fr.adrienbrault.idea.symfony2plugin.templating.path.TwigPath

class DrupalTwigNamespaceExtension : TwigNamespaceExtension {
    override fun getNamespaces(parameter: TwigNamespaceExtensionParameter): Collection<TwigPath> {
        return CachedValuesManager.getManager(parameter.project).getCachedValue(
            parameter.project,
            DrupalTwigNamespaceCachedValueProvider.key,
            DrupalTwigNamespaceCachedValueProvider(parameter.project),
            false,
        )
    }
}
