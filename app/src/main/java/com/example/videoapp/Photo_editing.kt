package com.example.videoapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.PopupMenu
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_photo_editing.*
import java.io.*


class Photo_editing : AppCompatActivity() {
    var PICK_IMAGE_FROM_ALBUM = 0
    var storage: FirebaseStorage? = null
    var photoUri: Uri? = null

    @SuppressLint("Range", "SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_editing)
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM)

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sk: SeekBar?, progress: Int, fromUser: Boolean) {
                text1.textSize = progress.toFloat()
                text2.textSize = progress.toFloat()

            }

            override fun onStartTrackingTouch(sk: SeekBar?) {
            }

            override fun onStopTrackingTouch(sk: SeekBar?) {


            }

        })


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_FROM_ALBUM) {
            if (resultCode == Activity.RESULT_OK) {
                //This is path to the selected image
                photoUri = data?.data
                imageView.setImageURI(photoUri)

            } else {
                //Exit the addPhotoActivity if you leave the album without selecting it
                finish()

            }
        }
    }


    @SuppressLint("Range")
    fun colors(v: View) {
        val popup = PopupMenu(this, v).apply {
            inflate(R.menu.options)

            show()


        }
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.red -> {
                    background_color.setBackgroundColor(Color.parseColor("Red"))
                }
                R.id.white -> {
                    background_color.setBackgroundColor(Color.parseColor("White"))
                }
                R.id.yellow -> {
                    background_color.setBackgroundColor(Color.parseColor("Yellow"))
                }
                R.id.blue -> {
                    background_color.setBackgroundColor(Color.parseColor("Blue"))
                }
                R.id.light_blue -> {
                    background_color.setBackgroundColor(Color.parseColor("#169FE1"))
                }
                R.id.green -> {
                    background_color.setBackgroundColor(Color.parseColor("Green"))
                }
                R.id.black -> background_color.setBackgroundColor(Color.parseColor("Black"))
            }
            true

        }

    }

    @SuppressLint("Range")
    fun style(v: View) {
        val popup = PopupMenu(this, v).apply {
            inflate(R.menu.positions)

            show()


        }
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.top -> {
                    text1.setTextAppearance(applicationContext, R.style.Bold)
                }
                R.id.middle -> {
                    text2.setTextAppearance(applicationContext, R.style.Bold)
                }
                R.id.full -> {
                    text1.setTextAppearance(applicationContext, R.style.Normal)
                }
                R.id.normal -> {
                    text2.setTextAppearance(applicationContext, R.style.Normal)
                }
            }
            true

        }

    }


    @SuppressLint("Range")
    fun text_color(v: View) {
        val popup = PopupMenu(this, v).apply {
            inflate(R.menu.options)

            show()


        }
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.red -> {
                    text1.setTextColor(Color.parseColor("Red"))
                    text2.setTextColor(Color.parseColor("Red"))
                    text1.setHintTextColor(Color.parseColor("Red"))
                    text2.setHintTextColor(Color.parseColor("Red"))
                }
                R.id.white -> {
                    text1.setTextColor(Color.parseColor("White"))
                    text2.setTextColor(Color.parseColor("White"))
                    text1.setHintTextColor(Color.parseColor("White"))
                    text2.setHintTextColor(Color.parseColor("White"))
                }
                R.id.yellow -> {
                    text1.setTextColor(Color.parseColor("Yellow"))
                    text2.setTextColor(Color.parseColor("Yellow"))
                    text1.setHintTextColor(Color.parseColor("Yellow"))
                    text2.setHintTextColor(Color.parseColor("Yellow"))
                }
                R.id.blue -> {
                    text1.setTextColor(Color.parseColor("Blue"))
                    text2.setTextColor(Color.parseColor("Blue"))
                    text1.setHintTextColor(Color.parseColor("Blue"))
                    text2.setHintTextColor(Color.parseColor("Blue"))

                }
                R.id.light_blue -> {
                    text1.setTextColor(Color.parseColor("#169FE1"))
                    text2.setTextColor(Color.parseColor("#169FE1"))
                    text1.setHintTextColor(Color.parseColor("#169FE1"))
                    text2.setHintTextColor(Color.parseColor("#169FE1"))
                }
                R.id.green -> {
                    text1.setTextColor(Color.parseColor("Green"))
                    text2.setTextColor(Color.parseColor("Green"))
                    text1.setHintTextColor(Color.parseColor("Green"))
                    text2.setHintTextColor(Color.parseColor("Green"))
                }
                R.id.black -> {
                    text1.setTextColor(Color.parseColor("Black"))
                    text2.setTextColor(Color.parseColor("Black"))
                    text1.setHintTextColor(Color.parseColor("Black"))
                    text2.setHintTextColor(Color.parseColor("Black"))
                }
            }
            true

        }

    }

}
