package com.example.videoapp

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.design.*


class option : AppCompatActivity() {

    private var fileUri // file url to store image/video
            : Uri? = null
    private var btnCapturePicture: Button? = null
    private var btnRecordVideo: Button? = null
    private var videouri: Uri? = null
    private var videoref: StorageReference? = null


    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_option)
        val storageRef = FirebaseStorage.getInstance().reference

        videoref = storageRef.child("/videos" + "/userIntro.3gp")
        btnCapturePicture =
            findViewById<View>(R.id.meme) as Button
        btnRecordVideo =
            findViewById<View>(R.id.dancing) as Button
        /**
         * Capture image button click event
         *
         */

        btnRecordVideo?.setOnClickListener{

            val dialog =  Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_RIGHT_ICON);
            dialog.setCancelable(false);
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.setCanceledOnTouchOutside(true)
            dialog.setContentView(R.layout.design);
            dialog.show()


            }

        }


    fun record() {
    }
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int, data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        videouri = data?.data
        if (requestCode == record.REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(
                    this, """
     Video saved to:
     $videouri
     """.trimIndent(), Toast.LENGTH_LONG
                ).show()
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(
                    this, "Video recording cancelled.",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    this, "Failed to record video",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    }

    companion object {
        private const val REQUEST_CODE = 101
    }

//    override fun onClick(v: View?) {
//    }

}




