package com.github.marcopla99.cleancoderearranger.rearranger

import com.github.marcopla99.cleancoderearranger.Graph
import org.jetbrains.kotlin.psi.KtFunction

object GraphRearranger {
    fun rearrange(graph: Graph): List<KtFunction> {
        if (graph.isEmpty()) return emptyList()
        val result: LinkedHashSet<KtFunction> = LinkedHashSet()
        val root = graph.findRoot()
        result.add(root)
        val queue = ArrayDeque<KtFunction>()
        queue.add(root)
        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            result.add(current)
            graph[current]?.forEach { child ->
                queue.add(child)
            }
        }
        return result.toList()
    }
}

private fun Map<KtFunction, List<KtFunction>>.findRoot(): KtFunction {
    val allNodes = this.keys.toSet()
    val allChildren = this.values.flatten().toSet()
    val rootCandidates = allNodes - allChildren
    require(rootCandidates.size == 1) { "Invalid tree structure: found ${rootCandidates.size} root candidates instead of 1" }
    return rootCandidates.first()
}
