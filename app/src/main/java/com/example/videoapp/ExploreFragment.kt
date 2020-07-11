package com.example.videoapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.videoapp.Adapter.UserAdapter
import com.example.videoapp.Model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class ExploreFragment : Fragment() {
    private var recyclerView: RecyclerView?=null
    private var userAdapter:UserAdapter?=null
    private var mUser: List<Users>?=null
    private var searchEditText: EditText?=null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_explore, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(context)
        searchEditText = view.findViewById(R.id.search_edit_text)

        mUser = ArrayList()
        retrieveAllUserd()
        searchEditText!!.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(cs: CharSequence?, start: Int, before: Int, count: Int) {
                searchForUser(cs.toString().toLowerCase())
            }
        })

        return view
    }


    private fun retrieveAllUserd() {
        var firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val refUser =  FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUserID)
        refUser.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                (mUser as ArrayList<Users>).clear()
                if (searchEditText!!.text.toString() == ""){
                    for (snapshot in p0.children){
                        val user = snapshot.getValue(Users::class.java)
                        if (!(user!!.id).equals(firebaseUserID)){
                            (mUser as ArrayList<Users>).add(user)

                        }
                    }

                }
                this@ExploreFragment.userAdapter = context?.let {
                    UserAdapter(
                        mContext = it,
                        mUser = mUser!!,
                        isFragment = false
                    )
                }
                recyclerView?.adapter = userAdapter
            }

            override fun onCancelled(p0: DatabaseError) {
            }

        })

    }
    private fun searchForUser(str: String){
        val firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val queryUserd = FirebaseDatabase.getInstance().reference.child("Users").orderByChild("name").startAt(str)
            .endAt(str+"\uf8ff")
        queryUserd.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                (mUser as ArrayList<Users>).clear()
                for (snapshot in p0.children){
                    val user = snapshot.getValue(Users::class.java)
                    if (!(user!!.id).equals(firebaseUserID)) {
                        (mUser as ArrayList<Users>).add(user)
                    }
                }

                userAdapter = context?.let {
                    UserAdapter(
                        mContext = it,
                        mUser = mUser!!,
                        isFragment = false
                    )
                }
                recyclerView?.adapter = userAdapter


            }
        })
    }


}


