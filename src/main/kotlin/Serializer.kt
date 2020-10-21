/*
 * @author Adit Shah
 *
 * This file creates a wrapper around two different methods for serializing and storing the graph object (or more accurately,
 * its vertices HashMap) in a file. It also has methods for reading it back into memory. I implemented this so that I
 * could pause and resume execution of the graph creation, and build up the graph over time rather than having to run it
 * from scratch each time.
 */

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.*

/* This interface acts as a template for Serializer classes, so I can switch between the two types of Serializers
* without having to change much code.*/
interface Serializer {
    fun write(obj: HashMap<String, Vertex>, filename: String);
    fun read(filename: String): HashMap<String, Vertex>;
}

/* This is the serializer I was using initially. It saves the object to some sort of Java proprietary .ser file. However,
 it started to break when run on very large files, causing a StackOverFlow error.*/
class JavaSerializer : Serializer {
    override fun write(obj: HashMap<String, Vertex>, filename: String) {
        val file = FileOutputStream(filename)
        val buffer = BufferedOutputStream(file)
        val outStream = ObjectOutputStream(buffer)
        try {
            outStream.writeObject(obj)
        } finally {
            outStream.close()
            file.close()
        }
    }

    override fun read(filename: String): HashMap<String, Vertex> {
        val file = FileInputStream(filename)
        val buffer = BufferedInputStream(file)
        val inStream = ObjectInputStream(buffer)
        val object1: HashMap<String, Vertex>
        try {
            object1 = inStream.readObject() as HashMap<String, Vertex>
        } finally {
            inStream.close()
            file.close()
        }
        return object1
    }
}

/* This class creates a JSON object out of the vertices HashMap, and saves it to a file. It does not have the StackOverflow
* issue after changing Neighbors in Vertex to a HashSet<String> instead of HashSet<Vertex>, b/c circular references are avoided */
class JSONSerializer : Serializer {
    val mapper = jacksonObjectMapper()
    override fun read(filename: String): HashMap<String, Vertex> {
        val obj: HashMap<String, Vertex> =
            mapper.readValue(File(filename))//, object : TypeReference<HashMap<String,Vertex>>(){})
        return obj
    }

    override fun write(obj: HashMap<String, Vertex>, filename: String) {
        val file = File(filename)
        mapper.writeValue(file, obj)
    }
}