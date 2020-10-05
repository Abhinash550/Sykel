package com.example.videoapp

import android.accessibilityservice.GestureDescription
import android.annotation.SuppressLint
import android.app.Application
import android.app.Notification
import android.content.Intent
import android.content.res.Configuration
import android.media.VolumeShaper
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.renderscript.Type
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.videoapp.Model.*
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.LoadControl
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelector
import com.google.android.exoplayer2.upstream.DefaultAllocator
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.api.Http
import com.google.api.Http.Builder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_watch_video.view.*
import kotlinx.android.synthetic.main.video_item.*
import kotlinx.android.synthetic.main.video_item.view.*
import okhttp3.internal.http2.Http2Connection
import java.util.*
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 */
@Suppress("UNREACHABLE_CODE")
class Watch_video : Fragment() {
    var firestore: FirebaseFirestore? = null
    var uid: String? = null
    lateinit var mUser: List<Users>

    lateinit var mAuth: FirebaseAuth
    lateinit var detailviewitem_video_contents: FirebaseAuth
    private var path = "https://firebasestorage.googleapis.com/v0/b/video-app-af9bf.appspot.com/o/Videos%2FVideo_20200812_141638_.mp4?alt=media&token=d87f94e0-6ca3-4de2-856c-b228884185f7"
    @SuppressLint("ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val fragmentview = inflater.inflate(R.layout.fragment_watch_video, container, false)
        fragmentview.videofragment?.setOnClickListener {
            var fragment = Watch_video()
            activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.frag, fragment)
                ?.commit()

        }
        fragmentview.memefragment?.setOnClickListener {
            var fragment = DetailViewFragment()
            activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.frag, fragment)
                ?.commit()

        }


        firestore = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid

        fragmentview.videofragment?.setOnClickListener {
            var fragment = Watch_video()
            activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.frag, fragment)
                ?.commit()

        }
//

//        if (Videoview != null) {
//            Videoview.setOnPreparedListener {
////                val placeholder = view?.findViewById<View>(R.id.placeholder)
////                if (placeholder != null) {
////                    placeholder.visibility = View.GONE
//                }
//            }


        fragmentview.memefragment?.setOnClickListener {
            var fragment = DetailViewFragment()
            activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.frag, fragment)
                ?.commit()

        }

        fragmentview.swiperefresh?.setOnRefreshListener {
            fragmentview.watchvideorecyclerview.adapter = WatchviewRecyclerviewAdapter()
            (fragmentview.watchvideorecyclerview.adapter as WatchviewRecyclerviewAdapter).notifyDataSetChanged()
            fragmentview.watchvideorecyclerview.layoutManager = LinearLayoutManager(activity)
            (fragmentview.watchvideorecyclerview.adapter as WatchviewRecyclerviewAdapter).notifyDataSetChanged()

            fragmentview.swiperefresh?.isRefreshing =
                false   // reset the SwipeRefreshLayout (stop the loading spinner)
        }



        fragmentview.watchvideorecyclerview.adapter = this.WatchviewRecyclerviewAdapter()
        fragmentview.watchvideorecyclerview.layoutManager = LinearLayoutManager(activity)
        return fragmentview

    }


    inner class WatchviewRecyclerviewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var contentDTOs: ArrayList<ContentDTOs> = arrayListOf()
        var contentUidList: ArrayList<String> = arrayListOf()


        init {


            firestore?.collection("videos")?.orderBy("timestamp")
                ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    contentDTOs.clear()
                    contentUidList.clear()
                    //Sometimes, This code return null of querySnapshot when it signout
                    if (querySnapshot == null) return@addSnapshotListener

                    for (snapshot in querySnapshot.documents) {
                        var item = snapshot.toObject(ContentDTOs::class.java)
                        contentDTOs.add(item!!)
                        contentUidList.add(0, snapshot.id)
                    }
                    notifyDataSetChanged()
                }

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
//        var mView: View? = null
//        var post_video: Videoview? = null
//
//        fun ViewHolder(itemView: View) {
//           val mView = itemView
//            post_video = mView.findViewById<View>(R.id.vid) as VideoView
//        }

