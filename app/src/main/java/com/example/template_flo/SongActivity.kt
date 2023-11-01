package com.example.template_flo

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.template_flo.databinding.ActivitySongBinding

class SongActivity : AppCompatActivity() {

    lateinit var binding: ActivitySongBinding
    lateinit var song: Song
    lateinit var timer: Timer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initSong()
        setPlayer(song)

        binding.songDownIb.setOnClickListener{
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
        }
    }

    /*override fun onDestory() {
        super.onDestroy()
        timer.interrupt()
    }*/

    fun setPlayerStatus(isPlaying: Boolean){

        song.isPlaying = isPlaying
        timer.isPlaying = isPlaying

        if(isPlaying){
            binding.songMiniplayer.visibility = View.GONE
            binding.songPause.visibility = View.VISIBLE
        } else{
            binding.songMiniplayer.visibility = View.VISIBLE
            binding.songPause.visibility = View.GONE
        }
    }

    private fun initSong(){
        if(intent.hasExtra("title") && intent.hasExtra("singer")){
            song = Song(
                intent.getStringExtra("title")!!,
                intent.getStringExtra("singer")!!,
                intent.getIntExtra("second", 0),
                intent.getIntExtra("playTime", 0),
                intent.getBooleanExtra("isPlaying", false)
            )
        }
        startTimer()
    }

    private fun setPlayer(song: Song) {
        binding.songTitleTv.text = intent.getStringExtra("title")!!
        binding.songSingerTv.text = intent.getStringExtra("singer")!!
        binding.songStartTime.text = String.format("%02d:%02d", song.second / 60, song.second % 60)
        binding.songEndTime.text = String.format("%02d:%02d", song.playTime / 60, song.playTime % 60)
        binding.songProgressSb.progress = (song.second * 1000 / song.playTime)

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