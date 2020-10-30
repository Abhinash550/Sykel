package com.example.videoapp

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.ScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.videoapp.Model.AlarmDTO
import com.example.videoapp.Model.ContentDTOs
import com.example.videoapp.Model.FcmPush
import com.example.videoapp.Model.Users
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.SimpleExoPlayerView
import com.google.android.exoplayer2.upstream.DefaultAllocator
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_blank.*
import kotlinx.android.synthetic.main.fragment_blank.view.*
import kotlinx.android.synthetic.main.video_item.*
import kotlinx.android.synthetic.main.video_item.view.*

/**
 * A simple [Fragment] subclass.
 */
class BlankFragment : Fragment() {
    var firestore : FirebaseFirestore? = null
    var uid : String? = null
    lateinit var  mUser: List<Users>

    lateinit var mAuth: FirebaseAuth
    lateinit var comment_btn_send: FirebaseAuth
    private var isplay = false
     lateinit var exoplayer:ExoPlayer
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var fragmentview = LayoutInflater.from(activity).inflate(R.layout.fragment_blank,container,false)
        firestore = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid




//        fragmentview.scroll.fullScroll(ScrollView.FOCUS_DOWN)
        fragmentview.videosfragment?.setOnClickListener {
            var fragment = BlankFragment()
            activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.frag, fragment)
                ?.commit()

        }

        fragmentview.memesfragment?.setOnClickListener {
            var fragment = DetailViewFragment()
            activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.frag, fragment)
                ?.commit()

        }



        fragmentview.watchvideorecyclerviews.adapter = this.WatchviewRecyclerviewAdapters()
        fragmentview.watchvideorecyclerviews.layoutManager = LinearLayoutManager(activity)

        return fragmentview

    }

    fun ScrollView.scrollToBottom() {
        val lastChild = getChildAt(childCount - 1)
        val bottom = lastChild.bottom + paddingBottom
        val delta = bottom - (scrollY+ height)
        smoothScrollBy(0, delta)
    }

    inner class WatchviewRecyclerviewAdapters : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var contentDTOs: ArrayList<ContentDTOs> = arrayListOf()
        var contentUidList: ArrayList<String> = arrayListOf()


        init {



            firestore?.collection("videos")?.orderBy("timestamp")
                ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    contentDTOs.clear()
                    contentUidList.clear()
                    //Sometimes, This code return null of querySnapshot when it signout
                    if (querySnapshot == null) return@addSnapshotListener

                    watchvideorecyclerviews.smoothScrollBy(0, 1);
                    for (snapshot in querySnapshot.documents) {
                        var item = snapshot.toObject(ContentDTOs::class.java)
                        contentDTOs.add(item!!)
                        contentUidList.add( snapshot.id)
                    }
                    notifyDataSetChanged()
                }
//            adapter.updateRow(adapter.getItem(position), convertView, object)
        }


        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(p0.context).inflate(R.layout.video_item, p0, false)


            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var alarmDTOList: ArrayList<AlarmDTO> = arrayListOf()

        }


        override fun getItemCount(): Int {
            return contentDTOs.size
        }


        @SuppressLint("SetTextI18n", "ResourceAsColor", "SourceLockedOrientationActivity")
        override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
            var viewholder = (p0 as CustomViewHolder).itemView




//            viewholder.set(Uri.parse(String.valueOf(videoUri)),model.getVideo());
            FirebaseFirestore.getInstance().collection("profileImages")
                .document(contentDTOs[p1].uid!!).get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val urls = task.result!!["image"]
                        if (activity == null) {
                            return@addOnCompleteListener
                        } else {
                            Glide.with(view?.context).load(urls)
                                .apply(RequestOptions().circleCrop())
                                .into(
                                    detailviewitem_Video_profile_image
                                )
                        }
                    }
                }



//            val views = viewholder.findViewById<VideoView>(R.id.detailviewitem_video_content)
//            val uri = Uri.parse(path)
//            views.setVideoURI(uri)
//            views.requestFocus()
//            views.setOnPreparedListener {
//                views.start()
//            }

            val uid = FirebaseAuth.getInstance().currentUser?.uid
                val mc = MediaController(context)
