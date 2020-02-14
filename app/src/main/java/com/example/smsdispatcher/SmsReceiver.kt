package com.example.smsdispatcher

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.SmsMessage
import android.util.Log


class SmsReceiver : BroadcastReceiver() {


    private val TAG = "smsdispatcherTAG"
    val pdu_type = "pdus"

    override fun onReceive(context: Context, intent: Intent) {



        val bundle = intent.extras
        val msgs: Array<SmsMessage?>
        var strMessage = ""
        val format = bundle!!.getString("format")





        val pdus = bundle[pdu_type] as Array<ByteArray>?
        val isVersionM: Boolean = false;

        // Fill the msgs array.
        msgs = arrayOfNulls<SmsMessage>(5);

        if (pdus != null) {
            msgs[0] = SmsMessage.createFromPdu(pdus[0], format)
        }

        strMessage += "SMS from " + msgs[0]?.getOriginatingAddress();
        strMessage += " :" + msgs[0]?.getMessageBody() + "\n";




        //Toast.makeText(context, strMessage, Toast.LENGTH_LONG).show();
        Log.i(TAG, strMessage);


//        context.sendBroadcast(Intent("SMS_RECAVED"));


        val `in` = Intent("SMS_RECAVED")
        val extras = Bundle()
        extras.putString("SMSFrom", msgs[0]?.getOriginatingAddress())
        extras.putString("SMSText", msgs[0]?.getMessageBody())
        `in`.putExtras(extras)
        context.sendBroadcast(`in`)


    }


}
