package com.example.template_flo

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.template_flo.databinding.FragmentHomeBinding
import com.google.gson.Gson
import java.util.*

class HomeFragment : Fragment(), SendInterface
{

    lateinit var binding: FragmentHomeBinding
    lateinit var songDB: SongDatabase
    private var albumDatas = ArrayList<Album>()

    private val timer = Timer()
    private val handler = Handler(Looper.getMainLooper())

    override fun sendData(album: Album) {
        if(activity is MainActivity){
            val activity = activity as MainActivity
            activity.updateMainPlayerCl(album)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        /*binding.homeAlbumIv01.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction().replace(R.id.main_frm, AlbumFragment()).commitAllowingStateLoss()
        }*/

        /*albumDatas.apply{
            add(Album("Butter", "방탄소년단 (BTS)", R.drawable.img_album_exp, Song("Butter", "방탄소년단(BTS)", 0, 60, false, "music_butter")))
            add(Album("Lilac", "아이유 (IU)", R.drawable.img_album_exp2, Song("라일락", "아이유(IU)", 0, 60, false, "music_lilac")))
            add(Album("Next Level", "에스파 (AESPA)", R.drawable.img_album_exp3, Song("Next Level", "에스파(AESPA)", 0, 60, false, "music_next")))
            add(Album("Boy with Luv", "방탄소년단 (BTS)", R.drawable.img_album_exp4, Song("작은 것들을 위한 시", "방탄소년단(BTS)", 0, 60, false, "music_boy")))
            add(Album("BBoom BBoom", "모모랜드 (MOMOLAND)", R.drawable.img_album_exp5, Song("뿜뿜", "모모랜드(MOMOLAND)", 0, 60, false, "music_bboom")))
            add(Album("Weekend", "태연 (TaeYeon)", R.drawable.img_album_exp6, Song("Weekend", "태연(TaeYeon)", 0, 60, false, "music_flu")))
        }*/

        inputDummyAlbums()

        songDB = SongDatabase.getInstance(requireContext())!!
        albumDatas.addAll(songDB.albumDao().getAlbums())

        val albumRVAdpater = AlbumRVAdpater(albumDatas)
        binding.homeTodayMusicAlbumRv.adapter = albumRVAdpater
        binding.homeTodayMusicAlbumRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        albumRVAdpater.setMyItemClickListener(object: AlbumRVAdpater.MyItemClickListener{
            override fun onItemClick(album: Album) {
                (context as MainActivity).supportFragmentManager.beginTransaction().replace(R.id.main_frm, AlbumFragment().apply {
                    arguments = Bundle().apply {
                        val gson = Gson()
                        val albumJson = gson.toJson(album)
                        putString("album", albumJson)
                    }
                }).commitAllowingStateLoss()
            }
            /*override fun onRemoveAlbum(position: Int) {
                albumRVAdpater.removeItem(position)
            }*/
            override fun onPlayClick(album: Album) {
                sendData(album)
            }
        })

        val bannerAdpater = BannerVpAdapter(this)
        bannerAdpater.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp))
        bannerAdpater.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp2))
        binding.homeBannerVp.adapter = bannerAdpater
        binding.homeBannerVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        autoSlide(bannerAdpater)

        val pannelAdapter = PannelVpAdapter(this)
        pannelAdapter.addFragment(PannelFragment(R.drawable.img_first_album_default))
        pannelAdapter.addFragment(PannelFragment(R.drawable.img_first_album_default))
        binding.homePannelBackgroundVp.adapter = pannelAdapter
        binding.homePannelBackgroundVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        binding.homeBannerIndicator.setViewPager(binding.homeBannerVp)
        binding.homePannelIndicator.setViewPager(binding.homePannelBackgroundVp)


        return binding.root
    }

    private fun autoSlide(adapter: BannerVpAdapter) {
        timer.scheduleAtFixedRate(object: TimerTask() {
            override fun run() {
                handler.post {
                    val nextItem = binding.homeBannerVp.currentItem + 1
                    if(nextItem < adapter.itemCount) {
                        binding.homeBannerVp.currentItem = nextItem
                    } else {
                        binding.homeBannerVp.currentItem = 0
                    }
                }
            }
        }, 3000, 3000)
    }
    private fun inputDummyAlbums(){
        val songDB = SongDatabase.getInstance(requireActivity())!!
        val songs = songDB.albumDao().getAlbums()

        if (songs.isNotEmpty()) return

        songDB.albumDao().insert(
            Album(
                1,
                "IU 5th Album 'LILAC'",
                "아이유 (IU)",
                R.drawable.img_album_exp2
            )
        )

        songDB.albumDao().insert(
            Album(
                2,
                "IU 5th Album 'LILAC'",
                "아이유 (IU)",
                R.drawable.img_album_exp2
            )
        )

        songDB.albumDao().insert(
            Album(
                3,
                "Butter",
                "방탄소년단 (BTS)",
                R.drawable.img_album_exp
            )
        )

        songDB.albumDao().insert(
            Album(
                4,
                "iScreaM Vol.10: Next Level Remixes",
                "에스파 (AESPA)",
                R.drawable.img_album_exp3
            )
        )

        songDB.albumDao().insert(
            Album(
                5,
                "Map of the Soul Persona",
                "방탄소년단 (BTS)",
                R.drawable.img_album_exp4,
            )
        )


        songDB.albumDao().insert(
            Album(
                6,
                "Great!",
                "모모랜드 (MOMOLAND)",
                R.drawable.img_album_exp5
            )
        )

        val songDBData = songDB.albumDao().getAlbums()
    }

}

