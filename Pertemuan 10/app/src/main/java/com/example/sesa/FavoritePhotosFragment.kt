package com.example.sesa

import DatabaseHelper
import android.content.ContentValues
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FavoritePhotosFragment : Fragment() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var recyclerViewFavoritePhotos: RecyclerView
    private lateinit var photoAdapter: PhotoAdapter
    private lateinit var loggedInUsername: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorite_photos, container, false)
        recyclerViewFavoritePhotos = view.findViewById(R.id.recyclerViewFavoritePhotos)
        dbHelper = DatabaseHelper(requireContext())
        loggedInUsername = arguments?.getString("username") ?: "User"

        setupRecyclerView()
        loadFavoritePhotos()

        return view
    }

    private fun setupRecyclerView() {
        photoAdapter = PhotoAdapter { photoId, isFavorite ->
            toggleFavorite(photoId, isFavorite)
        }
        recyclerViewFavoritePhotos.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = photoAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    private fun loadFavoritePhotos() {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            DatabaseHelper.TABLE_PHOTOS,
            null,
            "${DatabaseHelper.COLUMN_USERNAME}=? AND ${DatabaseHelper.COLUMN_IS_FAVORITE}=?",
            arrayOf(loggedInUsername, "1"),
            null,
            null,
            null
        )

        val photos = mutableListOf<Photo>()
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID))
                val photoBlob = cursor.getBlob(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PHOTO))
                val date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE))
                val isFavorite = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IS_FAVORITE)) == 1
                photos.add(Photo(id, byteArrayToBitmap(photoBlob), date, isFavorite))
            } while (cursor.moveToNext())
        }
        cursor.close()
        photoAdapter.submitList(photos)
    }

    private fun toggleFavorite(photoId: Int, isFavorite: Boolean) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_IS_FAVORITE, if (isFavorite) 1 else 0)
        }
        db.update(DatabaseHelper.TABLE_PHOTOS, values, "${DatabaseHelper.COLUMN_ID}=?", arrayOf(photoId.toString()))
        loadFavoritePhotos()
    }

    private fun byteArrayToBitmap(byteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
}