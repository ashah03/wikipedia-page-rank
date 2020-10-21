/*
 * @author Adit Shah
 *
 * This file includes the core logic for this project, including the logic to find and filter links using a modified
 * Breadth-first search algorithm, and sorting the list with a heap
 */
import kotlinx.serialization.UnstableDefault
import org.jsoup.Jsoup
import java.io.FileNotFoundException
import java.util.*


var graph = DirectedGraph()
var prefix = "https://simple.wikipedia.org/wiki/"

/* This function checks whether a link is actually a wikipedia article that should be indexed. It removes special pages
 * (these usually have a colon) and ensures that the page is on the desired subdomain of wikipedia (designated as prefix).
 *  It also ensures that links to itself (incl. the table of contents) are not included
 */
fun checkLink(url: String, originURL: String): Boolean {
    return url.startsWith(prefix) and !url.contains(originURL) and
            (url.asSequence().filter { it == ':' }.count() < 2) and
            !url.contains("#") and !url.contains("wikimedia")
}


/*
 * This function utilizes the JSoup API to find all of the links on a page, and returns them if they pass the checkLink method above
 */
fun getLinks(url: String): Collection<String> {
    val doc = Jsoup.connect(url).get()
    val content = doc.select("div.mw-parser-output")
        .select("p").select("a[href]")
    val list = ArrayList<String>()
    for (link in content) {
        val newURL = link.attr("abs:href")
        if (checkLink(newURL, url)) {
            list.add(newURL)
        }
    }
    return list
}


/*
 * This is where the core logic of the program is. It utilizes a modified breadth first search algorithm to find links
 *  between pages of wikipedia and create a directed graph to represent the relationships between pages
 */
fun findLinks(startingURL: String, numLinks: Int, saveFrequency: Int, serializer: Serializer, filename: String) {
    val visited = HashSet<String>() //Nodes that have already been visited
    val queue = LinkedList<Vertex>()
    queue.add(Vertex(startingURL))
    var i = 0
    while (queue.isNotEmpty() && i < numLinks) {
        i++
        if (i % saveFrequency == 0) {
            serializer.write(graph.vertices, filename)
            println("   $i")
        }

        val url = queue.remove().label
        try {
            for (newURL in getLinks(url)) {
                graph.addEdge(url, newURL)
                if (!visited.contains(newURL)) {
                    visited.add(newURL)
                    queue.add(graph.vertices[newURL]!!)
                }
            }
        } catch (e: org.jsoup.HttpStatusException) {
            println("incorrect URL: $url")
        }
    }
}


/*
 * This function utilizes the Java standard library to create a priority queue from the vertices (through the compareTo
 * which compares the number of neighbors -- see DirectedGraph.java for more info). It then prints out the first *count*
 * vertices, which correspond to the pages with the highest number of links pointing to them
 */
fun rankPages(count: Int, direction: String = "max") { //: PriorityQueue<Int> {
    val pq = when (direction) {
        "max" -> PriorityQueue(Collections.reverseOrder<Vertex>())
        "min" -> PriorityQueue()
        else -> throw IllegalArgumentException("$direction is invalid")
    }
    pq.addAll(graph.vertices.values)
    repeat(count) {
        val v = pq.poll()
        val article = v.label.removePrefix(prefix)
        println("$it: $article (${v.neighbors.size})")
    }
}

/* The main method is where everything gets put together. It loads the graph into memory (see Serializer.kt), starts the graph creation
* at a random page, runs the graph creation for a specified number of links, then ranks the pages. Finally, it saves the graph. */
@UnstableDefault
fun main() {
    val filename = "simple.json"
    prefix = "https://simple.wikipedia.org/wiki/"
    //val filename = "en.json"
    //prefix = "https://en.wikipedia.org/wiki/"

    //val serializer : Serializer = JavaSerializer<>()
    val serializer = JSONSerializer()
    try {
        val vertices = serializer.read(filename)
        graph = DirectedGraph(vertices)
        println("Successfully loaded $filename")
    } catch (f: FileNotFoundException) {
        println("File $filename not found, creating file")
    } catch (e: StackOverflowError) {
        println("Stack overflow, making a new graph")
    }

    repeat(1) {
        println("Loop $it:")
        findLinks(prefix + "Special:Random", 10, 250, serializer, filename)
        rankPages(50)
        //rankPages(50, "min")
        serializer.write(graph.vertices, filename)
        println(graph.vertices.size)
    }
}


/* Links and Sources:
 * https://jsoup.org/cookbook/extracting-data/example-list-links
 * https://www.geeksforgeeks.org/serialization-in-java/
 * https://www.baeldung.com/jackson-object-mapper-tutorial
 * https://stackoverflow.com/questions/11003155/change-priorityqueue-to-max-priorityqueue
 * Used the Graph code we wrote (including your starter code) as a baseline but converted
 *      it to Kotlin with the Java-> Kotlin converter, and made it a directed graph
 */

