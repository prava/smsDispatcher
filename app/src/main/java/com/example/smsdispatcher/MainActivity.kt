package com.example.smsdispatcher

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.kittinunf.fuel.Fuel
import kotlinx.android.synthetic.main.activity_main.*
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result;


class MainActivity : AppCompatActivity() {

    private val TAG = "smsdispatcherTAG"
    private val SMS_READ_PERMISSION_CODE = 100
    private val SMS_RECAVE_PERMISSION_CODE = 101
    private val INTERNET_PERMISSION_CODE = 102
    private val READ_STORAGE_PERMISSION_CODE = 103
    private val WRITE_STORAGE_PERMISSION_CODE = 104

    var smsListenerEnabled: Boolean = false;
    var smsDeliveryEnabled: Boolean = false;
    var smsDispatcherEnabled: Boolean = false;

    var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive( context: Context?, intent: Intent? ) {

            if(!smsListenerEnabled)return;

            if(smsDeliveryEnabled) {
                val bundle = intent!!.extras;
                val smsFrom = bundle!!.getString("SMSFrom")
                val smsText = bundle!!.getString("SMSText")
                Log.i(TAG, "SMS From: "+smsFrom)
                Log.i(TAG, "SMS Text: "+smsText)
                val path: String =  urlText.text.toString()+"/?from="+smsFrom+"&msg="+smsText
                Log.i(TAG, "Sending request: "+path)
                statusTextView.text = "have SMS from "+smsFrom
                Run.after(1000, {statusTextView.text = "Ready"})
                //Fuel.get(path)

                val result = Fuel.get(path).responseString(){ _, _, result ->
                    //val res = result.get()
                    return@responseString
                }

                while(!result.isDone){
                    Log.i(TAG, "Sleep")
                    Thread.sleep(1000);

                }
                val respData = String( result.get().data);

                Log.i(TAG, "respData: "+respData)
            }
            else {
                Toast.makeText(context, "SMS recaved but delivery is disabled", Toast.LENGTH_LONG).show();
            }


        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        registerReceiver(broadcastReceiver, IntentFilter("SMS_RECAVED"));

        makeRequest(Manifest.permission.RECEIVE_SMS, SMS_RECAVE_PERMISSION_CODE)

// region SWITCHS
        smsListenerSwitch.setOnCheckedChangeListener { _, isChecked ->
            smsListenerEnabled = isChecked;
            if (isChecked) {
                //Log.i(TAG,"smsListenerSwitch Switch on");
            } else {
                //Log.i(TAG,"smsListenerSwitch Switch off");
            }
        }

        smsDeliverySwitch.setOnCheckedChangeListener { _, isChecked ->
            smsDeliveryEnabled = isChecked;
            if (isChecked) {
                if(!smsListenerSwitch.isChecked) {
                    smsListenerSwitch.isChecked = true;
                }
                //Log.i(TAG,"smsDeliverySwitch Switch on");
            } else {
                //Log.i(TAG,"smsDeliverySwitch Switch off");
            }
        }

        smsDispatcherSwitch.setOnCheckedChangeListener { _, isChecked ->
            smsDispatcherEnabled = isChecked;
            if (isChecked) {
                //Log.i(TAG,"smsDispatcherSwitch Switch on");
            } else {
                //Log.i(TAG,"smsDispatcherSwitch Switch off");
            }
        }
// endregion


// region BUTTONS
        setUrlButton.setOnClickListener {

/*
            repeat(10) { i ->
                println("job: I'm sleeping $i ...")
                Thread.sleep(1000);
            }

 */

            //Toast.makeText(this, "click", Toast.LENGTH_LONG)
            setUrl();
        }

        testUrlButton.setOnClickListener {

            if(urlEditText.text.isEmpty()) {
                showMesage("Error", "Enter dispatch URL", null)
                return@setOnClickListener
            }
            Run.after(10, {testUrl();})
            /*
            Run.after(10, {
                if(false) {
                    println("false")
                } else {
                    testUrl();
                }
            })

             */




        }
// endregion
        urlText.setOnClickListener {
            editUrl();
        }

        urlEditText.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                setUrl();
                return@OnKeyListener true
            }
            false
        })

    }
// region SET/CHECK PERMITIONS
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
//endregion


    private fun showMesage(title: String, msg: String, callback_OK: (() -> Unit?)?) {
        AlertDialog.Builder(this).setTitle(title).setMessage(msg).setPositiveButton("OK"){ _, _ -> if(callback_OK != null) callback_OK();} .create().show();
    }

    private fun setUrl() {

        if(urlEditText.text.isEmpty()) {
            showMesage("Error", "Enter dispatch URL", null)
        }
        else
        {
            urlText.text = urlEditText.text
            setUrlButton.visibility = View.INVISIBLE
            urlEditText.visibility = View.INVISIBLE
            urlText.visibility = View.VISIBLE
            testUrlButton.visibility = View.VISIBLE
        }
    }



    private fun editUrl() {

        Log.i(TAG, "editUrl")
        urlEditText.setText(urlText.text)
        urlEditText.visibility = View.VISIBLE
        setUrlButton.visibility = View.VISIBLE
        urlText.visibility = View.INVISIBLE
    }

    private fun testUrl() {

        //val path = urlText.text.toString() //"https://api.ipify.org?format=json"
        val path = "http://46.101.128.113/sms/?from=111&msg=test"
        val result = Fuel.get(path).responseString(){ _, _, result ->
            when (result) {
                is Result.Failure -> {
                    val ex = result.getException()
                    Log.i(TAG, ex.toString())
                }
                is Result.Success -> {
                    val data = result.get()
                    Log.i(TAG, data)
                }
            }
            //val res = result.get()
            //return@responseString
        }
/*
        while(!result.isDone){
            Log.i(TAG, "Sleep")
            Thread.sleep(1000);

        }
        val respData = String( result.get().data);

        if(respData != "Success") {
            showMesage("URL test failed", respData, null)
            statusTextView.text = "URL test failed"
            //Toast.makeText(applicationContext,"URL test failed: " + respData,Toast.LENGTH_SHORT).show()
            Run.after(5000, {statusTextView.text = "Ready"})


        }
        else {
            testUrlButton.visibility = View.INVISIBLE;
            statusTextView.text = "Ready"
        }

 */
    }
}

class Run {
    companion object {
        fun after(delay: Long, process: () -> Unit) {
            Handler().postDelayed({
                process()
            }, delay)
        }
    }
}