import android.content.Context

class SessionManager(context: Context) {
    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val editor = preferences.edit()

    fun saveLogin(email: String) {
        editor.putString(KEY_EMAIL, email)
        editor.apply()
    }

    fun getLogin(): String? {
        return preferences.getString(KEY_EMAIL, null)
    }

    fun clearLogin() {
        editor.remove(KEY_EMAIL)
        editor.apply()
    }

    companion object {
        private const val PREFS_NAME = "session_prefs"
        private const val KEY_EMAIL = "key_email"
    }
}