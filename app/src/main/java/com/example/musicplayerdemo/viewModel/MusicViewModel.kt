package com.example.musicplayerdemo.viewModel

import android.app.ActivityManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayerdemo.data.LyricsInfo
import com.example.musicplayerdemo.util.CommonUtils.getMusicFromUrl
import com.example.musicplayerdemo.data.Music
import com.example.musicplayerdemo.util.CommonUtils.getDividedSections
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MusicViewModel: ViewModel() {
    private var lyricsInfoList: ArrayList<LyricsInfo>? = null

    internal val music = MutableLiveData<Music>()
    internal val lyrics = MutableLiveData<List<String>>()
    internal val selectedLyricPosition = MutableLiveData<Long>()
    internal val currentPosition = MutableLiveData<Int>()
    private val isToggleChecked = MutableLiveData<Boolean>()

    internal fun getMusic(url: String) = viewModelScope.launch(Dispatchers.IO) {
        setMusic(getMusicFromUrl(url))
    }

    private fun setMusic(data: Music) = viewModelScope.launch(Dispatchers.Main) {
        music.value = data
        lyricsInfoList = getDividedSections(data.lyrics)
    }

    internal fun getLyricInfoList() = lyricsInfoList

    internal fun findCurrentLyrics(currentTime: Long) {
        lyricsInfoList?.let { infoList ->
            for (i in 0 until infoList.size - 1) {
                if (currentTime <= infoList[i + 1].time) {
                    updateCurrentLyrics(i)
                    break
                }
            }
        }
    }

    private fun updateCurrentLyrics(position: Int) {
        lyricsInfoList?.let { infoList ->
            currentPosition.value = position
            val currentLyric = infoList[position].text
            val nextLyric = if (position < infoList.size - 1) infoList[position + 1].text else ""

            lyrics.value = listOf(currentLyric, nextLyric)
        }
    }

    internal fun selectLyricPosition(time: Long) {
        selectedLyricPosition.value = time
    }

    internal fun checkToggle(isOn: Boolean) {
        isToggleChecked.value = isOn
    }

    internal fun getToggleValue(): Boolean = isToggleChecked.value ?: false
}