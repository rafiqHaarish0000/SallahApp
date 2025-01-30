package za.co.topitup.salaahapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class StartAppOnPowerON : BroadcastReceiver()  {

    var con: Context? = null

    override fun onReceive(context: Context?, intent: Intent?) {

        con = context


        val intent = Intent(context, MainActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context?.startActivity(intent)
    }
}