package com.example.musicplayerdemo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayerdemo.R
import com.example.musicplayerdemo.adapter.LyricAdapter
import com.example.musicplayerdemo.data.Music
import com.example.musicplayerdemo.ui.PlayerActivity
import com.example.musicplayerdemo.viewModel.MusicViewModel
import kotlinx.android.synthetic.main.fragment_lyrics.*
import java.lang.Exception

class LyricFragment: Fragment() {
    private val playerActivity: PlayerActivity by lazy { activity as PlayerActivity }

    private lateinit var viewModel: MusicViewModel

    private var adapter: LyricAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_lyrics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        setViewClickListener()
        getMusicInfo()
        getToggleValue()
        getCurrentLyricPosition()
    }

    private fun setViewClickListener() {
        this.ib_cancel.setOnClickListener { playerActivity.onBackPressed() }

        this.tb_go_to_position.setOnClickListener {
            val isChecked = (it as SwitchCompat).isChecked
            viewModel.checkToggle(isChecked)
        }
    }

    @Throws(Exception::class)
    private fun initViewModel() {
        viewModel = activity?.run {
            ViewModelProviders.of(this).get(MusicViewModel::class.java)
        } ?: throw Exception(getString(R.string.error_invalid_activity))
    }

    private fun getMusicInfo() {
        viewModel.music.observe(this, Observer { music ->
            setMusicInfo(music)

            viewModel.getLyricInfoList()?.let {
                adapter?.addList(it.toList()) }
        })
    }

    private fun getCurrentLyricPosition() {
        viewModel.currentPosition.observe(this, Observer { position ->
            adapter?.setCurrentPosition(position)
        })
    }

    private fun getToggleValue() {
        this.tb_go_to_position.isChecked = viewModel.getToggleValue()
    }

    private fun initAdapter() {
        adapter = adapter ?: LyricAdapter(playerActivity, viewModel)
        this.rv_lyrics.layoutManager = LinearLayoutManager(activity)
        this.rv_lyrics.adapter = adapter
    }

    private fun setMusicInfo(music: Music) {
        this.tv_lyrics_title.text = music.title
        this.tv_lyrics_singer.text = music.singer
    }
}