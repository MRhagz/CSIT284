import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.quetek.LandingActivity
import com.example.quetek.LoginActivity
import com.example.quetek.R

class NotificationHelper(private val context: Context) {

    private val NOTIFICATION_ID = 1
    private val REQUEST_CODE_PERMISSION = 100

    private val PREFS_NAME = "NotificationSettings"
    private val PREF_VIBRATE_KEY = "vibrateEnabled"
    private val PREF_SOUND_KEY = "soundEnabled"

    private val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private var vibrateEnabled: Boolean = true
    private var soundEnabled: Boolean = true

    init {
        val (savedVibrate, savedSound) = getSavedSettings()
        vibrateEnabled = savedVibrate
        soundEnabled = savedSound
    }

    fun saveSoundSettings(sound: Boolean) {
        soundEnabled = sound

        val editor = sharedPreferences.edit()
        editor.putBoolean(PREF_SOUND_KEY, sound)
        editor.apply()
    }

    fun saveVibrateSettings(vibrate: Boolean) {
        vibrateEnabled = vibrate

        val editor = sharedPreferences.edit()
        editor.putBoolean(PREF_VIBRATE_KEY, vibrate)
        editor.apply()
    }

    private fun getSavedSettings(): Pair<Boolean, Boolean> {
        val vibrate = sharedPreferences.getBoolean(PREF_VIBRATE_KEY, true)
        val sound = sharedPreferences.getBoolean(PREF_SOUND_KEY, true)
        return Pair(vibrate, sound)
    }

    fun notificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (context is Activity) {
                    ActivityCompat.requestPermissions(
                        context,
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        REQUEST_CODE_PERMISSION
                    )
                }
                return
            }
        }

    }

    fun onRequestPermissionsResult(requestCode: Int, grantResults: IntArray, str : String) {
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showNotification(str)
            } else {
                Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun showNotification(str: String) {
        val (vibrateEnabled, soundEnabled) = getSavedSettings()
        val channelId = if (soundEnabled) "queueNotificationWithSound" else "queueNotificationSilent"
        val soundUri = if (soundEnabled) RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION) else null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Default Channel",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setSound(soundUri, null)
                enableVibration(vibrateEnabled)
            }

            val systemNotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            systemNotificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(context, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.quetek_logo)
            .setContentTitle("QueTek")
            .setContentText(str)
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(pendingIntent)

        if (soundEnabled) {
            builder.setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
        } else {
            builder.setDefaults(0)
        }

        if (vibrateEnabled) {
            builder.setVibrate(longArrayOf(0, 200, 1000))
        } else {
            builder.setVibrate(longArrayOf(0))
        }

        val compatNotificationManager = NotificationManagerCompat.from(context)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        compatNotificationManager.notify(NOTIFICATION_ID, builder.build())
    }


}
