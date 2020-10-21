/*
 * @author Adit Shah
 *
 * This file contains the DirectedGraph code, and the Vertex code.
 */

import java.io.Serializable
import java.util.*
import kotlin.math.max


/*
 * This vertex class represents a wikipedia page for the purposes of this project. The label is the URL of the page.
 * The neighbors LinkedList has been converted to a HashSet of Strings. HashSet because it was duplicate counting links
 * when the crawler went through the same link twice, and of Strings because a list of Vertex was causing a StackOverFlow
 * error when serialization was attempted due to circular references between pages that link to each other.
 */
data class Vertex(var label: String) : Comparable<Vertex>, Serializable {
    override fun compareTo(other: Vertex): Int {
        return neighbors.size - other.neighbors.size
    }

    //var neighbors = LinkedList<Vertex>()
    var neighbors = HashSet<String>()
}

/*
 * This class is a directed graph, where each node points to all of the pages that link to it. The code is modified from
 * what we did earlier this year, and was converted to Kotlin.
 */
data class DirectedGraph(val vertices: HashMap<String, Vertex> = HashMap()) {
    fun addVertex(label: String) {
        vertices[label] = Vertex(label)
    }

    fun removeVertex(label: String) {
        for (v in vertices[label]!!.neighbors) {
            vertices[v]!!.neighbors.remove(label)
        }
        vertices.remove(label)
    }

    fun addEdge(label1: String, label2: String) {
        if (!vertices.containsKey(label1)) {
            addVertex(label1)
        }
        if (!vertices.containsKey(label2)) {
            addVertex(label2)
        }
//        vertices[label2]!!.neighbors.add(vertices[label1]!!)
        vertices[label2]!!.neighbors.add(label1)
    }

    fun removeEdge(label1: String, label2: String) {
//        vertices[label2]!!.neighbors.remove(vertices[label1])
        vertices[label2]!!.neighbors.remove(label1)
    }


    fun printGraphShort() {
        var longest = 7
        for (str in vertices.keys) {
            longest = max(longest, str.length + 1)
        }

        var line = "Vertex "
        for (i in 7 until longest)
            line += " "
        val leftLength = line.length
        line += "| Adjacent Vertices"
        println(line)

        for (i in 0 until line.length) {
            print("-")
        }
        println()

        for (strLoop in vertices.keys) {
            var str = strLoop
            val v1 = vertices[str]
            if (v1!!.neighbors.size != 0) {
                print("$str| ")
                for (i in str.length until leftLength) {
                    str += " "
                }
                println(v1!!.neighbors.size)
            }
        }
    }
//    fun printGraph() {
//        var longest = 7
//        for (str in vertices.keys) {
//            longest = max(longest, str.length + 1)
//        }
//
//        var line = "Vertex "
//        for (i in 7 until longest)
//            line += " "
//        val leftLength = line.length
//        line += "| Adjacent Vertices"
//        println(line)
//
//        for (i in 0 until line.length) {
//            print("-")
//        }
//        println()
//
//        for (strLoop in vertices.keys) {
//            if(vertices[strLoop]!!.neighbors.size != 0) {
//                var str = strLoop
//                val v1 = vertices[str]
//                for (i in str.length until leftLength) {
//                    str += " "
//                }
//                print("$str| ")
//
//                for (i in 0 until v1!!.neighbors.size - 1) {
////                    print(v1.neighbors[i].label + ", ")
//                    print(v1.neighbors[i] + ", ")
//                }
//
//                if (!v1.neighbors.isEmpty()) {
////                    print(v1.neighbors[v1.neighbors.size - 1].label)
//                    print(v1.neighbors[v1.neighbors.size - 1])
//                }
//
//                println()
//            }
//        }
//    }

    fun depthFirstSearch(label: String) {
        val visited = HashSet<String>()
        val v = vertices[label]
        dfs(v!!, visited)
    }

    fun dfs(v: Vertex, visited: HashSet<String>) {
        println(v.label)
        visited.add(v.label)
        for (neighbor in v.neighbors) {
            if (!visited.contains(neighbor))
                dfs(vertices[neighbor]!!, visited)
        }
    }

    fun breadthFirstSearch(label: String) {
        val visited = HashSet<String>()
        val queue = LinkedList<Vertex>()
        queue.add(vertices[label]!!)
        while (!queue.isEmpty()) {
            val v = queue.remove()
            if (!visited.contains(v.label)) {
                println(v.label)
                visited.add(v.label)
                //                for(Vertex neighbor : v.neighbors){
                //                    queue.add(neighbor);
                //                }
                //
                for (neighbor in v.neighbors) {
                    queue.add(vertices[neighbor]!!)
                }
            }
        }
    }

