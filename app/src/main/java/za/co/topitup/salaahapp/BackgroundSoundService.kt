package za.co.topitup.salaahapp

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import java.io.IOException
import kotlin.random.Random


private const val ACTION_PLAY: String = "com.example.action.PLAY"

public var isStop:Boolean = false
class BackgroundSoundService : Service() {
    private var action: String? = "extra_action"
    var player: MediaPlayer? = null
    private val STOP_ID = 2
    val EXTRA_ACTION = "extra_action"
    val ACTION_STOP = "action_stop"
    private val NOTIFICATION_REQUEST_CODE = 0

    private val NOTIFICATION_CATEGORY = "notification_category"
    private lateinit var runnable: Runnable

    private var handler: Handler = Handler()

    override fun onBind(arg0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        isStop = false
        Log.e("service created","stop1......")
        if (Build.VERSION.SDK_INT >= 26) {
            val CHANNEL_ID = "my_channel_01"
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
                channel
            )
            //intent to open app

            //intent to open app
            val entryActivityIntent = Intent(applicationContext, MainActivity::class.java)

            entryActivityIntent.putExtra("fromNotification","true")
//            entryActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            val pendingEntryActivityIntent = PendingIntent.getActivity(
                applicationContext,
                NOTIFICATION_REQUEST_CODE,
                entryActivityIntent,
                PendingIntent.FLAG_MUTABLE
            )


