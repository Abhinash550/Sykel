package com.example.videoapp

import android.annotation.SuppressLint
import android.app.Activity
import android.hardware.Camera
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils.replace
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import androidx.fragment.app.Fragment
import com.bumptech.glide.load.model.stream.MediaStoreVideoThumbLoader
import com.facebook.share.internal.ShareConstants.CONTENT_URL
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.LoopingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.SingleSampleMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_media_recorder_recipes.*
import com.google.android.exoplayer2.upstream.HttpDataSource.Factory as Factory1


@Suppress("DEPRECATED_IDENTITY_EQUALS", "DEPRECATION")
class MediaRecorderRecipes : Activity() {



    @SuppressLint("SdCardPath")
    private val VIDEO_PATH_NAME = "/mnt/sdcard/VGA_30fps_512vbrate.mp4"

    private var mMediaRecorder: MediaRecorder? = null
    private var mCamera: Camera? = null
    private var mSurfaceView: SurfaceView? = null
    private var mHolder: SurfaceHolder? = null
    private var mToggleButton: View? = null
    private var path = "gs://video-app-af9bf.appspot.com/Videos/Video_20200809_153346_.mp4"


    @SuppressLint("SourceLockedOrientationActivity", "InflateParams", "PrivateResource",
        "WrongViewCast"
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_recorder_recipes)


        val uri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/video-app-af9bf.appspot.com/o/Videos%2FVideo_20200812_141638_.mp4?alt=media&token=d87f94e0-6ca3-4de2-856c-b228884185f7")
        named.setVideoURI(uri)
        named.requestFocus()
        named.start()

    }


}