    fun minimumDistance(label1: String, label2: String): Int {
        if (label1 == label2) {
            return 0
        }
        val visited = HashSet<String>()
        val queue = LinkedList<Vertex>()
        queue.add(vertices[label1]!!)
        val distance = HashMap<String, Int>()
        val path = HashMap<String, String>()
        distance[label1] = 0
        path[label1] = label1
        while (!queue.isEmpty()) {
            val v = queue.remove()
            if (!visited.contains(v.label)) {
                visited.add(v.label)
                if (v.label == label2) {
                    println(path[v.label])
                    return distance[v.label]!!
                }
                for (neighbor in v.neighbors) {
                    queue.add(vertices[neighbor]!!)
                    if (!distance.containsKey(neighbor)) {
                        distance[neighbor] = distance[v.label]!! + 1
                        path[neighbor] = path[v.label] + " " + neighbor
                    }
                }
            }
        }
        return 0
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val graph = DirectedGraph()

            graph.addVertex("London")
            graph.addVertex("New York")
            graph.addVertex("San Francisco")
            graph.addVertex("Chicago")
//            graph.printGraph()
            println()

            // Vertex        | Adjacent Vertices
            // ---------------------------------
            // San Francisco |
            // New York      |
            // Chicago       |
            // London        |

            graph.addEdge("London", "New York")
            graph.addEdge("New York", "Chicago")
            graph.addEdge("San Francisco", "New York")
            graph.addEdge("San Francisco", "Chicago")
//            graph.printGraph()
            println()

            // Vertex        | Adjacent Vertices
            // ---------------------------------
            // San Francisco | New York, Chicago
            // New York      | London, Chicago, San Francisco
            // Chicago       | New York, San Francisco
            // London        | New York

            graph.removeEdge("New York", "Chicago")
//            graph.printGraph()
            println()

            // Vertex        | Adjacent Vertices
            // ---------------------------------
            // San Francisco | New York, Chicago
            // New York      | London, San Francisco
            // Chicago       | San Francisco
            // London        | New York

            graph.removeVertex("London")
//            graph.printGraph()
            println()

            // Vertex        | Adjacent Vertices
            // ---------------------------------
            // San Francisco | New York, Chicago
            // New York      | San Francisco
            // Chicago       | San Francisco

            // DFS Search
            val graph2 = DirectedGraph()
            graph2.addVertex("A")
            graph2.addVertex("B")
            graph2.addVertex("C")
            graph2.addVertex("D")
            graph2.addVertex("E")
            graph2.addVertex("F")
            graph2.addVertex("G")
            graph2.addVertex("H")
            graph2.addVertex("I")
            graph2.addVertex("J")
            graph2.addVertex("K")

            graph2.addEdge("A", "B")
            graph2.addEdge("A", "G")
            graph2.addEdge("B", "C")
            graph2.addEdge("B", "E")
            graph2.addEdge("C", "D")
            graph2.addEdge("E", "F")
            graph2.addEdge("G", "H")
            graph2.addEdge("H", "I")
            graph2.addEdge("I", "J")
            graph2.addEdge("J", "K")
            graph2.addEdge("K", "G")

            println("DFS:")
            graph2.depthFirstSearch("A")
            println()

            // A
            // B
            // C
            // D
            // E
            // F
            // G
            // H
            // I
            // J
            // K

            println("BFS:")
            graph2.breadthFirstSearch("A")
            println()
            // A
            // B
            // G
            // C
            // E
            // H
            // K
            // D
            // F
            // I
            // J

            println("Minimum distance: ")
            println(graph2.minimumDistance("A", "B"))
            println(graph2.minimumDistance("A", "E"))
            println(graph2.minimumDistance("A", "G"))
            println(graph2.minimumDistance("A", "H"))
            println(graph2.minimumDistance("A", "K"))
            println(graph2.minimumDistance("A", "J"))
            println(graph2.minimumDistance("A", "I"))
            println(graph2.minimumDistance("A", "A"))
            // Minimum distance:
            // 1
            // 2
            // 1
            // 2
            // 2


            //        System.out.println("Cycle detection: ");
            //        System.out.println(graph2.detectCycle("A"));
            //
            //        Graph graph3 = new Graph();
            //        graph3.addVertex("A");
            //        graph3.addVertex("B");
            //        graph3.addVertex("C");
            //        graph3.addVertex("D");
            //        graph3.addEdge("A", "B");
            //        graph3.addEdge("B", "C");
            //        graph3.addEdge("C", "D");
            //        System.out.println(graph3.detectCycle("A"));
            //        // Cycle detection:
            //        // true
            //        // false
        }
    }
}