            val stopServiceIntent = Intent(applicationContext, BackgroundSoundService::class.java)
            stopServiceIntent.putExtra(EXTRA_ACTION, ACTION_STOP)
            stopServiceIntent.addCategory(NOTIFICATION_CATEGORY)
            val pendingStopForeground = PendingIntent.getService(
                applicationContext, STOP_ID, stopServiceIntent, PendingIntent.FLAG_MUTABLE
            )

            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Salaah App")
                .setSmallIcon(R.drawable.salaah_app_01)
                .addAction(R.drawable.salaah_app_01, "Stop", pendingStopForeground)
                .setContentIntent(pendingEntryActivityIntent)
                .setContentText("Prayer Time").build()
            startForeground(1, notification)
        }

        val sharedPreference =  getSharedPreferences("salaah",Context.MODE_PRIVATE)
        val selectedId = sharedPreference.getInt(getString(R.string.saved_high_score_key), 0)
        val getFrom: String = selectedId.toString()

        Log.e("service ","received from shared........"+getFrom )

        if (selectedId == 0) {
            var selectedRandom :Int = generateRandom()
            if(selectedRandom==1) {
                player = MediaPlayer.create(this, R.raw.a1)
            }else if(selectedRandom ==2){
                player = MediaPlayer.create(this, R.raw.a2)
            }else if(selectedRandom ==3){
                player = MediaPlayer.create(this, R.raw.a3)
            }
        } else if (selectedId == 1) {

            player = MediaPlayer.create(this, R.raw.a1)

        } else if (selectedId == 2) {
            player = MediaPlayer.create(this, R.raw.a2)


        } else if (selectedId == 3) {
            player = MediaPlayer.create(this, R.raw.a3)



        }

       /* if(selectedId == 0){
            player = MediaPlayer.create(this, R.raw.a1)
        }else if(selectedId == 1){
            player = MediaPlayer.create(this, R.raw.a2)

        }else if(selectedId == 2){
            player = MediaPlayer.create(this, R.raw.a3)

        }*//*else if(selectedId == 3){
            player = MediaPlayer.create(this, R.raw.a4)

        }else if(selectedId == 4){
            player = MediaPlayer.create(this, R.raw.a5)

        }else if(selectedId == 5){
            player = MediaPlayer.create(this, R.raw.a6)
        }else if(selectedId == 6){
            player = MediaPlayer.create(this, R.raw.a7)
        }*/
    }

    private fun generateRandom(): Int {
        val randomNum = Random.nextInt(1, 3 + 1)

        return randomNum
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e("service created","stop1...command...")

        try {
//            val sharedPref = getSharedPreferences(Context.MODE_PRIVATE) ?: return

            runnable = Runnable {

                Log.e("is stop","background........."+isStop)
                if(!isStop){
                    getRequestToDownload()

                    handler.postDelayed(runnable, 2*1000)
                }else{
                    handler.removeCallbacks(runnable)
                    isStop=false

                }

            }
            handler.postDelayed(runnable, 1000)
            if(intent != null){
                 action = intent!!.getStringExtra(EXTRA_ACTION)
            }
            if (action == ACTION_STOP) {
                if(player!=null) {
                    if(player!!.isPlaying())
                        player!!.stop();
//                    player!!.reset();
                    player!!.release();
                    player=null;
                }
                isStop = true

                handler.removeCallbacks(runnable)

                stopSelf()
            } else {
                player!!.setOnCompletionListener(OnCompletionListener {
                    handler.removeCallbacks(runnable)
                    isStop = true
                    stopSelf()
                })
                player!!.start()
                var isAppFore = isAppOnForeground(this," za.co.topitup.salaahapp")
                if(isAppFore){
                    var intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("fromNotification","true")
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return START_STICKY
    }


    override fun onStart(intent: Intent?, startId: Int) {
        // TO
    }

    fun onUnBind(arg0: Intent?): IBinder? {
        // TO DO Auto-generated method
        return null
    }

    fun onStop() {}
    fun onPause() {}
    override fun onDestroy() {
        handler.removeCallbacks(runnable)
        isStop = true

        stopSelf()
        if(player!=null) {
            if(player!!.isPlaying())
                player!!.stop();
            player!!.reset();
            player!!.release();
            player=null;

        }

    }

    override fun onLowMemory() {}

    companion object {
        private val TAG: String? = null
    }

    private fun isAppOnForeground(context: Context, appPackageName: String): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningAppProcesses = activityManager.runningAppProcesses ?: return false
        return runningAppProcesses.any { it.processName == context.packageName && it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND }

    }

    @SuppressLint("Range")
    fun getRequestToDownload() {
        // inserting complete table details in this text field
        val contactUri = Uri.parse("content://za.co.topitup/products")

        //columns to select
        val projection: Array<String>? = null

        //criteria for selecting row
        val selectionClause: String? = null
        //arguments
        val selectionArguments: Array<String>? = null
        //sort order
        val sortOrder: String? = null
        //query
        val cursor = contentResolver.query(
            contactUri,
            projection,
            selectionClause,
            selectionArguments,
            sortOrder
        )

        //our product data string
        val stringBuilder = StringBuilder()

       var  name:String =""
        //check cursor and iterate over
        if (cursor != null && cursor.moveToFirst()) {
            do {
                        stringBuilder.append("ID: ")
                stringBuilder.append(cursor.getString(cursor.getColumnIndexOrThrow("id")))
                stringBuilder.append("\t")
                stringBuilder.append("\t")

               stringBuilder.append("Name: ")
                 stringBuilder.append(cursor.getString(cursor.getColumnIndexOrThrow("name"))).toString()
                stringBuilder.append("\t")
                stringBuilder.append("\t")
                stringBuilder.append("Price: ")
                stringBuilder.append(cursor.getString(cursor.getColumnIndexOrThrow("price")))
                stringBuilder.append("\n")
                stringBuilder.append("\n")

                name=  cursor.getString(cursor.getColumnIndexOrThrow("name")).toString()

            } while (cursor.moveToNext())
            //close the cursor object
            cursor.close()
        } else {
            stringBuilder.append("Noting to show")
        }


        if(name.equals("stop")){
            Log.e("stop the service","service stop")
            if(player!=null) {
                if(player!!.isPlaying())
                    player!!.stop();
//                    player!!.reset();
                player!!.release();
                player=null;
            }
            val myService = Intent(this, BackgroundSoundService::class.java)
            stopService(myService)
            handler.removeCallbacks(runnable)

            isStop = true
        }
        //set string value in  textView
//        binding.myTextView.setText(stringBuilder);
        Log.e("result from ", "content provider.......$stringBuilder")


    }


}
