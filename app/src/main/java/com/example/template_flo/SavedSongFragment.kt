package com.example.template_flo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.template_flo.databinding.FragmentSavedSongBinding
import com.google.gson.Gson

class SavedSongFragment : Fragment() {

    private var albumDatas = ArrayList<Album>()
    lateinit var binding : FragmentSavedSongBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSavedSongBinding.inflate(inflater, container, false)

        albumDatas.apply{
            add(Album("Butter", "방탄소년단 (BTS)", R.drawable.img_album_exp, Song("Butter", "방탄소년단(BTS)", 0, 60, false, "music_butter")))
            add(Album("Lilac", "아이유 (IU)", R.drawable.img_album_exp2, Song("라일락", "아이유(IU)", 0, 60, false, "music_lilac")))
            add(Album("Next Level", "에스파 (AESPA)", R.drawable.img_album_exp3, Song("Next Level", "에스파(AESPA)", 0, 60, false, "music_next")))
            add(Album("Boy with Luv", "방탄소년단 (BTS)", R.drawable.img_album_exp4, Song("작은 것들을 위한 시", "방탄소년단(BTS)", 0, 60, false, "music_boy")))
            add(Album("BBoom BBoom", "모모랜드 (MOMOLAND)", R.drawable.img_album_exp5, Song("뿜뿜", "모모랜드(MOMOLAND)", 0, 60, false, "music_bboom")))
            add(Album("Weekend", "태연 (TaeYeon)", R.drawable.img_album_exp6, Song("Weekend", "태연(TaeYeon)", 0, 60, false, "music_flu")))
            add(Album("Butter", "방탄소년단 (BTS)", R.drawable.img_album_exp, Song("Butter", "방탄소년단(BTS)", 0, 60, false, "music_butter")))
            add(Album("Lilac", "아이유 (IU)", R.drawable.img_album_exp2, Song("라일락", "아이유(IU)", 0, 60, false, "music_lilac")))
            add(Album("Next Level", "에스파 (AESPA)", R.drawable.img_album_exp3, Song("Next Level", "에스파(AESPA)", 0, 60, false, "music_next")))
            add(Album("Boy with Luv", "방탄소년단 (BTS)", R.drawable.img_album_exp4, Song("작은 것들을 위한 시", "방탄소년단(BTS)", 0, 60, false, "music_boy")))
            add(Album("BBoom BBoom", "모모랜드 (MOMOLAND)", R.drawable.img_album_exp5, Song("뿜뿜", "모모랜드(MOMOLAND)", 0, 60, false, "music_bboom")))
            add(Album("Weekend", "태연 (TaeYeon)", R.drawable.img_album_exp6, Song("Weekend", "태연(TaeYeon)", 0, 60, false, "music_flu")))
            add(Album("Butter", "방탄소년단 (BTS)", R.drawable.img_album_exp, Song("Butter", "방탄소년단(BTS)", 0, 60, false, "music_butter")))
            add(Album("Lilac", "아이유 (IU)", R.drawable.img_album_exp2, Song("라일락", "아이유(IU)", 0, 60, false, "music_lilac")))
            add(Album("Next Level", "에스파 (AESPA)", R.drawable.img_album_exp3, Song("Next Level", "에스파(AESPA)", 0, 60, false, "music_next")))
            add(Album("Boy with Luv", "방탄소년단 (BTS)", R.drawable.img_album_exp4, Song("작은 것들을 위한 시", "방탄소년단(BTS)", 0, 60, false, "music_boy")))
            add(Album("BBoom BBoom", "모모랜드 (MOMOLAND)", R.drawable.img_album_exp5, Song("뿜뿜", "모모랜드(MOMOLAND)", 0, 60, false, "music_bboom")))
            add(Album("Weekend", "태연 (TaeYeon)", R.drawable.img_album_exp6, Song("Weekend", "태연(TaeYeon)", 0, 60, false, "music_flu")))
        }

        val savedSongRVAdpater = SavedSongRVAdapter(albumDatas)
        binding.savedSongRv.adapter = savedSongRVAdpater
        binding.savedSongRv.layoutManager = LinearLayoutManager(requireActivity())

        savedSongRVAdpater.setItemClickListener(object: SavedSongRVAdapter.OnItemClickListener {
            override fun onItemClick(album: Album) {
                (context as MainActivity).supportFragmentManager.beginTransaction().replace(R.id.main_frm, AlbumFragment().apply {
                    arguments = Bundle().apply {
                        val gson = Gson()
                        val albumJson = gson.toJson(album)
                        putString("album", albumJson)
                    }
                }).commitAllowingStateLoss()
            }

            override fun onRemoveAlbum(position: Int) {
                savedSongRVAdpater.removeItem(position)
            }

        })

        return binding.root
    }
}