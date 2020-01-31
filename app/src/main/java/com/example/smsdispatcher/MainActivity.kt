package com.example.smsdispatcher

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val TAG = "smsdispatcherTag"
    private val SMS_READ_PERMISSION_CODE = 100
    private val SMS_RECAVE_PERMISSION_CODE = 101
    private val INTERNET_PERMISSION_CODE = 102
    private val READ_STORAGE_PERMISSION_CODE = 103
    private val WRITE_STORAGE_PERMISSION_CODE = 104





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        smsListenerSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                Log.i(TAG,"smsListenerSwitch Switch on");
            } else {
                Log.i(TAG,"smsListenerSwitch Switch off");
            }
        }

        smsDeliverySwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                if(!smsListenerSwitch.isChecked) {
                    smsListenerSwitch.isChecked = true;
                }
                Log.i(TAG,"smsDeliverySwitch Switch on");
            } else {
                Log.i(TAG,"smsDeliverySwitch Switch off");
            }
        }

        smsDispatcherSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                Log.i(TAG,"smsDispatcherSwitch Switch on");
            } else {
                Log.i(TAG,"smsDispatcherSwitch Switch off");
            }
        }

        setUrlButton.setOnClickListener {
            Toast.makeText(this, "click", Toast.LENGTH_LONG)
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

    private  fun setupPermissions() {
        makeRequest(Manifest.permission.INTERNET, INTERNET_PERMISSION_CODE)
        makeRequest(Manifest.permission.READ_SMS, SMS_READ_PERMISSION_CODE)
        makeRequest(Manifest.permission.RECEIVE_SMS, SMS_RECAVE_PERMISSION_CODE)
        makeRequest(Manifest.permission.READ_EXTERNAL_STORAGE, READ_STORAGE_PERMISSION_CODE)
        makeRequest(Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_STORAGE_PERMISSION_CODE)
    }

    private fun checkPermition(permitionTyoe: String): Int {
        return ContextCompat.checkSelfPermission(this, permitionTyoe)
    }

    private fun makeRequest(permitionTyoe: String, PermitionCode: Int) {
        ActivityCompat.requestPermissions(this, arrayOf(permitionTyoe), PermitionCode)
    }

    private fun showMesage(ttl: String, txt: String, callback: (() -> Unit?)?) {
        AlertDialog.Builder(this).setTitle(ttl).setMessage(txt).setPositiveButton("OK"){ dialog, which -> if(callback != null) callback();} .create().show();
    }

    private fun setUrl() {
        if(urlEditText.text.isEmpty()) {
            //showMesage("Error", "Enter dispatch URL")
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

