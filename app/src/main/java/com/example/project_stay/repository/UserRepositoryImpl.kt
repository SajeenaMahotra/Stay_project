package com.example.project_stay.repository

import com.example.project_stay.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserRepositoryImpl: UserRepository {
    var auth :FirebaseAuth = FirebaseAuth.getInstance()

    var database : FirebaseDatabase = FirebaseDatabase.getInstance()
    var reference= database.reference.child("users")

    override fun login(email: String, password: String, callback: (Boolean, String) -> Unit) {
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
            if(it.isSuccessful){
                callback(true,"Login success")
            }else{
                callback(false,it.exception?.message.toString())
            }
        }

    }

    override fun signup(
        email: String,
        password: String,
        callback: (Boolean, String, String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
            if (it.isSuccessful){
                callback(true,"Registration success",
                    auth.currentUser?.uid.toString())
            }else{
                callback(false,it.exception?.message.toString(),"")
            }
        }
    }

    override fun forgetPassword(email: String, callback: (Boolean, String) -> Unit) {
    }

    override fun addUserToDatabase(
        userId: String,
        userModel: UserModel,
        callback: (Boolean, String) -> Unit
    ) {
        reference.child(userId.toString()).setValue(userModel).addOnCompleteListener {
            if(it.isSuccessful){
                callback(true,"Registration Successful")
            }else{
                callback(false,it.exception?.message.toString())
            }
        }
    }

    override fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    override fun getUserFromDatabase(
        userId: String,
        callback: (UserModel?, Boolean, String) -> Unit
    ) {
        reference.child(userId).addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var userModel=snapshot.getValue(UserModel::class.java)
                    callback(userModel,true,"Fetched")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null,false,error.message)
            }

        })

    }
}