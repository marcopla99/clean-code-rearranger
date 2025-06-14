package com.github.marcopla99.cleancoderearranger

import org.jetbrains.kotlin.psi.KtFunction

object GraphRearranger {
    fun rearrange(graph: Graph): List<KtFunction> {
        if (graph.isEmpty()) return emptyList()
        val result: LinkedHashSet<KtFunction> = LinkedHashSet()
        val roots = graph.findRoots()
        roots.forEach { root ->
            graph.searchBreadthFirstStartingFrom(root) { visitedNode -> result.addOrReplace(visitedNode) }
        }
        return result.toList()
    }
}

private fun Graph.findRoots(): List<KtFunction> {
    val allNodes = this.keys.toSet()
    val allChildren = this.values.flatten().toSet()
    val roots = allNodes - allChildren
    return roots.toList()
}

private fun Graph.searchBreadthFirstStartingFrom(
    ktFunction: KtFunction,
    onNodeVisited: (KtFunction) -> Unit
) {
    val queue = ArrayDeque<KtFunction>()
    queue.add(ktFunction)
    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()
        onNodeVisited(current)
        this[current]?.forEach { child -> queue.add(child) }
    }
}

private fun LinkedHashSet<KtFunction>.addOrReplace(current: KtFunction) {
    if (contains(current)) remove(current)
    add(current)
}