//                val videouri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/video-app-af9bf.appspot.com/o/Videos%2FVideo_20200809_153346_.mp4?alt=media&token=36f2e2c5-08c6-4503-9fc1-d0cd13b5c4c9")


                val rootRef = FirebaseDatabase.getInstance().reference
                val uidRef = uid?.let { rootRef.child("Usuarios").child(it) }
                val eventListener: ValueEventListener = object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val namess: String? =
                            dataSnapshot.child("username").getValue(String::class.java)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                }
                uidRef?.addListenerForSingleValueEvent(eventListener)


                viewholder.detailviewitem_comment_videoview.setOnClickListener {
                    var intent = Intent(context, CommentActivity::class.java)
                    intent.putExtra("contentUid", contentUidList[p1])
                    startActivity(intent)
                }


            if (contentDTOs[p1].videourl != null){

                    //Minimum Video you want to buffer while Playing
                     val MIN_BUFFER_DURATION = 2000

                    //Max Video you want to buffer during PlayBack
                     val MAX_BUFFER_DURATION = 5000

                    //Min Video you want to buffer before start Playing it
                     val MIN_PLAYBACK_START_BUFFER = 1500

                    //Min video You want to buffer when user resumes video
                     val MIN_PLAYBACK_RESUME_BUFFER = 2000

//                val builder: DefaultLoadControl.Builder = Control.Builder()
//                val loadControl: LoadControl =Builder()
//                    .setAllocator(DefaultAllocator(true, 16))
//                    .setBufferDurationsMs(
//                        MIN_BUFFER_DURATION,
//                        MAX_BUFFER_DURATION,
//                        MIN_PLAYBACK_START_BUFFER,
//                        MIN_PLAYBACK_RESUME_BUFFER
//                    )
//                    .setTargetBufferBytes(-1)
//                    .setPrioritizeTimeOverSizeThresholds(true).createDefaultLoadControl()
                val path = Uri.parse(contentDTOs[p1].videourl)
                val view = viewholder.findViewById<SimpleExoPlayerView>(R.id.detailviewitem_video_contents)
//        view.setVideoURI(uri)
//        view.start()
                val loadControl = DefaultLoadControl(
                    DefaultAllocator(true, 1024),
                    2000, // this is it!
                    5000,
                    1500,
                    2000,
                    1024,
                    true
                )
                val bandwidthMeter= DefaultBandwidthMeter()
                val trackSelector = DefaultTrackSelector(AdaptiveTrackSelection.Factory(bandwidthMeter))
                val exoplayer = ExoPlayerFactory.newSimpleInstance(context,trackSelector)
                val datasourcefactory = DefaultHttpDataSourceFactory("exoplayer")
                val extractorsFactory= DefaultExtractorsFactory()
//                (context as Activity).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT)
                val decorView: View? = activity?.window?.getDecorView()


//                this@BlankFragment.activity?.getWindow()?.getDecorView()
//                    ?.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
                val media = ExtractorMediaSource(path,datasourcefactory,extractorsFactory,null,null)
                view.player = exoplayer
                exoplayer.prepare(media)
//                var SimpleExoPlayer.playbackSpeed: Float
//                get() = playbackParameters?.speed ?: 1f
//                set(speed) {
//                    val pitch = playbackParameters?.pitch ?: 1f
//                    playbackParameters = PlaybackParameters(speed, pitch)
//
                exoplayer.playWhenReady = true


//                decorView?.systemUiVisibility = ( // Hide the nav bar and status bar
                view.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
//                exoplayer.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
//                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

//                val b = viewholder.findViewById<Button>(R.id.vbtn)


            }


            viewholder.detailviewitem_explain_textview_video.text = contentDTOs[p1].explain

            //likes
            viewholder.detailviewitem_favoritecounter_textview_video.text =
                contentDTOs[p1].favoriteCount.toString()
            viewholder.detailviewitem_favorite_videoview.setOnClickListener {
                favoriteEvent(p1)
            }

            //This code is when the page is loaded
            if (contentDTOs[p1].favorites.containsKey(uid)) {
                //This is like status
                viewholder.detailviewitem_favorite_videoview.setImageResource(R.drawable.wow)

            } else {
                //This is unlike status
                viewholder.detailviewitem_favorite_videoview.setImageResource(R.drawable.meme3)
            }

            //This code is when the profile image is clicked
            viewholder.detailviewitem_Video_profile_image.setOnClickListener {
                var fragment = ProfileFragment()
                var bundle = Bundle()
                bundle.putString("destinationUid", contentDTOs[p1].uid)
                bundle.putString("userId", contentDTOs[p1].userId)
                fragment.arguments = bundle
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.frag, fragment)?.commit()
            }
            viewholder.detailviewitem_comment_videoview.setOnClickListener { v ->
                var intent = Intent(v.context, CommentActivity::class.java)
                intent.putExtra("contentUid", contentUidList[p1])
                intent.putExtra("destinationUid", contentDTOs[p1].uid)
                startActivity(intent)
            }

        }

        fun favoriteEvent(position: Int) {
            var tsDoc = firestore?.collection("videos")?.document(contentUidList[position])
            firestore?.runTransaction { transaction ->


                var contentDTOss = transaction.get(tsDoc!!).toObject(ContentDTOs::class.java)

                if (contentDTOss!!.favorites.containsKey(uid)) {
                    //When the button is clicked
                    contentDTOss.favoriteCount = contentDTOss.favoriteCount - 1
                    contentDTOss.favorites.remove(uid)
                } else {
                    //When the button is not clicked
                    contentDTOss.favoriteCount = contentDTOss.favoriteCount + 1
                    contentDTOss.favorites[uid!!] = true
                    favoriteAlarm(contentDTOs[position].uid!!)

                }
                transaction.set(tsDoc, contentDTOss)
            }
        }

        fun favoriteAlarm(destinationUid: String) {
            var alarmDTO = AlarmDTO()
            alarmDTO.destinationUid = destinationUid
            alarmDTO.userId = FirebaseAuth.getInstance().currentUser?.email
            alarmDTO.uid = FirebaseAuth.getInstance().currentUser?.uid
            alarmDTO.kind = 0
            alarmDTO.timestamp = System.currentTimeMillis()
            FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO)

            var message =
                FirebaseAuth.getInstance().currentUser?.email + getString(R.string.alarm_favorite)
            FcmPush.instance.sendMessage(destinationUid, "Howlstagram", message)
        }


    }

//    override fun onStart() {
//        super.onStart()
//        val views = view?.findViewById<VideoView>(R.id.detailviewitem_video_content)
//            val uri = Uri.parse(path)
//            views?.setVideoURI(uri)
//            views?.requestFocus()
//            views?.setOnPreparedListener {
//                views.start()
//            }
//
//    }
private var player: SimpleExoPlayer? = null
    override fun onPause() {
        super.onPause()

        // Pause video if it's playing
        if (isplay) {
            player?.setPlayWhenReady(false)
        }
    }
//    override fun onDestroy() {
//        releasePlayer()
//        super.onDestroy()
//    }

//    private fun releasePlayer() {
//        view.exoplayer.stop()
//        exoplayer.release()
////        exoplayer? = null
//    }


}


//This code is when the button is clicked

