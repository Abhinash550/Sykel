package com.example.videoapp

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.hardware.Camera
import android.media.CamcorderProfile
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import android.os.SystemClock
import android.view.*
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_camera_view.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class Camera_view : AppCompatActivity(), SurfaceHolder.Callback {

    private var surfaceHolder: SurfaceHolder? = null
    private var camcorderProfile: CamcorderProfile? = null
    private var camera: Camera? = null
    var recording = false
    var usecamera = true
    private val VIDEO_PATH_NAME = "/mnt/sdcard/VGA_30fps_512vbrate.mp4"
    var previewRunning = false
    lateinit var surfaceView: SurfaceView
    var btnStart: Button? = null
    var btnStop: Button? = null
    var root: File? = null
    var file: File? = null
    var isSDPresent: Boolean? = null
    var simpleDateFormat: SimpleDateFormat? = null
    var timeStamp: String? = null
    var mediaRecorder = MediaRecorder()
    private var mMediaRecorder: MediaRecorder? = null
    private var mHolder: SurfaceHolder? = null
    private var mInitSuccesful = false

    private var recorder: MediaRecorder? = null


    private var isCaptured = false
    @SuppressLint("ResourceAsColor", "SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_view)


        actionListner()


        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        initComs()
        prepareRecorders()

//        initRecorder(mHolder?.getSurface())
    }

    private fun prepareRecorders() {
        recorder = MediaRecorder()
        recorder?.setPreviewDisplay(surfaceHolder?.surface)
        if (usecamera) {
            camera?.unlock()
//            mediaRecorder.setCamera(null)
            camera?.also { camera ->
                camera.release()
            }
        }
        recorder?.setAudioSource(MediaRecorder.AudioSource.DEFAULT)
        recorder?.setVideoSource(MediaRecorder.VideoSource.DEFAULT)
        recorder?.setProfile(camcorderProfile)
        if (camcorderProfile?.fileFormat == MediaRecorder.OutputFormat.MPEG_4) {
            recorder?.setOutputFile(
                "/sdcard/XYZApp/" + "XYZAppVideo" + ""
                        + SimpleDateFormat("ddMMyyyyHHmmss").format(Date())
                        + ".mp4"
            )
        } else if (camcorderProfile?.fileFormat == MediaRecorder.OutputFormat.MPEG_4) {
            recorder?.setOutputFile(
                "/sdcard/XYZApp/" + "XYZAppVideo" + ""
                        + SimpleDateFormat("ddMMyyyyHHmmss").format(Date())
                        + ".mp4"
            )
        } else {
            recorder?.setOutputFile(
                "/sdcard/XYZApp/" + "XYZAppVideo" + ""
                        + SimpleDateFormat("ddMMyyyyHHmmss").format(Date())
                        + ".mp4"
            )
        }
        try {
//            recorder?.prepare()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            finish()
        } catch (e: IOException) {
            e.printStackTrace()
            finish()
        }
    }


    private fun actionListner(){
        btn_to_start.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (isCaptured){
                    isCaptured = false
                    chro.base = SystemClock.elapsedRealtime()
                    recorder?.release()
                    try {
                        recorder?.stop()
                    } catch (stopException: RuntimeException) {
                        // handle cleanup here
                    }
                    camera?.lock()

                    timer.visibility = View.VISIBLE
                    gallery.visibility = View.VISIBLE
                    turn_camera.visibility = View.VISIBLE

                    btn_to_start.setBackgroundResource(R.drawable.video)
                    chro.stop()
                    startActivity(Intent(applicationContext,Photo_editing::class.java))
                    finish()

                }
                else{
                    isCaptured = true
                    chro.base = SystemClock.elapsedRealtime()
                    btn_to_start.setBackgroundResource(R.drawable.stop)

//                    recorder?.prepare()
                    val recorder = MediaRecorder()

                    val camera = Camera.open()
                    camera.unlock()
                    recorder.setCamera(camera)
                    recorder.setPreviewDisplay(surfaceHolder!!.surface)

                    recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
                    recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA)

                    recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)

                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                    recorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT)

                    recorder.setOutputFile(file?.absolutePath)

                    recorder.setMaxDuration(50000)
                    recorder.setMaxFileSize(5000000)
                    try {
                        recorder.prepare()
                        recorder.start()
                    } catch (e: java.lang.IllegalStateException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    prepareRecorderss()
                    timer.visibility = View.GONE
                    gallery.visibility = View.GONE
                    turn_camera.visibility = View.GONE
//                    recorder.prepare()

                    prepareRecorder()
                    rec()
                    chro.start()

                }

                mHolder?.getSurface()?.let { initRecorder(it) }


            }


        })}
            override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(0, 0, 0, "StartRecording")
        menu.add(0, 1, 0, "StopRecording")
        return super.onCreateOptionsMenu(menu)
    }



    private fun initRecorder(surface: Surface?) {
        if (camera == null) {
            camera = Camera.open()
            camera?.unlock()
        }
        recorder?.setPreviewDisplay(surface)
        recorder?.setCamera(camera)
        recorder?.setVideoSource(MediaRecorder.VideoSource.DEFAULT)
//        mediaRecorder.setOutputFormat(8);
        recorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        recorder?.setVideoEncoder(MediaRecorder.VideoEncoder.H264)
        recorder?.setVideoEncodingBitRate(512 * 1000)
        recorder?.setVideoFrameRate(30)
        recorder?.setVideoSize(640, 480)
        recorder?.setOutputFile(VIDEO_PATH_NAME)
        try {
            recorder?.prepare()
        } catch (e: IllegalStateException) {
            // This is thrown if the previous calls are not called with the
            // proper order
            e.printStackTrace()
        }
        mInitSuccesful = true
    }

        private fun rec(){
        camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK)
        camera?.setDisplayOrientation(90)
        camera?.unlock()
        mediaRecorder = MediaRecorder()
        mediaRecorder.setCamera(camera)
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT)
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT)
        val camcorderProfile_HQ =
            CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH)
        mediaRecorder.setProfile(camcorderProfile_HQ)
        mediaRecorder.setMaxDuration(60000) // Set max duration 60 sec.

        mediaRecorder.setMaxFileSize(5000000) // Set max file size 5M


    }

    private fun prepareRecorderss() {
        recorder = MediaRecorder()
        recorder?.setPreviewDisplay(surfaceHolder?.surface)
        if (usecamera) {
            camera?.unlock()
            recorder?.setCamera(camera)
        }
        recorder?.setAudioSource(MediaRecorder.AudioSource.DEFAULT)
        recorder?.setVideoSource(MediaRecorder.VideoSource.DEFAULT)
        recorder?.setProfile(camcorderProfile)
        if (camcorderProfile?.fileFormat == MediaRecorder.OutputFormat.MPEG_4) {
            recorder?.setOutputFile(
                "/sdcard/XYZApp/" + "XYZAppVideo" + ""
                        + SimpleDateFormat("ddMMyyyyHHmmss").format(Date())
                        + ".mp4"
            )
        } else if (camcorderProfile?.fileFormat == MediaRecorder.OutputFormat.MPEG_4) {
            recorder?.setOutputFile(
                "/sdcard/XYZApp/" + "XYZAppVideo" + ""
                        + SimpleDateFormat("ddMMyyyyHHmmss").format(Date())
                        + ".mp4"
            )
        } else {
            recorder?.setOutputFile(
                "/sdcard/XYZApp/" + "XYZAppVideo" + ""
                        + SimpleDateFormat("ddMMyyyyHHmmss").format(Date())
                        + ".mp4"
            )
        }
        try {
            recorder?.prepare()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            finish()
        } catch (e: IOException) {
            e.printStackTrace()
            finish()
        }
    }

    private fun prepareRecorder() {
        mediaRecorder.setPreviewDisplay(surfaceHolder?.getSurface())
        try {
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            finish()
        } catch (e: IOException) {
            e.printStackTrace()
            finish()
        }
    }
    @SuppressLint("SimpleDateFormat")
    private fun initComs() {
        simpleDateFormat = SimpleDateFormat("ddMMyyyyhhmmss")
        timeStamp = simpleDateFormat?.format(Date())
        camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH)
        surfaceView = findViewById<View>(R.id.preview) as SurfaceView
        surfaceHolder = surfaceView?.holder
        surfaceHolder?.addCallback(this)
        surfaceHolder?.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        isSDPresent = Environment.getExternalStorageState() ==
                Environment.MEDIA_MOUNTED

    }


        override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        println("onsurface changed")
        if (!recording && usecamera) {
            if (previewRunning) {
                camera?.stopPreview()
            }

            try {
                val p = camera?.parameters
                camcorderProfile?.videoFrameWidth?.let {
                    camcorderProfile?.videoFrameHeight?.let { it1 ->
                        p?.setPreviewSize(
                            it,
                            it1
                        )
                    }
                }
                if (p != null) {
                    p.set("orientation", "portrait")
                }
                if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {

                    camera?.setDisplayOrientation(90);

                } else {

                    camera?.setDisplayOrientation(0);

                }
                prepareRecorderss()
                p?.previewFrameRate  = camcorderProfile?.videoFrameRate!!
//                camera?.parameters  = p
                camera?.setPreviewDisplay(holder)
                camera?.startPreview()
                previewRunning = true
            } catch (e: IOException) {
                e.printStackTrace()
            }


        }

    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        if (usecamera) {
            previewRunning = false
            camera?.lock();
            camera?.release()
        }
        finish()
        if(recorder!=null)
        {
            recorder?.release();
            recorder = null;
        }
    }

    companion object {
        fun megabytesAvailable(f: File): Float {
            val stat = StatFs(f.path)
            val bytesAvailable = (stat.blockSize.toLong()
                    * stat.availableBlocks.toLong())
            return bytesAvailable / (1024f * 1024f)
        }
    }


    override fun surfaceCreated(holder: SurfaceHolder?) {
        println("onsurfacecreated")
        if (usecamera) {
            camera = Camera.open()
            try {
                camera?.setPreviewDisplay(holder)

                camera?.startPreview()
                previewRunning = true
            } catch (e: IOException) {

                e.printStackTrace()
            }
        }

        try {
            recorder?.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
            recorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder?.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
            recorder?.setOutputFile("/sdcard/recordvideooutput.3gpp");
            if (holder != null) {
                recorder?.setPreviewDisplay(holder.getSurface())
            };
            recorder?.prepare();
        } catch (e: Exception ) {
            Toast.makeText(this,"Error occurred",Toast.LENGTH_SHORT).show()
        }

    }


}
