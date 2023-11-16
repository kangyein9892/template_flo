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
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

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
            val intent = Intent(this, SongActivity::class.java)
            intent.putExtra("title", song.title)
            intent.putExtra("singer", song.singer)
            intent.putExtra("second", song.second)
            intent.putExtra("playTime", song.playTime)
            intent.putExtra("isPlaying", song.isPlaying)
            intent.putExtra("music", song.music)

            getResultText.launch(intent)
            // startActivity(intent)
        }

        binding.mainMiniplayerBtn.setOnClickListener {
            setPlayerStatus(true)
        }

        binding.mainPauseBtn.setOnClickListener {
            setPlayerStatus(false)
        }

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

        private fun setMiniPlayer(song: Song){
            binding.mainPlayerTitle.text = song.title
            binding.mainPlayerSinger.text = song.singer
            binding.mainMiniplayerProgressSb.progress = (song.second * 100000)/song.playTime
        }

        override fun onStart() {
            super.onStart()
            val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
            val songJson = sharedPreferences.getString("songData", null)

            song = if(songJson == null){
                Song("라일락", "아이유(IU)", 0, 60, false, "music_lilac")
            } else {
                gson.fromJson(songJson, Song::class.java)
            }

            setMiniPlayer(song)
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.interrupt()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onPause() {
        super.onPause()
        setPlayerStatus(false)
        song.second = ((binding.mainMiniplayerProgressSb.progress * song.playTime)/100)/1000
        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val songJson = gson.toJson(song)
        editor.putString("songData", songJson)
        editor.apply()

        // editor.putString("title", song.title),,,,
        // editor.putString("singer", song.singer),,,,

    }

    fun updateMainPlayerCl(album: Album) {
        binding.mainPlayerTitle.text = album.title
        binding.mainPlayerSinger.text = album.singer
        binding.mainMiniplayerProgressSb.progress = (album.songs!!.second * 100000)/album.songs!!.playTime
        binding.mainMiniplayerBtn.visibility = View.GONE
        binding.mainPauseBtn.visibility = View.VISIBLE
        setPlayer(album.songs!!)
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



}