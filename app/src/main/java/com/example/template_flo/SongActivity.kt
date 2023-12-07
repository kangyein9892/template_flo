package com.example.template_flo

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.template_flo.databinding.ActivitySongBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson

class SongActivity : AppCompatActivity() {

    lateinit var binding: ActivitySongBinding
    // lateinit var song: Song
    lateinit var timer: Timer
    private var mediaPlayer: MediaPlayer? = null
    private var gson: Gson = Gson()

    val songs = arrayListOf<Song>()
    lateinit var songDB: SongDatabase
    var nowPos = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initPlayList()
        initSong()
        initClickListener()
        // setPlayer(song)

        /*binding.songDownIb.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java).apply{
                // putExtra(MainActivity.STRING_INTENT_KEY, "Returning to Main Activity")
                putExtra("title", binding.songTitleTv.text.toString())
            }
            setResult(Activity.RESULT_OK, intent)
            if(!isFinishing) finish()
        }

        binding.songMiniplayer.setOnClickListener {
            setPlayerStatus(true)
        }

        binding.songPause.setOnClickListener {
            setPlayerStatus(false)
        }
        if(intent.hasExtra("title") && intent.hasExtra("singer")){
            binding.songTitleTv.text = intent.getStringExtra("title")
            binding.songSingerTv.text = intent.getStringExtra("singer")
        }*/
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.interrupt()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun initPlayList(){
        songDB = SongDatabase.getInstance(this)!!
        songs.addAll(songDB.songDao().getSongs())
    }

    private fun initClickListener(){
        binding.songDownIb.setOnClickListener {
            finish()
        }

        binding.songMiniplayer.setOnClickListener {
            setPlayerStatus(true)
        }

        binding.songPause.setOnClickListener {
            setPlayerStatus(false)
        }
        binding.songNext.setOnClickListener {
            moveSong(+1)
        }

        binding.songPrevious.setOnClickListener {
            moveSong(-1)
        }

        binding.songLikeIv.setOnClickListener {
            setLike(songs[nowPos].isLike)
        }
    }

    override fun onPause() {
        super.onPause()
        setPlayerStatus(false)
        songs[nowPos].second = ((binding.songProgressSb.progress * songs[nowPos].playTime)/100)/1000
        songs[nowPos].isPlaying = false
        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        // val songJson = gson.toJson(songs[nowPos])
        // editor.putString("songData", songJson)
        editor.putInt("songId", songs[nowPos].id)
        editor.apply()

        // editor.putString("title", song.title),,,,
        // editor.putString("singer", song.singer),,,,

    }

    fun setPlayerStatus(isPlaying: Boolean){

        songs[nowPos].isPlaying = isPlaying
        timer.isPlaying = isPlaying

        if(isPlaying){
            binding.songMiniplayer.visibility = View.GONE
            binding.songPause.visibility = View.VISIBLE
            mediaPlayer?.start()
        } else{
            binding.songMiniplayer.visibility = View.VISIBLE
            binding.songPause.visibility = View.GONE
            if(mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
            }
        }
    }


    private fun initSong(){
        /*if(intent.hasExtra("title") && intent.hasExtra("singer")){
            song = Song(
                intent.getStringExtra("title")!!,
                intent.getStringExtra("singer")!!,
                intent.getIntExtra("second", 0),
                intent.getIntExtra("playTime", 0),
                intent.getBooleanExtra("isPlaying", false),
                intent.getStringExtra("music")!!
            )
        }*/

        val spf = getSharedPreferences("song", MODE_PRIVATE)
        val songId = spf.getInt("songId", 0)

        nowPos = getPlayingSongPosition(songId)

        Log.d("now Song ID", songs[nowPos].id.toString())
        startTimer()
        setPlayer(songs[nowPos])
    }

    private fun setLike(isLike: Boolean){

        songs[nowPos].isLike = !isLike
        songDB.songDao().updateIsLikeById(!isLike,songs[nowPos].id)

        var firebaseSongDao = FirebaseSongDao()
        val firebasesongs = firebaseSongDao.getSongDBReference()
        val query: Query = firebasesongs!!.orderByChild("title").equalTo(songs[nowPos].title)!!

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    // 조건을 만족하는 데이터 찾음
                    val key = snapshot.key

                    val hasMap: HashMap<String, Any> = HashMap()
                    hasMap["like"] = songs[nowPos].isLike
                    //
                    // hasMap["isLike"] = songs[nowPos].isLike
                    firebaseSongDao.songUpdate(key!!, hasMap)

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // 에러 처리
                Toast.makeText(this@SongActivity, "파이어베이스 데이터 삽입을 실패했습니다. ${databaseError}", Toast.LENGTH_SHORT).show()
            }
        })

            if (!isLike){
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_on)
            CustomSnackbar.make(binding.root, "좋아요 한 곡에 담겼습니다").show()
        } else{
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_off)
            CustomSnackbar.make(binding.root, "좋아요 한 곡이 취소되었습니다.").show()
        }

    }

    private fun moveSong(direct: Int){
        if(nowPos + direct < 0){
            //Toast.makeText(this, "first song", Toast.LENGTH_SHORT).show()
            CustomSnackbar.make(binding.root, "처음 곡입니다.").show()
            return
        }
        if(nowPos + direct >= songs.size){
            // Toast.makeText(this, "last song", Toast.LENGTH_SHORT).show()
            CustomSnackbar.make(binding.root, "마지막 곡입니다.").show()
            return
        }

        nowPos += direct
        timer.interrupt()
        startTimer()

        mediaPlayer?.release()
        mediaPlayer = null

        setPlayer(songs[nowPos])
    }
    private fun getPlayingSongPosition(songId: Int): Int{
        for (i in 0 until songs.size){
            if (songs[i].id == songId){
                return i
            }
        }
        return 0
    }

    private fun setPlayer(song: Song) {
        // binding.songTitleTv.text = intent.getStringExtra("title")!!
        // binding.songSingerTv.text = intent.getStringExtra("singer")!!
        binding.songTitleTv.text = song.title
        binding.songSingerTv.text = song.singer
        binding.songAlbumIv.setImageResource(song.coverImg!!)
        binding.songStartTime.text = String.format("%02d:%02d", song.second / 60, song.second % 60)
        binding.songEndTime.text = String.format("%02d:%02d", song.playTime / 60, song.playTime % 60)
        binding.songProgressSb.progress = (song.second * 1000 / song.playTime)
        val music = resources.getIdentifier(song.music, "raw", this.packageName)
        mediaPlayer = MediaPlayer.create(this, music)

        if (song.isLike){
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_on)
        } else{
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_off)
        }

        setPlayerStatus(song.isPlaying)
    }

    private fun startTimer(){
        timer = Timer(songs[nowPos].playTime, songs[nowPos].isPlaying)
        timer.start()
    }

    inner class Timer(private val playTime: Int, var isPlaying: Boolean = true): Thread(){

        private var second: Int = 0
        private var mills: Float = 0f

        override fun run() {
            super.run()
            try {
                while(true){
                    if(second >= playTime){
                        break
                    }

                    if (isPlaying){
                        sleep(50)
                        mills +=50
                        runOnUiThread {
                            binding.songProgressSb.progress = ((mills / playTime)*100).toInt()
                        }
                        if(mills % 1000 == 0f){
                            runOnUiThread {
                                binding.songStartTime.text = String.format("%02d:%02d", second / 60, second % 60)
                            }
                            second++
                        }
                    }
                }
            } catch (e: InterruptedException){
                Log.d("Song", "쓰레드가 죽었습니다. ${e.message}")
            }
        }
    }
}