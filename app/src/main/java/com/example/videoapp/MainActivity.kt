package com.example.videoapp

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.PatternsCompat
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_create_acc.*
import kotlinx.android.synthetic.main.activity_create_acc.accnt
import kotlinx.android.synthetic.main.activity_main.*
import java.util.regex.Pattern


class MainActivity : AppCompatActivity() {
    lateinit var mAuth: FirebaseAuth
    lateinit var mProgressBar2: ProgressDialog


    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val mRegEmail2 = findViewById<EditText>(R.id.email2)
        val mRegPassword2 = findViewById<EditText>(R.id.pass)
        mProgressBar2 = ProgressDialog(this)
        mAuth=FirebaseAuth.getInstance()

        accnt.setOnClickListener {
            startActivity(Intent(this, create_acc::class.java))

        }
        forgot.setOnClickListener {

            startActivity(Intent(this,forgot_password::class.java))

        }
        login.setOnClickListener {
            val password2 = mRegPassword2.text.toString().trim()
            val email2 = mRegEmail2.text.toString().trim()

            if (TextUtils.isEmpty(email2)) {
                mRegEmail2.error = "Enter email"
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(password2)) {
                mRegPassword2.error = "Enter password"
                return@setOnClickListener

            }

            loginUser(email2,password2)

        }


    }


    private fun loginUser(email2: String, password2: String) {
        mProgressBar2.setMessage("Please wait.... ")
        mProgressBar2.show()
        mAuth.signInWithEmailAndPassword(email2, password2)
            .addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
                    mProgressBar2.dismiss()
                    val intent = Intent(applicationContext,video::class.java)
                    startActivity(intent)
                    finish()
                    Toast.makeText(this,"Login Successful",Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText(this,"Authentication failed",Toast.LENGTH_SHORT).show()
                }
                mProgressBar2.dismiss()


                // ...
            }
    }
    override fun onStart() {
        super.onStart()
        if (FirebaseAuth.getInstance().currentUser!=null){
            val intent = Intent(applicationContext,video::class.java)
            startActivity(intent)
            finish()

        }
    }

}



