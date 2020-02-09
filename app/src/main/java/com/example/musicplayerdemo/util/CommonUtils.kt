package com.example.musicplayerdemo.util

import android.content.Context
import com.example.musicplayerdemo.data.LyricsInfo
import com.example.musicplayerdemo.data.Music
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.URL

object CommonUtils {

    internal fun getMusicFromAsset(context: Context): Music {
        val music = getJson(context, "sample/sample_data.json")
        return GsonBuilder().serializeNulls().create().fromJson(music, Music::class.java)
    }

    private fun getJson(context: Context, data: String): String {
        val inputStream = context.assets.open(data)
        val buffer = ByteArray(inputStream.available())

        inputStream.read(buffer)
        inputStream.close()
        return String(buffer)
    }

    @Throws(IOException::class)
    internal fun getMusicFromUrl(sampleUrl: String): Music {
        val url = URL(sampleUrl)
        val reader = BufferedReader(InputStreamReader(url.openStream()))
        val str = StringBuilder()

        do {
            val line = reader.readLine()
            line?.let { str.append(line) }
        } while (line != null)

        reader.close()
        return convertJsonToMusic(
            str.toString()
        )
    }

    private fun convertJsonToMusic(json: String): Music {
        return Gson().fromJson(json, Music::class.java)
    }

    internal fun getDividedSections(data: String): ArrayList<LyricsInfo> {
        val infoList = ArrayList<LyricsInfo>()
        val sections = data.split("\n")

        for (section in sections) {
            val totalMilliSeconds = getMilliSecondsFromSection(section)
            val text = section.substring(11)

            infoList.add(LyricsInfo(totalMilliSeconds, text))
        }

        return infoList
    }

    private fun getMilliSecondsFromSection(section: String): Long {
        val time = section.substring(1, 9)
        val times = time.split(":")

        val min = times[0].toLong() * 60 * 1000 // 1min = 60sec
        val sec = times[1].toLong() * 1000 // 1sec = 1000 ms
        val ms = times[2].toLong()

        return min + sec + ms // total time of milliseconds
    }
}