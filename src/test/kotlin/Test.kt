import com.jagrosh.jlyrics.LyricsClient

fun main() {
    val client = LyricsClient()
    val it = client.getLyrics("That's What I Like").get()
    val lyrics = Lyrics(it.title, it.author, it.content, it.url, it.source)
    println(lyrics.toString())
    println(lyrics.content)
}

class Lyrics(title: String?, author: String?, content: String?, url: String?, source: String?) : com.jagrosh.jlyrics.Lyrics(title, author, content, url, source) {

    override fun toString(): String {
        return "Lyrics(title=$title, author=$author, content=$content, url=$url, source=$source)"
    }
}