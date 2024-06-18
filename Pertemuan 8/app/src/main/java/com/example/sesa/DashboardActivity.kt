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
import java.io.ByteArrayOutputStream

class DashboardActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var tvWelcome: TextView
    private lateinit var photoContainer: LinearLayout
    private lateinit var btnUploadPhoto: Button

    private lateinit var takePictureLauncher: ActivityResultLauncher<Intent>
    private lateinit var loggedInUsername: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard)

        dbHelper = DatabaseHelper(this)

        tvWelcome = findViewById(R.id.tvWelcome)
        photoContainer = findViewById(R.id.photoContainer)
        btnUploadPhoto = findViewById(R.id.btnUploadPhoto)

        // Get the logged-in username from the intent
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

        loadPhotos()
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
        }
        db.insert(DatabaseHelper.TABLE_PHOTOS, null, values)
        loadPhotos()
    }

    private fun loadPhotos() {
        photoContainer.removeAllViews()
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            DatabaseHelper.TABLE_PHOTOS,
            arrayOf(DatabaseHelper.COLUMN_PHOTO),
            "${DatabaseHelper.COLUMN_USERNAME}=?",
            arrayOf(loggedInUsername),
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            do {
                val photoBlob = cursor.getBlob(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PHOTO))
                val imageView = ImageView(this)
                imageView.setImageBitmap(byteArrayToBitmap(photoBlob))
                photoContainer.addView(imageView)
            } while (cursor.moveToNext())
        } else {
            val noPhotoTextView = TextView(this)
            noPhotoTextView.text = "No picture uploaded"
            photoContainer.addView(noPhotoTextView)
        }

        cursor.close()
    }

    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    private fun byteArrayToBitmap(byteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
}