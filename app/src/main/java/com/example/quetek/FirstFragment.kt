package com.example.quetek

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.lifecycle.findViewTreeViewModelStoreOwner

class FirstFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_first, container, false)

        val btnNotifyMe = view.findViewById<Button>(R.id.btnNotifyMe)
        val btnJoinQueue = view.findViewById<Button>(R.id.btnJoinQueue)
        val btnPriorityQueue = view.findViewById<Button>(R.id.btnPriorityLane)
        val ibtnMenu = view.findViewById<ImageButton>(R.id.ibtnMenu)

        ibtnMenu.setOnClickListener {
//            toast("Settings for now kay naa kang classmate ang screen.")

            Log.e("QueTek", "Navigating to SettingsActivity")
//
//            val intent = Intent(this, SettingsActivity::class.java)
//            startActivity(intent)
        }

        btnNotifyMe.setOnClickListener {
            // TODO
//            toast("Si teammate nag himo, sir!")
        }

        btnJoinQueue.setOnClickListener {
            Log.e("QueTek", "Feature underdevelopment.")
//            toast("Feature underdevelopment.")
        }
        btnPriorityQueue.setOnClickListener {
            Log.e("QueTek", "Feature underdevelopment.")
//            toast("Feature underdevelopment.")
        }

        return view
    }

    private fun toast(message: String) {
//        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
