package com.example.videoapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.videoapp.Model.AlarmDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_notification.view.*
import kotlinx.android.synthetic.main.fragment_notification.view.*
import kotlinx.android.synthetic.main.item_comment.view.commentviewitem_imageview_profile
import kotlinx.android.synthetic.main.item_comment.view.commentviewitem_textview_comment
import kotlinx.android.synthetic.main.item_comment.view.commentviewitem_textview_profile

/**
 * A simple [Fragment] subclass.
 */
class NotificationFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_notification,container,false)
        view.alarmfragment_recyclerview.adapter = this.AlarmRecyclerviewAdapter()
        view.alarmfragment_recyclerview.layoutManager = LinearLayoutManager(activity)

        return view
    }
    inner class AlarmRecyclerviewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        var alarmDTOList : ArrayList<AlarmDTO> = arrayListOf()

        init {
            val uid = FirebaseAuth.getInstance().currentUser?.uid

            FirebaseFirestore.getInstance().collection("alarms").whereEqualTo("destinationUid",uid).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                alarmDTOList.clear()
                if(querySnapshot == null) return@addSnapshotListener

                for(snapshot in querySnapshot.documents){
                    alarmDTOList.add(0,snapshot.toObject(AlarmDTO::class.java)!!)

                }
                notifyDataSetChanged()
            }
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(p0.context).inflate(R.layout.activity_notification,p0,false)

            return CustomViewHolder(view)
        }
        inner class CustomViewHolder(view : View) : RecyclerView.ViewHolder(view)
        override fun getItemCount(): Int {
            return alarmDTOList.size
        }

        override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
            var view = p0.itemView

            view.notification_layout.setOnLongClickListener {

                val storageRef =
                        FirebaseFirestore.getInstance().collection("alarms").document("alarms")

                storageRef.delete().addOnSuccessListener {
                    // File deleted successfully
                    Toast.makeText(context , "Deleted",Toast.LENGTH_SHORT).show()

                }.addOnFailureListener {
                    // Uh-oh, an error occurred!
                    Toast.makeText(context , "cannot delete",Toast.LENGTH_SHORT).show()
                }


//                val popups = PopupMenu(context, view).apply {
//                    inflate(R.menu.delete)
//
//                    show()
//
//
//                }
//                popups.setOnMenuItemClickListener {
//                    val newPosition: Int = p0.adapterPosition
//
//                    FirebaseFirestore.getInstance().collection("alarms").document(newPosition.toString()).delete().addOnCompleteListener{ task ->
//                        if (task.isSuccessful){
//                          Toast.makeText(context,"Message deleted",Toast.LENGTH_SHORT).show()
//                            alarmDTOList.removeAt(newPosition)
//                   notifyItemRemoved(newPosition)
////
//                    notifyItemRangeChanged(newPosition, alarmDTOList.size)
//                    notifyItemRemoved(newPosition)
//                        }
//                        else{
//                            Toast.makeText(context,"Message not deleted",Toast.LENGTH_SHORT).show()
//                        }
//                    }
//
////
//                    return@setOnMenuItemClickListener true
//                }

                return@setOnLongClickListener true
            }


            view.commentviewitem_imageview_profile.setOnClickListener {
                var fragment = ProfileFragment()
                var bundle = Bundle()
                bundle.putString("destinationUid",alarmDTOList[p1].uid)
                bundle.putString("userId",alarmDTOList[p1].userId)
                fragment.arguments = bundle
                activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.frag,fragment)?.commit()
            }

            FirebaseFirestore.getInstance().collection("profileImages").document(alarmDTOList[p1].uid!!).get().addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val url = task.result!!["image"]
                    Glide.with(view.context).load(url).apply(RequestOptions().circleCrop()).into(view.commentviewitem_imageview_profile)
                }
            }


            when(alarmDTOList[p1].kind){
                0 -> {
                    val str_0 = alarmDTOList[p1].userId + " "+ getString(R.string.alarm_favorite)
                    view.commentviewitem_textview_profile.text = str_0
                }
                1 -> {
                    val str_0 = alarmDTOList[p1].userId + " " + getString(R.string.alarm_comment) +" of " + alarmDTOList[p1].message
                    view.commentviewitem_textview_profile.text = str_0
                }
                2 -> {
                    val str_0 = alarmDTOList[p1].userId + " " + getString(R.string.alarm_follow)
                    view.commentviewitem_textview_profile.text = str_0
                }
            }

            view.commentviewitem_textview_comment.visibility = View.INVISIBLE

        }
//        fun remove(){
//            val position = 0
//            alarmDTOList.removeAt(position)
//            notifyItemRemoved(position)
//            notifyItemRangeChanged(position, alarmDTOList.size)
//
//        }

    }

}
