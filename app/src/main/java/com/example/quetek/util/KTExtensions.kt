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
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import android.graphics.Color
import android.view.Gravity
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.example.quetek.LoginActivity
import com.example.quetek.R
import com.facebook.shimmer.ShimmerFrameLayout
import kotlinx.coroutines.Job
import android.widget.LinearLayout

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

fun ShimmerFrameLayout.stopShimmerNull(){
    this.stopShimmer()
    this.setShimmer(null)
}

fun View.setVisibilityVisible(){
    this.visibility = View.VISIBLE
}

fun View.setVisibilityToggle(){
    if(this.visibility == View.GONE || this.visibility == View.INVISIBLE){
        this.visibility = View.VISIBLE
    } else {
        this.visibility = View.GONE
    }
}

fun View.setVisibilityGone(){
    this.visibility = View.GONE
}

fun TextView.textReturn(frame : ShimmerFrameLayout) {
    frame.stopShimmerNull()
    val topMarginInDp = 5
    val startMarginInDp = 0

    val topMarginInPx = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        topMarginInDp.toFloat(),
        resources.displayMetrics
    ).toInt()

    val startMarginInPx = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        startMarginInDp.toFloat(),
        resources.displayMetrics
    ).toInt()

    val params = FrameLayout.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )

    params.topMargin = topMarginInPx
    params.marginStart = startMarginInPx

    params.gravity = Gravity.CENTER
    this.layoutParams = params

    this.setBackgroundColor(Color.WHITE)
    this.textAlignment = View.TEXT_ALIGNMENT_CENTER
}


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