//        StorageTask<UploadTask.TakeSnapshot> storageTask;
//        storageTask = yourStorageRefernce.putFile(videoUri);


        @RequiresApi(Build.VERSION_CODES.N)
        @SuppressLint("SetTextI18n", "ResourceAsColor")
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
            if (contentDTOs[p1].videourl != null) {
                val mc = MediaController(context)
//                val videouri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/video-app-af9bf.appspot.com/o/Videos%2FVideo_20200809_153346_.mp4?alt=media&token=36f2e2c5-08c6-4503-9fc1-d0cd13b5c4c9")
                val uri: Uri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/video-app-af9bf.appspot.com/o/Videos%2FVideo_20200812_141638_.mp4?alt=media&token=d87f94e0-6ca3-4de2-856c-b228884185f7")
//                v.setVideoURI(uri)
//                v.start()
//
//                val dataSourceFactory = DefaultHttpDataSourceFactory("videos")
//                val application : Application = Application()
//                val extractorsFactory= DefaultExtractorsFactory()
//                val player = ExoPlayerFactory.newSimpleInstance(context, trackSelector, loadControl)
//                val media = ExtractorMediaSource(uri,dataSourceFactory,extractorsFactory,null)
//

                val MIN_BUFFER_DURATION = 2000
//Max Video you want to buffer during PlayBack
//Max Video you want to buffer during PlayBack
                val MAX_BUFFER_DURATION = 5000
//Min Video you want to buffer before start Playing it
//Min Video you want to buffer before start Playing it
                val MIN_PLAYBACK_START_BUFFER = 1500
//Min video You want to buffer when user resumes video
//Min video You want to buffer when user resumes video
                val MIN_PLAYBACK_RESUME_BUFFER = 2000

//                val loadControl: LoadControl =Builder.setAllocator(DefaultAllocator(true, 16))
//                    .setBufferDurationsMs(
//                        MIN_BUFFER_DURATION,
//                        MAX_BUFFER_DURATION,
//                        MIN_PLAYBACK_START_BUFFER,
//                        MIN_PLAYBACK_RESUME_BUFFER
//                    )
//                    .setTargetBufferBytes(-1)
//                    .setPrioritizeTimeOverSizeThresholds(true).createDefaultLoadControl()
//
//                val trackSelector: TrackSelector = DefaultTrackSelector()
//               val player = ExoPlayerFactory.newSimpleInstance(context, trackSelector, loadControl)

//                detailviewitem_video_content.player =
//                mc.setAnchorView(detailviewitem_video_content)
//                mc.setMediaPlayer(detailviewitem_video_content)
//                detailviewitem_video_content.setVideoURI(uri)
//                detailviewitem_video_content.requestFocus()
//                detailviewitem_video_content.start()



                val trackSelector = DefaultTrackSelector()
                val loadControl = DefaultLoadControl()
                val renderersFactory = DefaultRenderersFactory(context)

                val exoPlayer = ExoPlayerFactory.newSimpleInstance(
                    renderersFactory, trackSelector, loadControl)
                val userAgent = Util.getUserAgent(context, context?.getString(R.string.app_name))
                //2
                val mediaSource = ExtractorMediaSource
                    .Factory(DefaultDataSourceFactory(context, userAgent))
                    .setExtractorsFactory(DefaultExtractorsFactory())
                    .createMediaSource(Uri.parse(contentDTOs[p1].videourl))
                //3
                exoPlayer.prepare(mediaSource)
                //4
                exoPlayer.playWhenReady = true


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


                var contentDTO = transaction.get(tsDoc!!).toObject(ContentDTO::class.java)

                if (contentDTO!!.favorites.containsKey(uid)) {
                    //When the button is clicked
                    contentDTO?.favoriteCount = contentDTO?.favoriteCount - 1
                    contentDTO?.favorites.remove(uid)
                } else {
                    //When the button is not clicked
                    contentDTO?.favoriteCount = contentDTO?.favoriteCount + 1
                    contentDTO?.favorites[uid!!] = true
                    favoriteAlarm(contentDTOs[position].uid!!)

                }
                transaction.set(tsDoc, contentDTO)
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
}