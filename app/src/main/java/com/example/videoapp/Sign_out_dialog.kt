package com.example.videoapp

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.sign_out_dialog.*

class Sign_out_dialog : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_out_dialog)

        val dialog =  Dialog(this);

        dialog.requestWindowFeature(Window.FEATURE_RIGHT_ICON);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.sign_out_dialog);
        dialog.show()

        if (log_out.isClickable){
            FirebaseAuth.getInstance().signOut()
            val i = Intent(this,
                MainActivity::class.java
            )
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)


        }
        else{
            dialog.dismiss()
        }
    }

}