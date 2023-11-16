package com.example.template_flo

import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.template_flo.databinding.ItemSavesongBinding

class SavedSongRVAdapter(private val albumList: ArrayList<Album>) : RecyclerView.Adapter<SavedSongRVAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(album: Album)
        fun onRemoveAlbum(position: Int)
    }

    private val switchStatus = SparseBooleanArray()
    private lateinit var onItemClickListener: OnItemClickListener

    fun setItemClickListener(itemClickListener: OnItemClickListener){
        onItemClickListener = itemClickListener
    }

    fun removeItem(position: Int){
        albumList.removeAt(position)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): SavedSongRVAdapter.ViewHolder {
        val binding: ItemSavesongBinding = ItemSavesongBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SavedSongRVAdapter.ViewHolder, position: Int) {
        holder.bind(albumList[position])
        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(albumList[position])
        }
        holder.binding.itemLockerAlbumMoreIv.setOnClickListener { onItemClickListener.onRemoveAlbum(position) }

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

    override fun getItemCount(): Int = albumList.size

    inner class ViewHolder(val binding: ItemSavesongBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(album: Album){
            binding.itemLockerAlbumTitleTv.text = album.title
            binding.itemLockerAlbumSingerTv.text = album.singer
            binding.itemLockerAlbumCoverImgIv.setImageResource(album.coverImg!!)

        }
    }

}