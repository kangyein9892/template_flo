package com.example.template_flo

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query

class FirebaseSongDao {
    private var databaseReference: DatabaseReference? = null

    init {
        val db = FirebaseDatabase.getInstance()
        databaseReference = db.getReference("Song")
    }

    // 등록 함수
    /*fun add(firebasesong: FirebaseSong?): Task<Void> {
        return databaseReference!!.push().setValue(firebasesong)
    }*/
    fun add(firebaseSong: FirebaseSong?): Task<Void> {
        return databaseReference!!.push().setValue(firebaseSong)
    }

    // 조회 함수
    fun getSongList(): Query?{
        return databaseReference
    }
    fun getSongDBReference(): DatabaseReference? {
        return databaseReference
    }

    // 수정 함수
    fun songUpdate(key: String, hashMap: HashMap<String, Any>): Task<Void> {
        return databaseReference!!.child(key)!!.updateChildren(hashMap)
    }

}