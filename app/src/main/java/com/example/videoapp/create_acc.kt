package com.example.videoapp

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_create_acc.*


@Suppress("NAME_SHADOWING")
class create_acc : AppCompatActivity() {

    lateinit var mAuth: FirebaseAuth
    lateinit var mDatabase: DatabaseReference
    lateinit var mProgressBar: ProgressDialog
    var RC_SIGN_IN = 9001
   lateinit var googleSignInClient : GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_acc)
        mAuth=FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().getReference("Users")
        val mRegUsername = findViewById<EditText>(R.id.username)
        val mRegEmail = findViewById<EditText>(R.id.email)
        val mRegPassword=findViewById<EditText>(R.id.password)
        mProgressBar= ProgressDialog(this)


        creates.setOnClickListener {
            val name = mRegUsername.text.toString().trim()
            val password = mRegPassword.text.toString().trim()
            val email = mRegEmail.text.toString().trim()

            if (TextUtils.isEmpty(name)){
                mRegUsername.error = "Enter name"
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(email)){
                mRegEmail.error = "Enter email"
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(password)){
                mRegPassword.error = "Enter password"
                return@setOnClickListener
            }

            createUser(name,email,password)





        }
        val gso =  GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        val signInButton: SignInButton = findViewById(R.id.google_btn)
        signInButton.setSize(SignInButton.SIZE_WIDE)
        signInButton.setOnClickListener {
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
        val acct = GoogleSignIn.getLastSignedInAccount(this)
        if (acct != null) {
            signInButton.visibility = View.VISIBLE
        }
    }





    private fun createUser(name: String, email: String, password: String) {
        mProgressBar.setMessage("Please wait.....")
        val ref= FirebaseDatabase.getInstance().getReference("Users")
        val heroId=ref.push().key
        val image="https://firebasestorage.googleapis.com/v0/b/video-app-af9bf.appspot.com/o/Default%20Images%2Fprofile2.png?alt=media&token=27549ff3-9a3d-4f5a-a022-7fad0a37ef50"
        val hero = Hero(heroId,name,image)
        ref.child(heroId.toString()).setValue(hero).addOnCompleteListener {
            Toast.makeText(applicationContext,"User profile save successfully",Toast.LENGTH_SHORT).show()
            finish()


            mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val intent = Intent(applicationContext, video::class.java)
                    startActivity(intent)
                    finish()

                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                        this, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    mProgressBar.dismiss()
                }

                mProgressBar.show()

            }
    }}

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task: com.google.android.gms.tasks.Task<GoogleSignInAccount>? = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }
    private fun handleSignInResult(completedTask: com.google.android.gms.tasks.Task<GoogleSignInAccount>?) {
        try {

            startActivity(Intent(applicationContext,video::class.java))
            // Signed in successfully, show authenticated UI.
            SignInButton.VISIBLE
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Toast.makeText(this,"Authentication failed",Toast.LENGTH_SHORT).show()
            SignInButton.VISIBLE

    }
}}
