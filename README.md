# Wikipedia Page Rank

I created this as my Data Structures final project in Fall 2019. It scrapes Wikipedia for links to other pages (using Breadth First Search), which is to create a graph between the wikipedia pages (directed edges represent links). This is used to make a “page rank” algorithm that ranks pages by inbound links.

The main file is [Main.kt](src/main/kotlin/Main.kt), which contains the function to run the project and most of the core logic.

[DirectedGraph.kt](src/main/kotlin/DirectedGraph.kt) Contains the code for the graph that represents Wikipedia

[Serializer.kt](src/main/kotlin/Serializer.kt) Contains the code that stores the graph as a JSON object to allow the code to pick up where it left off and continue adding to its existing graph. The pre-generated JSON files are not included in this repository due to storage constraints, but will be saved when the code is run.



