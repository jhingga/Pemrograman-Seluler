import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_TABLE_ACCOUNT = ("CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_EMAIL + " TEXT PRIMARY KEY," + COLUMN_USERNAME + " TEXT,"
                + COLUMN_PASSWORD + " TEXT" + ")")
        db.execSQL(CREATE_TABLE_ACCOUNT)

        val CREATE_TABLE_PHOTOS = ("CREATE TABLE " + TABLE_PHOTOS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT,"
                + COLUMN_PHOTO + " BLOB" + ")")
        db.execSQL(CREATE_TABLE_PHOTOS)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PHOTOS")
        onCreate(db)
    }

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "sesa.db"
        const val TABLE_NAME = "account"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_PASSWORD = "password"

        const val TABLE_PHOTOS = "photos"
        const val COLUMN_ID = "id"
        const val COLUMN_PHOTO = "photo"
    }
}
