package com.example.videoapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_account_setting.*
import java.util.*

class account_setting : AppCompatActivity() {
    lateinit var mAuth: FirebaseAuth
    lateinit var first : EditText
    lateinit var last : EditText



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_setting)
        mAuth=FirebaseAuth.getInstance()

        first = findViewById(R.id.firstname)
        last = findViewById(R.id.lastname)
        cancel.setOnClickListener {
            startActivity(Intent(this,video::class.java))
        }


        save_change.setOnClickListener {
            saveUserinfo()

        }
        logout.setOnClickListener {

            FirebaseAuth.getInstance().signOut()

        }

    }

    @SuppressLint("DefaultLocale")
    private fun saveUserinfo() {
        val name = firstname.text.toString().trim().toLowerCase()
        val lastnames = lastname.text.toString().trim()

        if (name.isEmpty()){
            firstname.error = "Enter user name"
            return
        }
        if (lastnames.isEmpty()){
            lastname.error = "Enter unique name"
            return
        }


    }
    }









