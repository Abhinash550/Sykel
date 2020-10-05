package com.example.videoapp

import android.Manifest.permission.RECORD_AUDIO
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.hardware.Camera
import android.media.CamcorderProfile
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import android.view.*
import android.widget.Button
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_option.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.jar.Manifest


@Suppress("DEPRECATED_IDENTITY_EQUALS", "DEPRECATION")
class Chooses : Activity(), SurfaceHolder.Callback {
    private var recorder: MediaRecorder? = null
    private var surfaceHolder: SurfaceHolder? = null
    private var camcorderProfile: CamcorderProfile? = null
    private var camera: Camera? = null
    var recording = false
    var usecamera = true
    var previewRunning = false
    var surfaceView: SurfaceView? = null
    var btnStart: Button? = null
    var btnStop: Button? = null
    var root: File? = null
    var file: File? = null
    var isSDPresent: Boolean? = null
    var simpleDateFormat: SimpleDateFormat? = null
    var timeStamp: String? = null
    @SuppressLint("SourceLockedOrientationActivity")
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        singing.setOnClickListener {
            val dialog =  Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_RIGHT_ICON);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.design);
            dialog.show()

        }
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        setContentView(R.layout.activity_chooses)
        initComs()
        actionListener()
    }

    private fun initComs() {
        simpleDateFormat = SimpleDateFormat("ddMMyyyyhhmmss")
        timeStamp = simpleDateFormat?.format(Date())
        camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH)
        surfaceView = findViewById<View>(R.id.preview) as SurfaceView
        surfaceHolder = surfaceView?.holder
        surfaceHolder?.addCallback(this)
        btnStop = findViewById<View>(R.id.btn_stop) as Button
        surfaceHolder?.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        isSDPresent = Environment.getExternalStorageState() ==
                Environment.MEDIA_MOUNTED
    }

    private fun actionListener() {
        btnStop?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (recording) {
                    recorder?.release()
                    try {
                        recorder?.stop()
                    } catch (stopException: RuntimeException) {
                        // handle cleanup here
                    }
                    camera?.lock()
                    if (usecamera) {
                        try {
                            camera?.reconnect()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                    // recorder.release();
                    recording = false
                    // Let's prepareRecorder so we can record again
                    recorder?.start()
                    prepareRecorder()
                }
            }
        })
    }

    private fun prepareRecorder() {
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
    }

    @SuppressLint("SdCardPath")
    override fun surfaceChanged(
        holder: SurfaceHolder?, format: Int, width: Int,
        height: Int
    ) {
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
                p?.previewFrameRate = camcorderProfile?.videoFrameRate!!
//                camera?.parameters = p
                camera?.setPreviewDisplay(holder)
                camera?.startPreview()
                previewRunning = true
            } catch (e: IOException) {
                e.printStackTrace()
            }
            prepareRecorder()
            if (!recording) {
                recording = true
                val recorder = MediaRecorder()

                val camera = Camera.open()
                camera.unlock()
                recorder.setCamera(camera)
                recorder.setPreviewDisplay(surfaceHolder!!.surface)

                recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
                recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA)
//                recorder.setProfile(CamcorderProfile
//                    .get(CamcorderProfile.QUALITY_HIGH));]
                recorder.setPreviewDisplay(surfaceHolder?.getSurface());

//                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                //             recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

                recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                recorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);

//                recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                this.recorder?.setOutputFile(this.file?.absolutePath?.substring(8));
//               recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                //   recorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT)


                if (ActivityCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.RECORD_AUDIO
                    ) != PackageManager.PERMISSION_GRANTED
                ) {

                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf({ android.Manifest.permission.RECORD_AUDIO }.toString()),
                        RECORD_AUDIO.toInt()
                    );

                } else {


                }


//                recorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
//                recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                recorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
                recorder.setOutputFile("/sdcard/recordvideooutput.3gpp");
                if (holder != null) {
                    recorder.setPreviewDisplay(holder.getSurface())
                };
                recorder.prepare();

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
                this.recorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

            }
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        if (recording) {
            recorder?.release()
            try {
                recorder?.stop()
            } catch (stopException: RuntimeException) {
                // handle cleanup here
            }
            camera?.lock()
            recording = false
        }
        recorder?.release()
        if (usecamera) {
            previewRunning = false
            // camera.lock();
            camera?.release()
        }
        finish()
    }

    companion object {
        fun megabytesAvailable(f: File): Float {
            val stat = StatFs(f.path)
            val bytesAvailable = (stat.blockSize.toLong()
                    * stat.availableBlocks.toLong())
            return bytesAvailable / (1024f * 1024f)
        }
    }
}
