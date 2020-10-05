package com.example.videoapp

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.hls.DefaultHlsExtractorFactory
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.SimpleExoPlayerView
import com.google.android.exoplayer2.upstream.BandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_gallery__upload.*


@Suppress("UNREACHABLE_CODE")
class Gallery_Upload : Fragment() {


    private val path ="https://firebasestorage.googleapis.com/v0/b/video-app-af9bf.appspot.com/o/Videos%2FVideo_20200820_174303_.mp4?alt=media&token=e88afd93-6a62-4a99-8c13-28928d36f7af"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val fragmentview = inflater.inflate(R.layout.activity_gallery__upload, container, false)
        val uri = Uri.parse(path)
        val view = fragmentview.findViewById<SimpleExoPlayerView>(R.id.display)
//        view.setVideoURI(uri)
//        view.start()
        val bandwidthMeter= DefaultBandwidthMeter()
        val trackSelector = DefaultTrackSelector(AdaptiveTrackSelection.Factory(bandwidthMeter))
        val exoplayer = ExoPlayerFactory.newSimpleInstance(context,trackSelector)
        val datasourcefactory = DefaultHttpDataSourceFactory("exoplayer")
        val extractorsFactory=DefaultExtractorsFactory()
        val media = ExtractorMediaSource(uri,datasourcefactory,extractorsFactory,null,null)
        view.player = exoplayer
        exoplayer.prepare(media)
        exoplayer.playWhenReady = true
        return fragmentview

    }


}
