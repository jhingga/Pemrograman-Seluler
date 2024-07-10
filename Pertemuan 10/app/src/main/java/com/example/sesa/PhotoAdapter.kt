package com.example.sesa

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

data class Photo(val id: Int, val bitmap: Bitmap, val date: String, val isFavorite: Boolean)

class PhotoAdapter(private val toggleFavoriteCallback: (Int, Boolean) -> Unit) :
    ListAdapter<Photo, PhotoAdapter.PhotoViewHolder>(PhotoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_photo, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)
        private val textViewDate: TextView = itemView.findViewById(R.id.textViewDate)
        private val buttonFavorite: Button = itemView.findViewById(R.id.buttonFavorite)

        fun bind(photo: Photo) {
            imageView.setImageBitmap(photo.bitmap)
            textViewDate.text = photo.date
            buttonFavorite.text = if (photo.isFavorite) "Unfavorite" else "Favorite"
            buttonFavorite.setOnClickListener {
                toggleFavoriteCallback(photo.id, !photo.isFavorite)
            }
        }
    }

    class PhotoDiffCallback : DiffUtil.ItemCallback<Photo>() {
        override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean {
            return oldItem == newItem
        }
    }
}