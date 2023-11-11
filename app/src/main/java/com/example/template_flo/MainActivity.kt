package com.example.template_flo

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.template_flo.databinding.ActivityMainBinding
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private var song: Song = Song()
    private var gson: Gson = Gson()

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

        val song = Song(binding.mainPlayerTitle.text.toString(), binding.mainPlayerSinger.text.toString(), 0, 60, false, "music_lilac")

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
            binding.mainMiniplayerProgressSb.progress = (song.second*100000)/song.playTime
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

}