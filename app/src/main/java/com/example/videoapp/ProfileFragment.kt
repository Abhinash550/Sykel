// Profile Fragment of app

package com.example.videoapp

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.PorterDuff
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.videoapp.Model.AlarmDTO
import com.example.videoapp.Model.ContentDTO
import com.example.videoapp.Model.FcmPush
import com.example.videoapp.Model.FollowDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_create_acc.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.fragment_profile.view.name
import kotlinx.android.synthetic.main.item_comment.view.*


@Suppress("UNREACHABLE_CODE")
class ProfileFragment : Fragment() {
    var fragmentView : View? = null
    var firestore : FirebaseFirestore? = null
    var uid : String? = null
    var auth : FirebaseAuth? = null
    var currentUserUid : String? = null
    companion object {
        var PICK_PROFILE_FROM_ALBUM = 10
    }
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentView = inflater.inflate(R.layout.fragment_profile,container,false)
        uid = arguments?.getString("destinationUid")
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        currentUserUid = auth?.currentUser?.uid
        var comment = ContentDTO.Comment()
        var contentDTOs : ArrayList<ContentDTO> = arrayListOf()





        var user = FirebaseAuth.getInstance().currentUser
        var users = FirebaseAuth.getInstance().currentUser

        if(uid == currentUserUid){
            //MyPage
            val email: String? = user?.getEmail()
            fragmentView?.account_btn_follow_signout?.text = getString(R.string.signout)
            fragmentView?.name?.setText(email)

            fragmentView?.account_btn_follow_signout?.setOnClickListener {
                activity?.finish()
                startActivity(Intent(activity,MainActivity::class.java))
                auth?.signOut()
                }
        }

        if (uid!=currentUserUid){
            //OtherUserPage

            fragmentView?.account_btn_follow_signout?.text = getString(R.string.follow)
            fragmentView?.account_btn_follow_signout?.setOnClickListener {
//                Follow button
                requestFollow()
            }

        }
        fragmentView?.account_reyclerview?.adapter = UserFragmentRecyclerViewAdapter()
        fragmentView?.account_reyclerview?.layoutManager = GridLayoutManager(activity, 3)

        this.fragmentView?.account_iv_profile?.setOnClickListener {
//            Profile Image
            var photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            activity?.startActivityForResult(photoPickerIntent,PICK_PROFILE_FROM_ALBUM)
        }
        getProfileImage()
        getFollowerAndFollowing()

