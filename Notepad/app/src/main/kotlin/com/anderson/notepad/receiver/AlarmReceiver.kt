package com.anderson.notepad.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class AlarmReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        if("uncaughtException" == intent?.action)  {
            val stackTrace = intent.getStringExtra("stackTrace");
            Toast.makeText(context,stackTrace,Toast.LENGTH_LONG).show();
        }

    }

}