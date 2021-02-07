package com.example.yanji_oblig2

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream

private val ns:String?= null
class XmlParser {
    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(inputStream: InputStream): List<party> {
        inputStream.use { inputStream ->
            val parser: XmlPullParser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(inputStream, null)
            parser.nextTag()
            return readFeed(parser)

        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readFeed(parser: XmlPullParser): List<party> {
        val entries = mutableListOf<party>()

        parser.require(XmlPullParser.START_TAG, ns, "districtThree")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            // Starts by looking for the entry tag
            if (parser.name == "party") {
                entries.add(readEntry(parser))
            } else {
                skip(parser)
            }
        }
        return entries
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readEntry(parser: XmlPullParser): party {
        parser.require(XmlPullParser.START_TAG, ns, "districtThree")
        var id: Int? = null
        var votes: Int? = null

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when (parser.name) {
                "id" -> id = readID(parser)
                "votes" -> votes = readVotes(parser)
                else -> skip(parser)
            }
        }
        return party(id, votes)
    }
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readID(parser: XmlPullParser): Int? {
        parser.require(XmlPullParser.START_TAG, ns, "id")
        val id = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "id")
        return id.toIntOrNull()
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readVotes(parser: XmlPullParser): Int? {
        parser.require(XmlPullParser.START_TAG, ns, "votes")
        val votes = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, "votes")
        return votes.toIntOrNull()
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readText(parser: XmlPullParser): String {
        var result = ""
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.text
            parser.nextTag()
        }
        return result
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun skip(parser: XmlPullParser) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            throw IllegalStateException()
        }
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }

}
data class party (val id:Int?, val votes:Int?)