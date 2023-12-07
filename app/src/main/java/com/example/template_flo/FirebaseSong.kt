package com.example.template_flo

import androidx.room.PrimaryKey

data class FirebaseSong(
    var title: String,
    var singer: String,
    var second: Int,
    var playTime: Int,
    // var `isPlaying`: Boolean,
    var playing: Boolean,
    var music: String,
    var coverImg: Int?,
    //var `isLike`: Boolean,
    var like: Boolean,
    var albumIdx : Int,
    var songKey: String
){
    constructor(): this("", "", 0, 0, false, "",null,false, 0, "")
}
