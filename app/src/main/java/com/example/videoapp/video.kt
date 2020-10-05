package com.example.videoapp

import android.Manifest
import android.Manifest.permission.CAMERA
import android.app.Activity
import android.app.PendingIntent.getActivity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_video.*


class video : AppCompatActivity() {
    lateinit var DetailViewFragment: DetailViewFragment
    lateinit var notificationFragment: NotificationFragment
    lateinit var tutorialFragment: TutorialFragment
    lateinit var exploreFragment: ExploreFragment
    lateinit var profileFragment: ProfileFragment
    lateinit var mAuth: FirebaseAuth
    private var PERMISSION_REQUEST = 10



    private val permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)



    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        fabs?.setOnClickListener {

                startActivity(Intent(this,record::class.java))
            }


        mAuth=FirebaseAuth.getInstance()






                    replace(DetailViewFragment())


        val bottom_navigation = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottom_navigation.setOnNavigationItemSelectedListener {
            item ->
            when(item.itemId){
                R.id.home -> {
                    replace(DetailViewFragment())

                }
//                R.id.tutorial -> {
//                        replace(TutorialFragment())
//                }
//                R.id.explore -> {
//                    replace(ExploreFragment())
//
//                }
                R.id.notificaton -> {
                    replace(NotificationFragment())

                }
                R.id.profile -> {
                    replace(save_image())
                    var bundle = Bundle()
                    var uid = FirebaseAuth.getInstance().currentUser?.uid
                    var profile = save_image()

                    bundle.putString("destinationUid",uid)
                    profile.arguments = bundle

                }

            }
            registerPushToken()

            return@setOnNavigationItemSelectedListener true
        }


    }

//   override fun onRequestPermissionsResult(
//       requestCode: Int,
//       permissions: Array<out String>,
//       grantResults: IntArray
//   ) {
//        if (requestCode == REQUEST_WRITE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            startActivity(Intent(this,add_photos::class.java))
//
//        }
//    }
//
//    fun  requestPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            requestPermissions(
//                arrayOf({ android.Manifest.permission.WRITE_EXTERNAL_STORAGE }.toString()),
//                REQUEST_WRITE_PERMISSION
//            );
//        } else {
//            startActivity(Intent(this,add_photos::class.java))
//        }
//    }

    override fun onStart() {
        super.onStart()
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser==null){
            val intent = Intent(applicationContext,video::class.java)
            startActivity(intent)
            finish()
        }
        else{


        }

    }

     fun replace(fragment: Fragment){
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frag,fragment)
        fragmentTransaction.commit()
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == ProfileFragment.PICK_PROFILE_FROM_ALBUM && resultCode == Activity.RESULT_OK){
            var imageUri = data?.data
            var uid = FirebaseAuth.getInstance().currentUser?.uid
            var storageRef = FirebaseStorage.getInstance().reference.child("userProfileImages").child(uid!!)
            storageRef.putFile(imageUri!!).continueWithTask { task: Task<UploadTask.TaskSnapshot> ->
                return@continueWithTask storageRef.downloadUrl
            }.addOnSuccessListener { uri ->
                var map = HashMap<String,Any>()
                map["image"] = uri.toString()
                FirebaseFirestore.getInstance().collection("profileImages").document(uid).set(map)
            }
        }

    }

    fun registerPushToken(){
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener {
                task ->
            val token = task.result?.token
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            val map = mutableMapOf<String,Any>()
            map["pushToken"] = token!!

            FirebaseFirestore.getInstance().collection("pushtokens").document(uid!!).set(map)
        }


}


}



