package com.atozkids.learn.data

import android.content.Context
import org.json.JSONObject

data class Item(
    val id: String,
    val name: String,
    /** Set for alphabet items only ("A"). */
    val letter: String?,
    /** Main tile: the letter tile for the alphabet, otherwise the picture. */
    val tile: String,
    /** A picture of the thing itself. For the alphabet this is the word card (e.g. Apple). */
    val picture: String,
    val audioMale: String,
    val audioFemale: String,
) {
    fun audio(female: Boolean) = if (female) audioFemale else audioMale
}

data class Category(
    val id: String,
    val title: String,
    val caption: String,
    val icon: String,
    val items: List<Item>,
) {
    val isAlphabet get() = id == "alphabet"
}

data class Rhyme(val id: String, val title: String, val image: String, val audio: String)

class Content(val categories: List<Category>, val rhymes: List<Rhyme>) {
    fun category(id: String) = categories.first { it.id == id }
    fun rhyme(id: String) = rhymes.first { it.id == id }

    companion object {
        const val FILE = "content.json"

        fun load(context: Context): Content =
            parse(context.assets.open(FILE).bufferedReader().use { it.readText() })

        fun parse(json: String): Content {
            val root = JSONObject(json)
            val cats = root.getJSONArray("categories")
            val categories = (0 until cats.length()).map { c ->
                val o = cats.getJSONObject(c)
                val items = o.getJSONArray("items")
                Category(
                    id = o.getString("id"),
                    title = o.getString("title"),
                    caption = o.getString("caption"),
                    icon = o.getString("icon"),
                    items = (0 until items.length()).map { i ->
                        val it = items.getJSONObject(i)
                        val letter = it.optString("letter").takeIf { l -> l.isNotEmpty() && l != "null" }
                        Item(
                            id = it.getString("id"),
                            name = it.getString("name"),
                            letter = letter,
                            tile = it.getString("image"),
                            // Alphabet: the tile is only the letter, the caption card holds the picture.
                            picture = if (letter != null) it.getString("caption") else it.getString("image"),
                            audioMale = it.getString("audioMale"),
                            audioFemale = it.getString("audioFemale"),
                        )
                    },
                )
            }
            val rh = root.getJSONArray("rhymes")
            val rhymes = (0 until rh.length()).map {
                val o = rh.getJSONObject(it)
                Rhyme(o.getString("id"), o.getString("title"), o.getString("image"), o.getString("audio"))
            }
            return Content(categories, rhymes)
        }
    }
}
