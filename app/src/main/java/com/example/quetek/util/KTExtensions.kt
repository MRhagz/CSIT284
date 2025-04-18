import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import com.example.quetek.LoginActivity
import com.example.quetek.R
import kotlinx.coroutines.Job

// Activity
fun Activity.showToast(msg : String){
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
fun Activity.getStringExtraSafe(key: String, msg : String): String { return intent.getStringExtra(key) ?: msg }

// EditText
fun EditText.checkInput(): Boolean{
    return (this.text.trim().isNullOrEmpty())
}
fun EditText.getTextValue(): String { return this.text.toString() }
fun EditText.getTextOrMessage(message: String): String {
    val input = this.getTextValue()
    return if (input.isEmpty()) message else input
}
fun EditText.clearText() { this.text.clear() }


// BUTTON
fun Button.disable() { this.isEnabled = false }
fun Button.enable() { this.isEnabled = true }

@SuppressLint("MissingInflatedId")
fun EditText.isEmailValid(): Boolean {
    val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
    return emailRegex.toRegex().matches(this.text.toString())
}

// CONTEXT
fun Context.createIntent(target: Class<*>): Intent { return Intent(this, target) }
fun Context.intentPutExtra(activity: Activity, intent: Intent, key: Array<String>, value: Array<String>) {
    if (key.size != value.size) {
        Log.e("Error","Keys and values must have the same length"); return;
    }

    key.forEachIndexed { index, k ->
        intent.putExtra(k, value[index])
    }

}
fun Activity.showFullscreenLoadingDialog(): Dialog {
    val dialog = Dialog(this)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(R.layout.loading_screen)
    dialog.setCancelable(false)

    // Make it fullscreen
    dialog.window?.setLayout(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    )
    dialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
    dialog.window?.setFlags(
        WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN
    )

    // Setup the progress bar
    val progressBar: ProgressBar = dialog.findViewById(R.id.progressBar)
    val colors = listOf(
        ContextCompat.getColor(this, R.color.colorAccent),
        ContextCompat.getColor(this, R.color.colorPrimaryDark)
    )

    var currentColorIndex = 0

    // Handler to change the progress bar color at intervals
    val handler = Handler(Looper.getMainLooper())

    val colorChanger = object : Runnable {
        override fun run() {
            val color = colors[currentColorIndex]
            progressBar.indeterminateTintList = ColorStateList.valueOf(color)
            currentColorIndex = (currentColorIndex + 1) % colors.size
            handler.postDelayed(this, 2000) // Change color every 800ms
        }
    }

    // Start changing colors
    handler.post(colorChanger)

    dialog.show()
    return dialog
}