        return fragmentView

    }

    fun getFollowerAndFollowing(){
        uid?.let {
            firestore?.collection("users")?.document(it)?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                if(documentSnapshot == null) return@addSnapshotListener
                var followDTO = documentSnapshot.toObject(FollowDTO::class.java)
                if(followDTO?.followingCount != null){
                    fragmentView?.account_tv_following_count?.text = followDTO.followingCount.toString()
                }
                if(followDTO?.followerCount != null){
                    fragmentView?.account_tv_follower_count?.text = followDTO.followerCount.toString()
                    if(followDTO.followers.containsKey(currentUserUid!!)){
                        fragmentView?.account_btn_follow_signout?.text = activity?.getString(R.string.follow_cancel)
                        activity?.let { it1 -> ContextCompat.getColor(it1,R.color.browser_actions_text_color) }?.let { it2 ->
                            fragmentView?.account_btn_follow_signout?.background
                                ?.setColorFilter(it2,PorterDuff.Mode.MULTIPLY)
                        }
                    }else{
                        if(uid != currentUserUid){
                            fragmentView?.account_btn_follow_signout?.text = activity?.getString(R.string.follow)
                            fragmentView?.account_btn_follow_signout?.background?.colorFilter = null
                        }

                    }
                }
            }
        }
    }
    fun requestFollow(){
        //Save data to my account
        var tsDocFollowing = firestore?.collection("users")?.document(currentUserUid!!)
        firestore?.runTransaction { transaction ->
            var followDTO = transaction.get(tsDocFollowing!!).toObject(FollowDTO::class.java)
            if(followDTO == null){
                followDTO = FollowDTO()
                followDTO.followingCount = 1
                followDTO.followings[uid!!] = true

                transaction.set(tsDocFollowing, followDTO)
                return@runTransaction
            }

            if(followDTO.followings.containsKey(uid)){
                //It remove following third person when a third person follow me
                followDTO.followingCount = followDTO.followingCount - 1
                followDTO.followings.remove(uid)
            }else{
                //It add following third person when a third person do not follow me
                followDTO.followingCount = followDTO.followingCount + 1
                followDTO.followings[uid!!] = true
            }
            transaction.set(tsDocFollowing,followDTO)
            return@runTransaction
        }

        //Save data to third account

        var tsDocFollower = uid?.let { firestore?.collection("users")?.document(it) }
        firestore?.runTransaction { transaction ->
            var followDTO = transaction.get(tsDocFollower!!).toObject(FollowDTO::class.java)
            if(followDTO == null){
                followDTO = FollowDTO()
                followDTO!!.followerCount = 1
                followDTO!!.followers[currentUserUid!!] = true
                followerAlarm(uid!!)
                transaction.set(tsDocFollower,followDTO!!)
                return@runTransaction
            }

            if(followDTO!!.followers.containsKey(currentUserUid!!)){
                //It cancel my follower when I follow a third person
                followDTO!!.followerCount = followDTO!!.followerCount - 1
                followDTO!!.followers.remove(currentUserUid!!)
            }else{
                //It add my follower when I don't follow a third person
                followDTO!!.followerCount = followDTO!!.followerCount + 1
                followDTO!!.followers[currentUserUid!!] = true
                followerAlarm(uid!!)
            }
            transaction.set(tsDocFollower,followDTO!!)
            return@runTransaction
        }
    }

    fun followerAlarm(destinationUid : String){
        var alarmDTO = AlarmDTO()
        alarmDTO.destinationUid = destinationUid
        alarmDTO.userId = auth?.currentUser?.email
        alarmDTO.uid = auth?.currentUser?.uid
        alarmDTO.kind = 2
        alarmDTO.timestamp = System.currentTimeMillis()
        FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO)

        var message = auth?.currentUser?.email + getString(R.string.alarm_follow)
        FcmPush.instance.sendMessage(destinationUid,"Memeria",message)
    }
    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("UseRequireInsteadOfGet")

    fun getProfileImage(){

        uid?.let {
            firestore?.collection("profileImages")?.document(it)?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                if(documentSnapshot == null) return@addSnapshotListener
                    val url = documentSnapshot.data!!["image"]


                    Glide.with(activity?.applicationContext).load(url).into(fragmentView?.account_iv_profile!!)
                }

        }
    }
    inner class UserFragmentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        var contentDTOs : ArrayList<ContentDTO> = arrayListOf()
        init {
            firestore?.collection("images")?.whereEqualTo("uid",uid)?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                //Sometimes, This code return null of querySnapshot when it signout
                if(querySnapshot == null) return@addSnapshotListener

                //Get data
                for(snapshot in querySnapshot.documents){
                    contentDTOs.add(0,snapshot.toObject(ContentDTO::class.java)!!)
                }


                fragmentView?.account_tv_post_count?.text = contentDTOs.size.toString()
                notifyDataSetChanged()
            }
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
            var width = resources.displayMetrics.widthPixels / 3

            var imageview = ImageView(p0.context)
            imageview.layoutParams = LinearLayoutCompat.LayoutParams(width,width)
            return CustomViewHolder(imageview)
        }

        inner class CustomViewHolders(view: View) : RecyclerView.ViewHolder(view)

        inner class CustomViewHolder(var imageview: ImageView) : RecyclerView.ViewHolder(imageview) {

        }


        override fun getItemCount(): Int {
            return contentDTOs.size
        }


        override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {

            var viewholder = (p0 as CustomViewHolder).itemView

            val str_0 = contentDTOs[p1].userId
            viewholder.name?.text  = str_0


            var imageview = (p0 as CustomViewHolder).imageview
            Glide.with(p0.itemView.context).load(contentDTOs[p1].imageUrl).apply(RequestOptions().centerCrop()).into(imageview)
        }


    }
}
