package com.example.videoapp

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile_activity.*
import kotlinx.android.synthetic.main.fragment_save_image.*
import kotlinx.android.synthetic.main.fragment_save_image.view.*
import kotlinx.android.synthetic.main.fragment_save_image.view.set_image
import java.io.File
import java.io.FileOutputStream

class Profile_activity : AppCompatActivity() {

    lateinit var fragmentView: View
    var firestore: FirebaseFirestore? = null
    var uid: String? = null
    var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_activity)

        val users = FirebaseAuth.getInstance().currentUser?.email
        user_name.text = users.toString()

        profile_image.setOnClickListener {
            var photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(
                photoPickerIntent,
                ProfileFragment.PICK_PROFILE_FROM_ALBUM
            )
        }

        getProfileImage()
    }
    fun getProfileImage(){
        firestore?.collection("profileImages")?.document(uid!!)?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            if(documentSnapshot == null) return@addSnapshotListener
                var url = documentSnapshot.data!!["image"]
                Glide.with(applicationContext).load(url).apply(RequestOptions().circleCrop()).into(profile_image)
        }
    }
}
