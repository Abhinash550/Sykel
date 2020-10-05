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
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.videoapp.Model.ContentDTOs
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_record.*
import java.text.SimpleDateFormat
import java.util.*


class record : AppCompatActivity() {
    private var videouri: Uri? = null
    private var videoref: StorageReference? = null
    lateinit var mVideoView: VideoView
    var firestore: FirebaseFirestore? = null
    var auth: FirebaseAuth? = null


    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val storageRef = FirebaseStorage.getInstance().reference
        videoref = storageRef.child("/videos" + "/userIntro.3gp")

        val dialog =  Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_RIGHT_ICON);
        dialog.setCancelable(false);
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(R.layout.design);
        dialog.show()

        records()

        button3?.setOnClickListener {

            upload()
        }
    }

    fun records() {
        mVideoView = findViewById(R.id.videoView)
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        startActivityForResult(intent, REQUEST_CODE)
    }
    fun pick(v:View) {
        mVideoView = findViewById(R.id.videoView)
        val intent =  Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODE)
    }

    @SuppressLint("ShowToast")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int, data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        videouri = data?.data
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                val videoUri = data?.data
                mVideoView.setVideoURI(videoUri)
                mVideoView.requestFocus()
                mVideoView.start()
                Toast.makeText(
                    this, """
     Video saved to:
     $videouri
     """.trimIndent(), Toast.LENGTH_LONG
                ).show()
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(
                    this, "Video recording cancelled.", Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    this, "Failed to record video",
                    Toast.LENGTH_LONG
                ).show()

            }



        }

    }

    @SuppressLint("SimpleDateFormat")
    fun upload() {
        if (videouri != null) {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val videoFileName = "Video_" + timestamp + "_.3gp"
            val storageRef = FirebaseStorage.getInstance().reference.child("Videos").child(videoFileName)
            videoref = storageRef.child("/videos" + "/userIntro.3gp")

            storageRef.putFile(videouri!!).continueWithTask { task: Task<UploadTask.TaskSnapshot> ->
                return@continueWithTask storageRef.downloadUrl
            }.addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Upload failed: " + e.localizedMessage,
                    Toast.LENGTH_LONG
                ).show()

                videoref?.downloadUrl
            }.addOnSuccessListener { uri->
                Toast.makeText(
                    this, "Upload completed",
                    Toast.LENGTH_LONG
                ).show()

                val contentDTOs = ContentDTOs()
                contentDTOs.videourl = uri.toString()

                //Insert uid of user
                contentDTOs.uid = auth?.currentUser?.uid

                //Insert userId
                contentDTOs.userId = auth?.currentUser?.email
                contentDTOs.username = auth?.currentUser?.displayName

                //Insert explain of content
                contentDTOs.explain = addvideo_explain_explains?.text.toString()

                //Insert timestamp
                contentDTOs.timestamp = System.currentTimeMillis()

                firestore?.collection("videos")?.document()?.set(contentDTOs)

                setResult(Activity.RESULT_OK)

                finish()



            }
            videoref?.putFile(videouri!!)?.addOnProgressListener { taskSnapshot -> updateProgress(taskSnapshot) }
        } else {
            Toast.makeText(
                this, "Nothing to upload",
                Toast.LENGTH_LONG
            ).show()
        }




    }

    fun updateProgress(taskSnapshot: UploadTask.TaskSnapshot) {
        val fileSize = taskSnapshot.totalByteCount
        val uploadBytes = taskSnapshot.bytesTransferred
        val progress = 100 * uploadBytes / fileSize
        val progressBar = findViewById<View>(R.id.pbar) as ProgressBar
        progressBar.progress = progress.toInt()
    }

    companion object {
        const val REQUEST_CODE: Int = 101
    }

}





