import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.jsoup.Jsoup



@ImplicitReflectionSerializer
@UnstableDefault
fun main(){
    val doc = Jsoup.connect("https://en.wikipedia.org/wiki/Elizabeth_Richards_Tilton").get()
    val content = doc.select("div.mw-parser-output").select("p").select("a[href]")
    println(content)
    //Json.stringify(Vertices.serializer(), vertices)
}