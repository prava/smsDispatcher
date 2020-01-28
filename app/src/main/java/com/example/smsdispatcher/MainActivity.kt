package com.example.smsdispatcher

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val TAG = "smsdispatcherTag"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        smsDispatcherSwitch.isEnabled = false;

        smsListenerSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                Log.i(TAG,"Switch on");
            } else {
                Log.i(TAG,"Switch off");
            }
        }

        smsDeliverySwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                if(!smsListenerSwitch.isChecked)
                    smsListenerSwitch.isChecked = true;
                Log.i(TAG,"Switch on");
            } else {
                Log.i(TAG,"Switch off");
            }
        }

        smsDispatcherSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                Log.i(TAG,"Switch on");
            } else {
                Log.i(TAG,"Switch off");
            }
        }

        setUrlButton.setOnClickListener {
            setUrl();
        }

        urlText.setOnClickListener {
            editUrl();
        }

        urlEditText.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                setUrl();
                return@OnKeyListener true
            }
            false
        })

    }

    private fun showMesage(ttl: String, txt: String) {
        val alert = AlertDialog.Builder(this);
        alert.setTitle(ttl).setMessage(txt).setPositiveButton("OK"){
            dialog, which -> {

        }
        }.create().show();
    }

    private fun setUrl() {
        if(urlEditText.text.isEmpty()) {
            showMesage("Error", "Enter dispatch URL")
        }
        else
        {
            urlText.text = urlEditText.text
            setUrlButton.visibility = View.INVISIBLE
            urlEditText.visibility = View.INVISIBLE
            urlText.visibility = View.VISIBLE

        }
    }

    private fun editUrl()
    {
        urlEditText.setText(urlText.text)
        urlEditText.visibility = View.VISIBLE
        setUrlButton.visibility = View.VISIBLE
        urlText.visibility = View.INVISIBLE
    }

}
