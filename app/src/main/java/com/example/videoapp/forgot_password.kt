package com.example.videoapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth

class forgot_password : AppCompatActivity() {

    lateinit var mEmail : EditText
    lateinit var mForgotbtn : Button
    lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        mEmail = findViewById(R.id.send_email)
        mForgotbtn = findViewById(R.id.button)
        mAuth = FirebaseAuth.getInstance()

        mForgotbtn.setOnClickListener {
            val email = mEmail.text.toString().trim()
            if (email.isEmpty()){
                mEmail.error = "Enter Email"
                return@setOnClickListener
            }
            forgetPassword(email)
        }
    }

    private fun forgetPassword(email: String) {
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener { task: Task<Void> ->
            if (task.isComplete){
                startActivity(Intent(this,MainActivity::class.java))
                finish()
                Toast.makeText(applicationContext,"Please check your inbox , we have sent you a mail.",Toast.LENGTH_LONG).show()


            }
        }

    }
}

