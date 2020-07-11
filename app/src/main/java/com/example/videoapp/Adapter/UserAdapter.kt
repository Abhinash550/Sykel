package com.example.videoapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.videoapp.Model.Users
import com.example.videoapp.ProfileFragment
import com.example.videoapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(private val mContext: Context,
                  mUser: List<Users>,
                  isFragment: Boolean

):RecyclerView.Adapter<UserAdapter.ViewHolder?>(){
    private val mUsers :List<Users> = mUser
    private var isFragment :Boolean = false
    private var firebaseUser:FirebaseUser? = FirebaseAuth.getInstance().currentUser

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {
        val view =LayoutInflater.from(this.mContext).inflate(R.layout.user_item_layout,parent,false)
        return UserAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mUsers.size
    }

    override fun onBindViewHolder(holder: UserAdapter.ViewHolder, i: Int) {
        val user = mUsers[i]
        holder.userNameTextView.text = user.username
        holder.userFullNameTextView.text = user.Fullname
        Picasso.get().load(user.image).placeholder(R.drawable.profile).into(holder.userProfileImage)

        checkfollowingstatus(user.id,holder.followbtn)
        holder.itemView.setOnClickListener {
            val pref = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()
            pref.putString("profileId",user.id)
            pref.apply()
            (mContext as FragmentActivity).supportFragmentManager.beginTransaction().replace(R.id.SecondFragment,
                ProfileFragment()
            )
                .commit()
        }

        holder.followbtn.setOnClickListener {
            if (holder.followbtn.text.toString() == "Follow"){
                firebaseUser?.uid.let { it1 ->
                    FirebaseDatabase.getInstance().reference.child("Follow").child(it1.toString()).child("Following")
                        .child(user.id).setValue(true).addOnCompleteListener { task ->
                            if (task.isSuccessful){
                                firebaseUser?.uid.let { it1 ->
                                    FirebaseDatabase.getInstance().reference.child("Follow")
                                        .child(user.id) .child("Followers")
                                        .child(it1.toString()).setValue(true).addOnCompleteListener { task ->
                                            if (task.isSuccessful){

                                            }
                                        }
                                }

                            }
                        }
                }
            }
            else{
                firebaseUser?.uid.let { it1 ->
                    FirebaseDatabase.getInstance().reference.child("Follow").child(it1.toString()).child("Following")
                        .child(user.id).removeValue().addOnCompleteListener { task ->
                            if (task.isSuccessful){
                                firebaseUser?.uid.let { it1 ->
                                    FirebaseDatabase.getInstance().reference.child("Follow")
                                        .child(user.id) .child("Followers")
                                        .child(it1.toString()).removeValue().addOnCompleteListener { task ->
                                            if (task.isSuccessful){

                                            }
                                        }
                                }

                            }
                        }
                }


            }
        }
    }


    class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView){
        var userNameTextView:TextView = itemView.findViewById(R.id.user_name_search)
        var userFullNameTextView:TextView = itemView.findViewById(R.id.unique_name_search)
        var userProfileImage:CircleImageView = itemView.findViewById(R.id.user_profile_image_search)
        var followbtn:Button = itemView.findViewById(R.id.follow_btn_search)

    }
    private fun checkfollowingstatus(id: String, followbtn: Button) {

        val followingRef= firebaseUser?.uid.let { it1 ->
            FirebaseDatabase.getInstance().reference.child("Follow").child(it1.toString()).child("Following")
        }

        followingRef.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child(id).exists()){

                    followbtn.text = "Following"
                }
                else{
                    followbtn.text = "Follow"
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }

        })


    }}