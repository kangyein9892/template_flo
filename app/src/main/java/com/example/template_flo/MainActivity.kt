package com.example.template_flo

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.template_flo.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    val songs = arrayListOf<Song>()
    lateinit var songDB: SongDatabase
    var nowPos = 0

    private var song: Song = Song()
    private var gson: Gson = Gson()
    private var mediaPlayer: MediaPlayer? = null
    lateinit var timer: Timer

    companion object { const val STRING_INTENT_KEY = "my_string_key"}

    private val getResultText = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){ result ->
        if (result.resultCode == Activity.RESULT_OK){
            // val returnString = result.data?.getStringExtra(STRING_INTENT_KEY)
            // val returnTitle = result.data?.getStringExtra("title")
            Toast.makeText(this, result.data?.getStringExtra("title"), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Template_flo)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startTimer()
        // val song = Song(binding.mainPlayerTitle.text.toString(), binding.mainPlayerSinger.text.toString(), 0, 60, false, "music_lilac")

        binding.mainPlayerCl.setOnClickListener {
           /* val intent = Intent(this, SongActivity::class.java)
            intent.putExtra("title", song.title)
            intent.putExtra("singer", song.singer)
            intent.putExtra("second", song.second)
            intent.putExtra("playTime", song.playTime)
            intent.putExtra("isPlaying", song.isPlaying)
            intent.putExtra("music", song.music)

            getResultText.launch(intent)*/


            val editor = getSharedPreferences("song", MODE_PRIVATE).edit()
            editor.putInt("songId", song.id)
            editor.apply()

            val intent = Intent(this, SongActivity::class.java)
            startActivity(intent)
        }

        binding.mainPreviousBtn.setOnClickListener {
            moveSong(-1)
            setMiniPlayer(songs[nowPos])
        }

        binding.mainNextBtn.setOnClickListener {
            moveSong(+1)
            setMiniPlayer(songs[nowPos])
        }

        binding.mainMiniplayerBtn.setOnClickListener {
            setPlayerStatus(true)
        }

        binding.mainPauseBtn.setOnClickListener {
            setPlayerStatus(false)
        }



        inputDummySongs()
        inputDummyAlbums()
        inputDummyFirebaseSongs()
        initPlayList()
        initBottomNavigation()



    }

        private fun initBottomNavigation() {

            supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, HomeFragment())
                .commitAllowingStateLoss()

            binding.mainBnv.setOnItemSelectedListener { item ->
                when(item.itemId){

                    R.id.homeFragment -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.main_frm, HomeFragment())
                            .commitAllowingStateLoss()
                        return@setOnItemSelectedListener true
                    }

                    R.id.lookFragment -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.main_frm, LookFragment())
                            .commitAllowingStateLoss()
                        return@setOnItemSelectedListener true
                    }

                    R.id.searchFragment -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.main_frm, SearchFragment())
                            .commitAllowingStateLoss()
                        return@setOnItemSelectedListener true
                    }

                    R.id.lockerFragment -> {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.main_frm, LockerFragment())
                            .commitAllowingStateLoss()
                        return@setOnItemSelectedListener true
                    }

                }

                false
            }
        }

    private fun moveSong(direct: Int){
        if(nowPos + direct < 0){
            Toast.makeText(this, "first song", Toast.LENGTH_SHORT).show()
            return
        }
        if(nowPos + direct >= songs.size){
            Toast.makeText(this, "last song", Toast.LENGTH_SHORT).show()
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

        private fun setMiniPlayer(song: Song){
           /* binding.mainPlayerTitle.text = song.title
            binding.mainPlayerSinger.text = song.singer
            binding.mainMiniplayerProgressSb.progress = (song.second * 100000)/song.playTime*/
            binding.mainPlayerTitle.text = song.title
            binding.mainPlayerSinger.text = song.singer
            Log.d("songInfo", song.toString())
            val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
            val second = sharedPreferences.getInt("second", 0)
            Log.d("spfSecond", second.toString())
            binding.mainMiniplayerProgressSb.progress = (song.second * 100000 / song.playTime)
        }

        private fun initPlayList(){
            songDB = SongDatabase.getInstance(this)!!
            songs.addAll(songDB.songDao().getSongs())
        }


        /*override fun onStart() {
            super.onStart()
            *//*val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
            val songJson = sharedPreferences.getString("songData", null)

            song = if(songJson == null){
                Song("라일락", "아이유(IU)", 0, 60, false, "music_lilac")
            } else {
                gson.fromJson(songJson, Song::class.java)
            }*//*

            val spf = getSharedPreferences("Song", MODE_PRIVATE)
            val songId = spf.getInt("songId", 0)

            val songDB = SongDatabase.getInstance(this)!!

            song = if(songId == 0){
                songDB.songDao().getSong(1)
            } else {
                songDB.songDao().getSong(songId)
            }

            Log.d("song ID", song.id.toString())

            setMiniPlayer(song)
    }*/

    override fun onResume() {
        super.onResume()

        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val songId = sharedPreferences.getInt("songId", 0)

        nowPos = getPlayingSongPosition(songId)
        setMiniPlayer(songs[nowPos])
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.interrupt()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onPause() {
        /*super.onPause()
        setPlayerStatus(false)
        song.second = ((binding.mainMiniplayerProgressSb.progress * song.playTime)/100)/1000
        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val songJson = gson.toJson(song)
        editor.putString("songData", songJson)
        editor.apply()*/

        super.onPause()
        songs[nowPos].second = (songs[nowPos].playTime * binding.mainMiniplayerProgressSb.progress)
        Log.d("second", songs[nowPos].second.toString())
        songs[nowPos].isPlaying = false
        setPlayerStatus(false)

        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("songId", songs[nowPos].id)
        editor.putInt("second", songs[nowPos].second)
        editor.apply()

        // editor.putString("title", song.title),,,,
        // editor.putString("singer", song.singer),,,,

    }



    fun updateMainPlayerCl(album: Album) {
        /*binding.mainPlayerTitle.text = album.title
        binding.mainPlayerSinger.text = album.singer
        binding.mainMiniplayerProgressSb.progress = (album.songs!!.second * 100000)/album.songs!!.playTime
        binding.mainMiniplayerBtn.visibility = View.GONE
        binding.mainPauseBtn.visibility = View.VISIBLE
        setPlayer(album.songs!!)*/
        val songDB = SongDatabase.getInstance(this)!!
        val song = songDB.songDao().getAlbumSong(album.id)
        binding.mainPlayerTitle.text = song.title
        binding.mainPlayerSinger.text = song.singer
        // binding.mainMiniplayerProgressSb.progress = (song!!.second * 100000)/ song!!.playTime
        binding.mainMiniplayerBtn.visibility = View.GONE
        binding.mainPauseBtn.visibility = View.VISIBLE
        setPlayer(song!!)
        setPlayerStatus(true)
    }

    fun setPlayerStatus(isPlaying: Boolean){

        song.isPlaying = isPlaying
        timer.isPlaying = isPlaying

        if(isPlaying){
            binding.mainMiniplayerBtn.visibility = View.GONE
            binding.mainPauseBtn.visibility = View.VISIBLE
            mediaPlayer?.start()
        } else{
            binding.mainMiniplayerBtn.visibility = View.VISIBLE
            binding.mainPauseBtn.visibility = View.GONE
            if(mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
            }
        }
    }

    private fun setPlayer(song: Song) {
        binding.mainMiniplayerProgressSb.progress = (song.second * 1000 / song.playTime)
        val music = resources.getIdentifier(song.music, "raw", this.packageName)
        mediaPlayer = MediaPlayer.create(this, music)
        setPlayerStatus(song.isPlaying)
    }

    private fun startTimer(){
        timer = Timer(song.playTime, song.isPlaying)
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
                            binding.mainMiniplayerProgressSb.progress = ((mills / playTime)*100).toInt()
                        }
                        if(mills % 1000 == 0f){
                            second++
                        }
                    }
                }
            } catch (e: InterruptedException){
                Log.d("Song", "쓰레드가 죽었습니다. ${e.message}")
            }
        }
    }

    private fun inputDummySongs(){
        val songDB = SongDatabase.getInstance(this)!!
        val songs = songDB.songDao().getSongs()

        if (songs.isNotEmpty()) return

        songDB.songDao().insert(
            Song(
                "Lilac",
                "아이유 (IU)",
                0,
                200,
                false,
                "music_lilac",
                R.drawable.img_album_exp2,
                false,
                1
            )
        )

        songDB.songDao().insert(
            Song(
                "Flu",
                "아이유 (IU)",
                0,
                200,
                false,
                "music_flu",
                R.drawable.img_album_exp2,
                false,
                1
            )
        )

        songDB.songDao().insert(
            Song(
                "Butter",
                "방탄소년단 (BTS)",
                0,
                190,
                false,
                "music_butter",
                R.drawable.img_album_exp,
                false,
                2
            )
        )

        songDB.songDao().insert(
            Song(
                "Next Level",
                "에스파 (AESPA)",
                0,
                210,
                false,
                "music_next",
                R.drawable.img_album_exp3,
                false,
                3
            )
        )


        songDB.songDao().insert(
            Song(
                "Boy with Luv",
                "방탄소년단 (BTS)",
                0,
                230,
                false,
                "music_boy",
                R.drawable.img_album_exp4,
                false,
                4
            )
        )


        songDB.songDao().insert(
            Song(
                "BBoom BBoom",
                "모모랜드 (MOMOLAND)",
                0,
                240,
                false,
                "music_bboom",
                R.drawable.img_album_exp5,
                false,
                5
            )
        )

    }

    private fun inputDummyAlbums() {
        val songDB = SongDatabase.getInstance(this)!!
        val albums = songDB.albumDao().getAlbums()

        if (albums.isNotEmpty()) return

        songDB.albumDao().insert(
            Album(
                1,
                "IU 5th Album 'LILAC'", "아이유 (IU)", R.drawable.img_album_exp2
            )
        )

        songDB.albumDao().insert(
            Album(
                2,
                "Butter", "방탄소년단 (BTS)", R.drawable.img_album_exp
            )
        )

        songDB.albumDao().insert(
            Album(
                3,
                "iScreaM Vol.10 : Next Level Remixes", "에스파 (AESPA)", R.drawable.img_album_exp3
            )
        )

        songDB.albumDao().insert(
            Album(
                4,
                "MAP OF THE SOUL : PERSONA", "방탄소년단 (BTS)", R.drawable.img_album_exp4
            )
        )

        songDB.albumDao().insert(
            Album(
                5,
                "GREAT!", "모모랜드 (MOMOLAND)", R.drawable.img_album_exp5
            )
        )

    }

    // 파이어베이스에 기본 데이터 집어넣기
    private fun inputDummyFirebaseSongs(){

        val firebaseSongDao = FirebaseSongDao()
        val firebasesongs = firebaseSongDao.getSongDBReference()

        // addValueEventListener -> 실시간 업데이트로 수신
        // addListenerForSingleValueEvent -> 한번만 수신
        firebasesongs?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // 데이터베이스에 데이터가 있음
                    // 처리할 작업을 추가하세요
                    // Toast.makeText(this@MainActivity, "이미 파이어베이스에 데이터가 존재합니다.", Toast.LENGTH_SHORT).show()
                    for(dataSnapshot in snapshot.children ){
                        val song = dataSnapshot.getValue(FirebaseSong::class.java)
                        val key = dataSnapshot.key.toString()
                        // song?.songKey = key.toString()

                        val hasMap: HashMap<String, Any> = HashMap()
                        hasMap["songKey"] = key
                        firebaseSongDao.songUpdate(key!!, hasMap)
                    }
                } else {
                    // 데이터베이스가 비어있음
                    // 처리할 작업을 추가하세요
                    firebaseSongDao.add(
                        FirebaseSong(
                            "Lilac",
                            "아이유 (IU)",
                            0,
                            200,
                            false,
                            "music_lilac",
                            R.drawable.img_album_exp2,
                            false,
                            1,
                            "")
                    )

                    firebaseSongDao.add(
                        FirebaseSong(
                            "Flu",
                            "아이유 (IU)",
                            0,
                            200,
                            false,
                            "music_flu",
                            R.drawable.img_album_exp2,
                            false,
                            1,
                            "")
                    )

                    firebaseSongDao.add(
                        FirebaseSong(
                            "Butter",
                            "방탄소년단 (BTS)",
                            0,
                            190,
                            false,
                            "music_butter",
                            R.drawable.img_album_exp,
                            false,
                            2,
                            "")
                    )

                    firebaseSongDao.add(
                        FirebaseSong(
                            "Next Level",
                            "에스파 (AESPA)",
                            0,
                            210,
                            false,
                            "music_next",
                            R.drawable.img_album_exp3,
                            false,
                            3,
                            "")
                    )

                    firebaseSongDao.add(
                        FirebaseSong(
                            "Boy with Luv",
                            "방탄소년단 (BTS)",
                            0,
                            230,
                            false,
                            "music_boy",
                            R.drawable.img_album_exp4,
                            false,
                            4,
                            "")
                    )

                    firebaseSongDao.add(
                        FirebaseSong(
                            "BBoom BBoom",
                            "모모랜드 (MOMOLAND)",
                            0,
                            240,
                            false,
                            "music_bboom",
                            R.drawable.img_album_exp5,
                            false,
                            5,
                            "")
                    )
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@MainActivity, "파이어베이스 데이터 삽입을 실패했습니다. ${databaseError}", Toast.LENGTH_SHORT).show()
            }
        })

        /*firebaseSongdao.add(FirebaseSong(
            "Lilac",
            "아이유 (IU)",
            0,
            200,
            false,
            "music_lilac",
            R.drawable.img_album_exp2,
            false,
            1))?.addOnSuccessListener {
                Toast.makeText(this, "등록 성공", Toast.LENGTH_SHORT).show()
        }?.addOnFailureListener {
            Toast.makeText(this, "등록 실패 : ${it.message}", Toast.LENGTH_SHORT).show()
        }*/


    }


}