package za.co.topitup.salaahapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.util.Log


class AlarmReciver : BroadcastReceiver() {
    private var mediaPlayer: MediaPlayer? = null
    var con: Context? = null

    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    private val channelId = "i.apps.notifications"
    private val description = "Test notification"
    override fun onReceive(context: Context, intent: Intent) {
        con = context
        fromAlarm = true

        val from_receive = intent.getStringExtra("to_open")
        val alarm_set = intent.getStringExtra("alarm_set")
        val alarm_play = intent.getBooleanExtra("alarm_play",true) as Boolean
        val alarm_stop = intent.getBooleanExtra("alarm_stop",true) as Boolean



        val sharedPref = context.getSharedPreferences("salaah", Context.MODE_PRIVATE)

//        val  alarm_play = sharedPref.getBoolean("alarm1_play", true)

        Log.e("alarm receiver", "receiver11111"+alarm_play);

        with(sharedPref.edit()) {
            putBoolean("alarm1_play", true)
            apply()
            commit()
        }
        if(alarm_play) {
            Log.e("alarm receiver","$alarm_set........received....if"+alarm_stop)
            if (from_receive.equals("false")) {
                if(!alarm_stop){
                    Log.e("alarm receiver","$alarm_set........received....if"+alarm_stop)

                    if (alarm_set.equals("true")) {
                        val serviceIntent = Intent(context, BackgroundSoundService::class.java)
                        context.startForegroundService(serviceIntent)
                    }else{
                        val intent = Intent(context, MainActivity::class.java)
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

                        context.startActivity(intent)
                    }
                }else{
                    val intent = Intent(context, MainActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

                    context.startActivity(intent)
                }


            } else {
                val intent = Intent(context, MainActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

                context.startActivity(intent)
            }
        }else{
            Log.e("alarm receiver","........received....if")

            val intent = Intent(context, MainActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

            context.startActivity(intent)
        }

    }

    companion object {
        var fromAlarm = false
    }


}