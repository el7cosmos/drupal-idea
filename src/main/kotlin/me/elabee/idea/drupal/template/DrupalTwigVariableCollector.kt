package me.elabee.idea.drupal.template

import com.intellij.util.indexing.FileBasedIndex
import fr.adrienbrault.idea.symfony2plugin.templating.variable.TwigFileVariableCollector
import fr.adrienbrault.idea.symfony2plugin.templating.variable.TwigFileVariableCollectorParameter
import me.elabee.idea.drupal.indexing.DrupalIndexIds

class DrupalTwigVariableCollector : TwigFileVariableCollector {
    override fun collect(parameter: TwigFileVariableCollectorParameter, variables: MutableMap<String, Set<String>>) {
        val twigFile = parameter.element.containingFile
        val component = (twigFile.parent ?: return).findFile("${twigFile.virtualFile.nameWithoutExtension}.component.yml") ?: return
        FileBasedIndex.getInstance().getFileData(DrupalIndexIds.Component, component.virtualFile, parameter.project).forEach {
            it.value.slots.forEach { (name, type) -> variables[name] = setOf(type) }
            it.value.props.forEach { (name, type) -> variables[name] = setOf(type) }
        }
    }
}
