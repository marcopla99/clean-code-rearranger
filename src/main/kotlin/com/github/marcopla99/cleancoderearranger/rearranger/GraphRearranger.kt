package com.github.marcopla99.cleancoderearranger.rearranger

import com.github.marcopla99.cleancoderearranger.Graph
import org.jetbrains.kotlin.psi.KtFunction

object GraphRearranger {
    fun rearrange(graph: Graph): List<KtFunction> {
        if (graph.isEmpty()) return emptyList()
        val result: LinkedHashSet<KtFunction> = LinkedHashSet()
        val roots = graph.findRoots()
        roots.forEach { root ->
            result.add(root)
            val queue = ArrayDeque<KtFunction>()
            queue.add(root)
            while (queue.isNotEmpty()) {
                val current = queue.removeFirst()
                if (result.contains(current)) result.remove(current)
                result.add(current)
                graph[current]?.forEach { child ->
                    queue.add(child)
                }
            }
        }
        return result.toList()
    }
}

private fun Map<KtFunction, List<KtFunction>>.findRoots(): List<KtFunction> {
    val allNodes = this.keys.toSet()
    val allChildren = this.values.flatten().toSet()
    val roots = allNodes - allChildren
    return roots.toList()
}
