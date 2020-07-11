package com.example.videoapp

import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent.getActivity
import android.content.Intent
import android.content.pm.PackageManager
import android.media.session.MediaController
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.FileProvider.getUriForFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_create_acc.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_home.video_view
import kotlinx.android.synthetic.main.activity_video.*
import kotlinx.android.synthetic.main.fragment_profile.*
import java.io.File
import java.security.Provider

class video : AppCompatActivity() {
    lateinit var DetailViewFragment: DetailViewFragment
    lateinit var notificationFragment: NotificationFragment
    lateinit var tutorialFragment: TutorialFragment
    lateinit var exploreFragment: ExploreFragment
    lateinit var profileFragment: ProfileFragment
    lateinit var mAuth: FirebaseAuth





    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        mAuth=FirebaseAuth.getInstance()

        fabs?.setOnClickListener {
            startActivity(Intent(this,choose::class.java))
        }


        replace(DetailViewFragment())

        val bottom_navigation = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottom_navigation.setOnNavigationItemSelectedListener {
            item ->
            when(item.itemId){
                R.id.home -> {
                    replace(DetailViewFragment())

                }
                R.id.tutorial -> {
                        replace(TutorialFragment())
                }
                R.id.explore -> {
                    replace(ExploreFragment())

                }
                R.id.notificaton -> {
                    replace(NotificationFragment())

                }
                R.id.profile -> {
                    replace(ProfileFragment())
                    var bundle = Bundle()
                    var uid = FirebaseAuth.getInstance().currentUser?.uid
                    var profile = ProfileFragment()

                    bundle.putString("destinationUid",uid)
                    profile.arguments = bundle

                }

            }
            return@setOnNavigationItemSelectedListener true
        }


    }
    override fun onStart() {
        super.onStart()
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser==null){
            val intent = Intent(applicationContext,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        else{


        }
    }








    private fun replace(fragment: Fragment){
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frag,fragment)
        fragmentTransaction.commit()
    }




}



