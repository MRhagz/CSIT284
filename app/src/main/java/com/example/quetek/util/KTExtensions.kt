import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

// Activity
fun Activity.showToast(msg : String){
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
fun Activity.getStringExtraSafe(key: String, msg : String): String { return intent.getStringExtra(key) ?: msg }

// EditText
fun EditText.checkInput(): Boolean{
    return (this.text.trim().isNullOrEmpty())
}
fun EditText.getTextValue(): String { return this.text.toString().trim() }
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
