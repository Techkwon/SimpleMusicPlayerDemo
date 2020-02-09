package com.example.musicplayerdemo.ui

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.musicplayerdemo.fragments.LyricFragment
import com.example.musicplayerdemo.viewModel.MusicViewModel
import com.example.musicplayerdemo.R
import com.example.musicplayerdemo.fragments.AlbumFragment
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_player.*

class PlayerActivity : AppCompatActivity() {
    private val fragmentManager = supportFragmentManager

    private var albumFragment: AlbumFragment? = null
    private var lyricsFragment: LyricFragment? = null

    private var player: SimpleExoPlayer? = null

    private lateinit var playBackStateListener: PlayBackStateListener

    private lateinit var viewModel: MusicViewModel

    companion object {
        private const val SAMPLE_MUSIC_URL = "https://grepp-programmers-challenges.s3.ap-northeast-2.amazonaws.com/2020-flo/song.json"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        viewModel = ViewModelProviders.of(this).get(MusicViewModel::class.java)

        getMusicInfo()
        getSelectedLyricPositionOrGoBack()
        transactAlbumFragment()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStart() {
        super.onStart()
    }

    private fun transactAlbumFragment() {
        albumFragment = albumFragment ?: AlbumFragment()

        fragmentManager.beginTransaction()
            .add(R.id.constraint_holder_player_activity, albumFragment as Fragment)
            .commit()
    }

    internal fun transactLyricsFragment() {
        lyricsFragment = lyricsFragment ?: LyricFragment()

        fragmentManager.beginTransaction()
            .addToBackStack(null)
            .setTransition(TRANSIT_FRAGMENT_FADE)
            .replace(R.id.constraint_holder_player_activity, lyricsFragment as Fragment)
            .commit()
    }

    private fun getMusicInfo() {
        viewModel.getMusic(SAMPLE_MUSIC_URL)

        viewModel.music.observe(this, Observer { music ->
            initPLayer(music.file)
        })
    }

    private fun getSelectedLyricPositionOrGoBack() {
        viewModel.selectedLyricPosition.observe(this, Observer {
            if (viewModel.getToggleValue()) player?.seekTo(it + 50)
            else onBackPressed()
        })
    }

    private fun initPLayer(songUrl: String) {
        playBackStateListener = PlayBackStateListener()

        player = ExoPlayerFactory.newSimpleInstance(this)
        this.music_player.player = player
        this.music_player.showTimeoutMs = 0 // keep player UI on display

        val httpDataSourceFactory = DefaultHttpDataSourceFactory(getString(R.string.app_name))
        val mediaSource = ProgressiveMediaSource
            .Factory(httpDataSourceFactory)
            .createMediaSource(Uri.parse(songUrl))
        player?.addListener(playBackStateListener)
        player?.prepare(mediaSource)

        setPlayerProgressListener()
    }

    private fun setPlayerProgressListener() {
         this.music_player.setProgressUpdateListener { currentTime, _ ->
            viewModel.findCurrentLyrics(currentTime)
        }
    }

    private fun releasePlayer() {
        player?.let {

            it.removeListener(playBackStateListener)
            it.release()
            player = null
        }
    }

    inner class PlayBackStateListener: Player.EventListener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            super.onPlayerStateChanged(playWhenReady, playbackState)
            val stateString: String = when (playbackState) {
                ExoPlayer.STATE_IDLE -> {
                    setViewWhenTrouble()
                    "ExoPlayer.STATE_IDLE      -"
                }
                ExoPlayer.STATE_BUFFERING -> {
                    "ExoPlayer.STATE_BUFFERING -"
                }
                ExoPlayer.STATE_READY -> {
                    "ExoPlayer.STATE_READY     -"
                }
                ExoPlayer.STATE_ENDED -> {
                    "ExoPlayer.STATE_ENDED     -"
                }
                else -> {
                    "UNKNOWN_STATE             -"
                }
            }
        }

        private fun setViewWhenTrouble() {
            Snackbar.make(this@PlayerActivity.constraint_main_player_holder,
                getString(R.string.error_with_audio_source),
                Snackbar.LENGTH_LONG
            ).show()
        }
    }
}
