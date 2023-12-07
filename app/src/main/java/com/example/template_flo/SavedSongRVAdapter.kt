package com.example.template_flo

import android.annotation.SuppressLint
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.template_flo.databinding.ItemSavesongBinding

class SavedSongRVAdapter() : RecyclerView.Adapter<SavedSongRVAdapter.ViewHolder>() {

    interface OnItemClickListener {
        // fun onItemClick(album: Album)
        fun onItemClick(song: Song)

       //  fun onRemoveAlbum(position: Int)
    }

    interface MyItemClickListener{
        fun onRemoveSong(songId: Int)
    }

    private val switchStatus = SparseBooleanArray()
    private lateinit var onItemClickListener: OnItemClickListener
    private lateinit var mItemClickListener : MyItemClickListener
    private val songs = ArrayList<Song>()

    fun setItemClickListener(itemClickListener: OnItemClickListener){
        onItemClickListener = itemClickListener
    }
    fun setMyItemClickListener(itemClickListener: MyItemClickListener){
        mItemClickListener = itemClickListener
    }

    /*fun removeItem(position: Int){
        albumList.removeAt(position)
        notifyDataSetChanged()
    }*/


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): SavedSongRVAdapter.ViewHolder {
        val binding: ItemSavesongBinding = ItemSavesongBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SavedSongRVAdapter.ViewHolder, position: Int) {
        holder.bind(songs[position])
        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(songs[position])
        }
        holder.binding.itemLockerAlbumMoreIv.setOnClickListener {
            //onItemClickListener.onRemoveAlbum(position)
            mItemClickListener.onRemoveSong(songs[position].id)
            removeSong(position)
        }

        val switch =  holder.binding.switchRV
        switch.isChecked = switchStatus[position]
        switch.setOnClickListener {
            if (switch.isChecked) {
                switchStatus.put(position, true)
            }
            else {
                switchStatus.put(position, false)
            }

            notifyItemChanged(position)
        }

    }

    @SuppressLint("NotifyDataSetChanged") // 경고 무시 어노테이션
    fun addSongs(songs: ArrayList<Song>) {
        this.songs.clear()
        this.songs.addAll(songs)

        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun removeSong(position: Int){
        songs.removeAt(position)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = songs.size

    inner class ViewHolder(val binding: ItemSavesongBinding) : RecyclerView.ViewHolder(binding.root){
        /*fun bind(album: Album){
            binding.itemLockerAlbumTitleTv.text = album.title
            binding.itemLockerAlbumSingerTv.text = album.singer
            binding.itemLockerAlbumCoverImgIv.setImageResource(album.coverImg!!)

        }*/

        fun bind(song: Song){
            binding.itemLockerAlbumCoverImgIv.setImageResource(song.coverImg!!)
            binding.itemLockerAlbumTitleTv.text = song.title
            binding.itemLockerAlbumSingerTv.text = song.singer
        }
    }

}