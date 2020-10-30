package com.example.videoapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.videoapp.Model.AlarmDTO
import com.example.videoapp.Model.ContentDTO
import com.example.videoapp.Model.FcmPush
import com.example.videoapp.Model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_detail.view.*
import kotlinx.android.synthetic.main.item_detail.view.*


class DetailViewFragment(
) : Fragment(
){
    var firestore : FirebaseFirestore? = null
    var uid : String? = null
    lateinit var  mUser: List<Users>

    lateinit var mAuth: FirebaseAuth
    lateinit var comment_btn_send: FirebaseAuth
    var auth : FirebaseAuth? = null

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var fragmentview = LayoutInflater.from(activity).inflate(R.layout.fragment_detail,container,false)
        firestore = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid

        val currentUserUid = auth?.currentUser?.uid
        var comment = ContentDTO.Comment()
        var contentDTOs : ArrayList<ContentDTO> = arrayListOf()

        auth = FirebaseAuth.getInstance()



        var users = FirebaseAuth.getInstance().currentUser

        fragmentview.videofragment?.setOnClickListener {
            var fragment = BlankFragment()
            activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.frag,fragment)?.commit()

        }
        fragmentview.memefragment?.setOnClickListener {
            var fragment = DetailViewFragment()
            activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.frag,fragment)?.commit()

        }

            fragmentview.swiperefresh?.setOnRefreshListener {
                fragmentview.detailviewfragment_recyclerview.adapter = this.DetailViewRecyclerViewAdapter()
                (fragmentview.detailviewfragment_recyclerview.adapter as DetailViewRecyclerViewAdapter).notifyDataSetChanged()
                fragmentview.detailviewfragment_recyclerview.layoutManager = LinearLayoutManager(activity)
                (fragmentview.detailviewfragment_recyclerview.adapter as DetailViewRecyclerViewAdapter).notifyDataSetChanged()

                fragmentview.swiperefresh?.isRefreshing = false   // reset the SwipeRefreshLayout (stop the loading spinner)
        }



        fragmentview.detailviewfragment_recyclerview.adapter = this.DetailViewRecyclerViewAdapter()
        fragmentview.detailviewfragment_recyclerview.layoutManager = LinearLayoutManager(activity)
        return fragmentview

    }



    inner class DetailViewRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        var contentDTOs : ArrayList<ContentDTO> = arrayListOf()
        var contentUidList : ArrayList<String> = arrayListOf()


        init {


            firestore?.collection("images")?.orderBy("timestamp")?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                contentDTOs.clear()
                contentUidList.clear()
                //Sometimes, This code return null of querySnapshot when it signout
                if(querySnapshot == null) return@addSnapshotListener

                for(snapshot in querySnapshot.documents){
                    var item = snapshot.toObject(ContentDTO::class.java)
                    item?.let { contentDTOs.add(0, it) }
                    contentUidList.add(0,snapshot.id)
                }
                notifyDataSetChanged()
            }

        }


        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(p0.context).inflate(R.layout.item_detail,p0,false)
            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view){
            var alarmDTOList : ArrayList<AlarmDTO> = arrayListOf()

        }

        override fun getItemCount(): Int {
            return contentDTOs.size
        }



        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
            var viewholder = (p0 as CustomViewHolder).itemView

            val str_0 = contentDTOs[p1].userId
            viewholder.idname?.text  = str_0


            FirebaseFirestore.getInstance().collection("profileImages").document(contentDTOs[p1].uid!!).get().addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val url = task.result!!["image"]
                    if (activity==null){
                        return@addOnCompleteListener
                    }
                    else{
                    Glide.with(viewholder.context).load(url).apply(RequestOptions().circleCrop()).into(
                        viewholder.detailviewitem_profile_image as ImageView?
                    )}
                }
            }

            val uid = FirebaseAuth.getInstance().currentUser?.uid

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

                viewholder.detailviewitem_comment_imageview.setOnClickListener {
                var intent = Intent(context,CommentActivity::class.java)
                intent.putExtra("contentUid",contentUidList[p1])
                startActivity(intent)
            }

            viewholder.detailviewitem_imageview_content.setOnLongClickListener {
                val firebase_url = contentDTOs[p1].imageUrl
                val storageRef =
                    firebase_url?.let { it1 ->
                        FirebaseFirestore.getInstance().collection("images").document(
                            it1
                        )
                    }




                storageRef?.delete()?.addOnSuccessListener {
                    // File deleted successfully
                    Toast.makeText(context , "Deleted",Toast.LENGTH_SHORT).show()

                }?.addOnFailureListener {
                    // Uh-oh, an error occurred!
                    Toast.makeText(context , "cannot delete",Toast.LENGTH_SHORT).show()
                }

                return@setOnLongClickListener true

            }
            //Image
            Glide.with(p0.itemView.context).load(contentDTOs[p1].imageUrl)
                .into(viewholder.detailviewitem_imageview_content)
            var user = FirebaseAuth.getInstance().currentUser
            val uids = arguments?.getString("destinationUid")

            firestore = FirebaseFirestore.getInstance()
            val auth = FirebaseAuth.getInstance()
           val currentUserUid = auth.currentUser?.email



            //Explain of content
            viewholder.detailviewitem_explain_textview.text = contentDTOs[p1].explain

            //likes
            viewholder.detailviewitem_favoritecounter_textview.text = contentDTOs[p1].favoriteCount.toString()
            viewholder.detailviewitem_favorite_imageview.setOnClickListener {
                favoriteEvent(p1)
            }


            //This code is when the page is loaded
            if(contentDTOs[p1].favorites.containsKey(uid)){
                //This is like status
                viewholder.detailviewitem_favorite_imageview.setImageResource(R.drawable.smile4)

            }else{
                //This is unlike status
                viewholder.detailviewitem_favorite_imageview.setImageResource(R.drawable.meme3)
            }

            val users = FirebaseAuth.getInstance().currentUser?.email
            if(str_0 == currentUserUid){
                //MyPage
                viewholder.example?.visibility = View.VISIBLE
            }

            if (str_0!=currentUserUid){
                //OtherUserPage
                viewholder.example?.visibility = View.GONE
            }

            //This code is when the profile image is clicked
            viewholder.detailviewitem_profile_image.setOnClickListener {
                var fragment = ProfileFragment()
                var bundle = Bundle()
                bundle.putString("destinationUid",contentDTOs[p1].uid)
                bundle.putString("userId",contentDTOs[p1].userId)
                fragment.arguments = bundle
                activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.frag,fragment)?.commit()
            }
            viewholder.detailviewitem_comment_imageview.setOnClickListener { v ->
                var intent = Intent(v.context,CommentActivity::class.java)
                intent.putExtra("contentUid",contentUidList[p1])
                intent.putExtra("destinationUid",contentDTOs[p1].uid)
                startActivity(intent)
            }

        }
        fun favoriteEvent(position : Int){
            var tsDoc = firestore?.collection("images")?.document(contentUidList[position])
            firestore?.runTransaction { transaction ->


                var contentDTO = transaction.get(tsDoc!!).toObject(ContentDTO::class.java)

                if(contentDTO!!.favorites.containsKey(uid)){
                    //When the button is clicked
                    contentDTO.favoriteCount = contentDTO.favoriteCount - 1
                    contentDTO.favorites.remove(uid)
                }else{
                    //When the button is not clicked
                    contentDTO.favoriteCount = contentDTO.favoriteCount + 1
                    contentDTO.favorites[uid!!] = true
                    favoriteAlarm(contentDTOs[position].uid!!)

                }
                transaction.set(tsDoc,contentDTO)
            }
        }

        fun favoriteAlarm(destinationUid : String){
            var alarmDTO = AlarmDTO()
            alarmDTO.destinationUid = destinationUid
            alarmDTO.userId = FirebaseAuth.getInstance().currentUser?.email
            alarmDTO.uid = FirebaseAuth.getInstance().currentUser?.uid
            alarmDTO.kind = 0
            alarmDTO.timestamp = System.currentTimeMillis()
            FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO)

            var message = FirebaseAuth.getInstance().currentUser?.email + " " + getString(R.string.alarm_favorite)
            FcmPush.instance.sendMessage(destinationUid,"Memeria",message)

        }




    }

    }


            //This code is when the button is clicked

