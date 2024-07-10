package com.example.sesa

import DatabaseHelper
import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class DashboardActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var tvWelcome: TextView
    private lateinit var btnUploadPhoto: Button
    private lateinit var recyclerViewPhotos: RecyclerView
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var photoAdapter: PhotoAdapter

    private lateinit var takePictureLauncher: ActivityResultLauncher<Intent>
    private lateinit var loggedInUsername: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard)

        dbHelper = DatabaseHelper(this)

        tvWelcome = findViewById(R.id.tvWelcome)
        btnUploadPhoto = findViewById(R.id.btnUploadPhoto)
        recyclerViewPhotos = findViewById(R.id.recyclerViewPhotos)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        loggedInUsername = intent.getStringExtra("username") ?: "User"

        tvWelcome.text = "Welcome $loggedInUsername, how's your day?"

        takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as Bitmap
                savePhotoToDatabase(imageBitmap)
            }
        }

        btnUploadPhoto.setOnClickListener {
            dispatchTakePictureIntent()
        }

        setupRecyclerView()
        setupBottomNavigationView()

        loadPhotos()  // Load all photos initially
    }

    private fun setupRecyclerView() {
        photoAdapter = PhotoAdapter { photoId, isFavorite ->
            toggleFavorite(photoId, isFavorite)
        }
        recyclerViewPhotos.apply {
            layoutManager = LinearLayoutManager(this@DashboardActivity)
            adapter = photoAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    private fun setupBottomNavigationView() {
        bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_all_photos -> {
                    loadPhotos()
                    true
                }
                R.id.nav_favorite_photos -> {
                    loadFavoritePhotos()
                    true
                }
                else -> false
            }
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            takePictureLauncher.launch(takePictureIntent)
        }
    }

    private fun savePhotoToDatabase(imageBitmap: Bitmap) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_USERNAME, loggedInUsername)
            put(DatabaseHelper.COLUMN_PHOTO, bitmapToByteArray(imageBitmap))
            put(DatabaseHelper.COLUMN_DATE, getCurrentDate())
            put(DatabaseHelper.COLUMN_IS_FAVORITE, 0)
        }
        db.insert(DatabaseHelper.TABLE_PHOTOS, null, values)
        loadPhotos()
    }

    private fun loadPhotos() {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            DatabaseHelper.TABLE_PHOTOS,
            null,  // ambil semua kolom
            "${DatabaseHelper.COLUMN_USERNAME}=?",
            arrayOf(loggedInUsername),
            null,
            null,
            null
        )

        val photos = mutableListOf<Photo>()
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID))
                val photoBlob = cursor.getBlob(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PHOTO))
                val date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE)) ?: ""
                val isFavorite = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IS_FAVORITE)) == 1
                photos.add(Photo(id, byteArrayToBitmap(photoBlob), date, isFavorite))
            } while (cursor.moveToNext())
        }
        cursor.close()
        photoAdapter.submitList(photos)
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
                val date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE)) ?: ""
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
        loadPhotos()
    }

    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    private fun byteArrayToBitmap(byteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }
}