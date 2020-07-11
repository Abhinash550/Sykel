package com.example.videoapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.MediaController
import android.widget.VideoView

class record : AppCompatActivity() {
    lateinit var mVideoView:VideoView
    companion object{
        val CAPTURE_VIDEO_CODE= 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)
        captureVideo()
    }
    fun captureVideo(){
        mVideoView=findViewById(R.id.video_view)
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        startActivityForResult(intent, CAPTURE_VIDEO_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode== CAPTURE_VIDEO_CODE && resultCode==Activity.RESULT_OK){
            val videUri = data!!.data
            mVideoView.setVideoURI(videUri)
            mVideoView.setMediaController(MediaController(this))
            mVideoView.requestFocus()
            mVideoView.start()

        }
    }
}
