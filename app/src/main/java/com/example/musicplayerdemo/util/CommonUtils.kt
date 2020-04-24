package com.example.musicplayerdemo.util

import android.content.Context
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.LruCache
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

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val isAvailable: Boolean

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // VERSION_CODES.M = API 23
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)

            capabilities ?: return false
            isAvailable = true

            return isAvailable || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        } else {
            connectivityManager.activeNetworkInfo.type == ConnectivityManager.TYPE_WIFI
        }
    }

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

        return convertJsonToMusic(str.toString())
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
            val musicInfo = LyricsInfo(totalMilliSeconds, text)

            infoList.add(musicInfo)
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