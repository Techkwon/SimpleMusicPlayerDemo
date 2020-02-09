package com.example.musicplayerdemo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.example.musicplayerdemo.R
import com.example.musicplayerdemo.data.Music
import com.example.musicplayerdemo.ui.PlayerActivity
import com.example.musicplayerdemo.viewModel.MusicViewModel
import kotlinx.android.synthetic.main.fragment_album.*
import java.lang.Exception

class AlbumFragment: Fragment() {
    private val playerActivity: PlayerActivity by lazy { activity as PlayerActivity }

    private lateinit var viewModel: MusicViewModel

    companion object {
        private const val LYRIC_POSITION_CURRENT = 0
        private const val LYRIC_POSITION_NEXT = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_album, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getMusicInfo()
        getLyrics()
        setViewsClickListener()
    }

    @Throws(Exception::class)
    private fun initViewModel() {
        viewModel = activity?.run {
            ViewModelProviders.of(this).get(MusicViewModel::class.java)
        } ?: throw Exception(getString(R.string.error_invalid_activity))
    }

    private fun setViewsClickListener() {
        this.constraint_holder_lyrics.setOnClickListener {
            playerActivity.transactLyricsFragment()
        }
    }

    private fun getMusicInfo() {
        viewModel.music.observe(this, Observer { music ->
            setSongInfoView(music)
        })
    }

    private fun setSongInfoView(music: Music) {
        Glide.with(this).load(music.image).into(this.iv_album_cover)
        this.tv_song_title.text = music.title
        this.tv_singer.text = music.singer
    }

    private fun getLyrics() {
        viewModel.lyrics.observe(this, Observer {
            showCurrentLyric(it[LYRIC_POSITION_CURRENT])
            showNextLyric(it[LYRIC_POSITION_NEXT])
        })
    }

    private fun showCurrentLyric(text: String) {
        this.tv_song_lyrics_now.text = text
    }

    private fun showNextLyric(text: String) {
        this.tv_song_lyrics_next.text = text
    }
}