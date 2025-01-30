package za.co.topitup.salaahapp

import android.R.attr.country
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.ProgressDialog
import android.content.ComponentName
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import androidx.core.content.PackageManagerCompat.LOG_TAG
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dev.kosrat.muslimdata.models.AsrMethod
import dev.kosrat.muslimdata.models.CalculationMethod
import dev.kosrat.muslimdata.models.HigherLatitudeMethod
import dev.kosrat.muslimdata.models.PrayerAttribute
import dev.kosrat.muslimdata.repository.MuslimRepository
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Scanner
import java.util.TimeZone
import kotlin.random.Random


class MainActivity : AppCompatActivity() {



    private lateinit var pd: ProgressDialog
    private lateinit var radioschool: RadioGroup
    private var fromService: String? = ""
    private var time1: String = ""
    private var time2: String = ""
    private var time3: String = ""
    private var time4: String = ""
    private var time5: String = ""

    private var schoolType:String = ""
    private var alarm_index: Int = -1
    private var alarm_play: Boolean = true
    public var nextPrayerIndex: Int = -1
    private var nextPrayerIndexSelected: Int = -1

    private var selectedReciterId: Int = 0
    private lateinit var txt_check: TextView
    private lateinit var audioManager: AudioManager
    private lateinit var sound_icon: ImageView
    private lateinit var activity_login_admin: ImageView

    private lateinit var img_flag:ImageView

    lateinit var imageSource:String
    private var spinner: Spinner? = null
    private var spinner_country: Spinner? = null
    private lateinit var rl_isha_light: RelativeLayout
    private lateinit var rl_isha_dark: RelativeLayout
    private lateinit var rl_maghrib_light: RelativeLayout
    private lateinit var rl_maghrib_dark: RelativeLayout
    private lateinit var rl_asr_light: RelativeLayout
    private lateinit var rl_asr_dark: RelativeLayout
    private lateinit var rl_dhuhr_light: RelativeLayout
    private lateinit var rl_dhuhr_dark: RelativeLayout
    private lateinit var rl_fjr_dark: RelativeLayout
    private lateinit var rl_fjr_light: RelativeLayout


    private var sharedPreference: SharedPreferences? = null
    public var prefs: SharedPreferences? = null
    private lateinit var alarmIntent_open: Intent
    private lateinit var alarmManager_open: AlarmManager

    //Play audio
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var runnable: Runnable
    private lateinit var runnable_update: Runnable
    private lateinit var runnabletime:Runnable

    private var handler: Handler = Handler()
    private var handler_update: Handler = Handler()

    private var pause: Boolean = false

    var CONTENT_URI = Uri.parse("content://com.demo.user.provider/users")

    private lateinit var timings: String
    lateinit var imageUrl: ArrayList<String>

    // on below line we are creating
    // a variable for our slider view.

    lateinit var fajr_time_text_view: TextView
    lateinit var rl_time1:RelativeLayout
    lateinit var rl_time2:RelativeLayout
    lateinit var rl_time3:RelativeLayout
    lateinit var rl_time4:RelativeLayout
    lateinit var rl_time5:RelativeLayout

    lateinit var txt_dhuhr_text_view: TextView
    lateinit var txt_asr_text_view: TextView
    lateinit var txt_time: TextView

    lateinit var txt_country_name : TextView
    lateinit var txt_maghrib_text_view: TextView
    lateinit var txt_isha_text_view: TextView
    lateinit var play_icon: ImageView
    lateinit var pause_icon: ImageView
    lateinit var seekbar: SeekBar
    lateinit var txt_play_time: TextView
    lateinit var txt_play_time_end: TextView

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var currentLocation: Location? = null
    lateinit var locationManager: LocationManager
    private val permissionId = 2

    lateinit var check_active: SwitchCompat
    lateinit var check_alaram1: CheckBox
    lateinit var check_alaram2: CheckBox
    lateinit var check_alaram3: CheckBox
    lateinit var check_alaram4: CheckBox
    lateinit var check_alaram5: CheckBox
    lateinit var radioshafi: RadioButton
    lateinit var radiohanafi: RadioButton




    public var alarm1_status: Boolean = false
    public var alarm2_status: Boolean = false
    public var alarm3_status: Boolean = false
    public var alarm4_status: Boolean = false
    public var alarm5_status: Boolean = false


    public var alarm1_stop: Boolean = false
    public var alarm2_stop: Boolean = false
    public var alarm3_stop: Boolean = false
    public var alarm4_stop: Boolean = false
    public var alarm5_stop: Boolean = false


    public var alarm1_time: String = ""
    public var alarm2_time: String = ""
    public var alarm3_time: String = ""
    public var alarm4_time: String = ""
    public var alarm5_time: String = ""

    public var fromCheck: Boolean = false


    public var alarm1_play: Boolean = true
    public var alarm2_play: Boolean = true
    public var alarm3_play: Boolean = true
    public var alarm4_play: Boolean = true
    public var alarm5_play: Boolean = true

    public var alarmStatus: String = "true"
    var modelList: ArrayList<SpinnerModel> = ArrayList<SpinnerModel>()

    // on below line we are creating
    // a variable for our slider adapter.
//    lateinit var sliderAdapter: SliderAdapter

    @SuppressLint("Range")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fajr_time_text_view = findViewById(R.id.fajr_time)
        txt_dhuhr_text_view = findViewById(R.id.txt_dhuhr)
        txt_asr_text_view = findViewById(R.id.txt_asr)
        txt_maghrib_text_view = findViewById(R.id.txt_maghrib)
        txt_isha_text_view = findViewById(R.id.txt_isha)

        rl_time1 = findViewById(R.id.rl_time1)
        rl_time2 = findViewById(R.id.rl_time2)
        rl_time3 = findViewById(R.id.rl_time3)
        rl_time4 = findViewById(R.id.rl_time4)
        rl_time5 = findViewById(R.id.rl_time5)

        rl_fjr_light = findViewById(R.id.rl_fjr_light)
        rl_fjr_dark = findViewById(R.id.rl_fjr_dark)
        rl_dhuhr_light = findViewById(R.id.rl_dhuhr_light)
        rl_dhuhr_dark = findViewById(R.id.rl_dhuhr_dark)
        rl_asr_light = findViewById(R.id.rl_asr_light)
        rl_asr_dark = findViewById(R.id.rl_asr_dark)
        rl_maghrib_dark = findViewById(R.id.rl_maghrib_dark)
        rl_maghrib_light = findViewById(R.id.rl_maghrib_light)
        rl_isha_dark = findViewById(R.id.rl_isha_dark)
        rl_isha_light = findViewById(R.id.rl_isha_light)
        seekbar = findViewById(R.id.seekbar)
        sound_icon = findViewById(R.id.sound_icon)
        txt_play_time = findViewById(R.id.txt_play_time)
        txt_play_time_end = findViewById(R.id.txt_play_time_end)
        play_icon = findViewById(R.id.play_icon)
        pause_icon = findViewById(R.id.pause_icon)
        mediaPlayer = MediaPlayer.create(this@MainActivity, R.raw.a1)
        spinner = findViewById<Spinner>(R.id.spinner_reciter)

        spinner_country = findViewById<Spinner>(R.id.spinner_country)
        check_active = findViewById(R.id.check_active)
        txt_check = findViewById(R.id.txt_check)
        check_alaram1 = findViewById(R.id.check_alaram1)
        check_alaram2 = findViewById(R.id.check_alaram2)
        check_alaram3 = findViewById(R.id.check_alaram3)
        check_alaram4 = findViewById(R.id.check_alaram4)
        check_alaram5 = findViewById(R.id.check_alaram5)
        activity_login_admin = findViewById(R.id.activity_login_admin)
        radiohanafi = findViewById(R.id.radiohanafi)
        radioshafi = findViewById(R.id.radioshafi)
        radioschool = findViewById(R.id.radioschool)
        txt_time = findViewById(R.id.txt_time)
        txt_country_name = findViewById(R.id.txt_country_name)
        img_flag = findViewById(R.id.img_flag)
        modelList.add(SpinnerModel("Bangladesh","BD","bangladesh","GMT+06:00"))
        modelList.add(SpinnerModel("South Africa","ZA","south_africa","GMT+02:00"))
        modelList.add(SpinnerModel("Zimbabwe","ZW","zimbabwe","GMT+02:00"))
        modelList.add(SpinnerModel("Tanzania","TZ","tanzania","GMT+03:00"))
        modelList.add(SpinnerModel("India","IN","india","GMT+05:30"))
        modelList.add(SpinnerModel("Egypt","EG","egypt","GMT+02:00"))
        modelList.add(SpinnerModel("Eritrea","ER","eritria","GMT+03:00"))
        modelList.add(SpinnerModel("Somalia","SO","somalia","GMT+03:00"))
        modelList.add(SpinnerModel("Pakistan","PK","pakistan","GMT+05:00"))
        modelList.add(SpinnerModel("Malawi","MW","malawi","GMT+02:00"))
        modelList.add(SpinnerModel("Morocco","MA","morocco","GMT+01:00"))
        modelList.add(SpinnerModel("Ethiopia","ET","ethiopia","GMT+03:00"))

        val customDropDownAdapter = CustomDropDownAdapter(this, modelList)



        val sharedPref = getSharedPreferences("salaah", Context.MODE_PRIVATE)
        val selectedCountry = sharedPref.getInt("countrycode", 0)
        Log.e("selected country code","...$selectedCountry.....")
        spinner_country?.post(Runnable {
            spinner_country?.setSelection(selectedCountry)
        })

      /*  mediaPlayer.setOnCompletionListener(MediaPlayer.OnCompletionListener {

            Log.e("media player","onComplete")
            resetAudio()
            *//*if (handler_update != null) {
                if(runnable_update!= null) {
                    handler_update.removeCallbacks(runnable_update)

                    handler_update.postDelayed(runnable_update, 0)
                }}*//*
        })*/

        mediaPlayer.setOnCompletionListener(OnCompletionListener { mediaPlayer ->
            Log.i("media", "onComplete hit")
            /* mp.stop()
             mp.release()*/
        })
        if (isMyServiceRunning(BackgroundSoundService::class.java)) {
            check_active.isChecked = true
        }
            activity_login_admin.setOnClickListener(object :View.OnClickListener{
            override fun onClick(v: View?) {
                Log.e("topitup click","...onclick.....")
                val intent = Intent()
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.setComponent(
                    ComponentName(
                        "za.co.topitup",
                        "za.co.topitup.activity_login"
                    )
                )
                startActivity(intent)
            }
        })

        radioschool.setOnCheckedChangeListener { radioGroup, optionId ->
            run {
                when (optionId) {
                    R.id.radioshafi -> {
                        with(sharedPref.edit()) {
                            putString("school", "shafi")
                            apply()
                            commit()
                        }
                        pd.show()
                        getDataFromMuslimApi("za", "Johannesburg", false,"shafi")

//                        getDataFromMuslimApi("za", "cape town", false,"shafi")

                    }
                    R.id.radiohanafi -> {
                        with(sharedPref.edit()) {
                            putString("school", "hanafi")
                            apply()
                            commit()
                        }
                        pd.show()
                        getDataFromMuslimApi("za", "Johannesburg", false,"hanafi")

//                        getDataFromMuslimApi("za", "cape town", false,"hanafi")

                    }
                }
            }
        }
      /*  check_active.setOnCheckedChangeListener { _, isChecked ->

            if(isChecked) {
                alarmStatus = "true"

                 *//* val serviceIntent = Intent(this@MainActivity, BackgroundSoundService::class.java)
                  startForegroundService(serviceIntent)*//*
                with(sharedPref.edit()) {
                    putBoolean("alarm1_play", true)
                    apply()
                    commit()
                }
            }else{

                alarmStatus = "false"
                with(sharedPref.edit()) {
                    putBoolean("alarm1_play", false)
                    putInt("alarm_index", nextPrayerIndexSelected)
                    apply()
                    commit()
                }
                if (isMyServiceRunning(BackgroundSoundService::class.java)) {
                    val myService = Intent(this@MainActivity, BackgroundSoundService::class.java)
                    stopService(myService)
                    stopAudio()
                    play_icon.isVisible = true
                    pause_icon.isVisible = false
                    if (mediaPlayer != null) {
                        if (mediaPlayer!!.isPlaying())
                            mediaPlayer!!.stop();
                        mediaPlayer!!.reset();
                        mediaPlayer!!.release();
                        createMediaPlayer(selectedReciterId)
                       *//* if (handler_update != null) {
                            if(runnable_update!= null){
                                handler_update.removeCallbacks(runnable_update)
                                handler_update.postDelayed(runnable_update, 0)
                            }
                        }*//*
                    }
                    Log.e("prayer is playing", ".......playing background.....")
                }else{

                    if (mediaPlayer.isPlaying) {
                        mediaPlayer.seekTo(0)
                        stopAudio()
                    } else {
                        play_icon.isVisible = true
                        pause_icon.isVisible = false
                    }
                }
            }
            Log.e("check alarm", "next time........."+alarmStatus)

            onClickAddDetails(alarmStatus)


            *//* if (isChecked) {
                 txt_check.setText("Active")
                 if (nextPrayerIndexSelected == 0) {
                     alarm1_play = true
                     alarmStatus = "true"
                     setAlarm1(alarm1_time, this@MainActivity)
                 } else if (nextPrayerIndexSelected == 2) {
                     alarm2_play = true
                     alarmStatus = "true"
                     setAlarm6(alarm2_time, this@MainActivity)
                 } else if (nextPrayerIndexSelected == 3) {
                     alarm3_play = true
                     alarmStatus = "true"
                     setAlarm33(alarm3_time, this@MainActivity)
                 } else if (nextPrayerIndexSelected == 4) {
                     alarm4_play = true
                     alarmStatus = "true"
                     setAlarm4(alarm4_time, this@MainActivity)
                 } else if (nextPrayerIndexSelected == 5) {
                     alarm5_play = true
                     alarmStatus = "true"
                     setAlarm5(alarm5_time, this@MainActivity)
                 }
                 with(sharedPref.edit()) {
                     putBoolean("alarm1_play", true)
                     apply()
                     commit()
                 }
             } else {
                 nextPrayerIndexSelected = nextPrayerIndex
                 txt_check.setText("On Mute")
                 with(sharedPref.edit()) {
                     putBoolean("alarm1_play", false)
                     putInt("alarm_index", nextPrayerIndexSelected)
                     apply()
                     commit()
                 }
                 if (nextPrayerIndexSelected == 0) {
                     alarm1_play = false
                     alarmStatus = "false"
                     setAlarm1(alarm1_time, this@MainActivity)
                 } else if (nextPrayerIndexSelected == 2) {
                     alarm2_play = false
                     alarmStatus = "false"
                     setAlarm6(alarm2_time, this@MainActivity)
                 } else if (nextPrayerIndexSelected == 3) {
                     alarm3_play = false
                     alarmStatus = "false"
                     setAlarm33(alarm3_time, this@MainActivity)
                 } else if (nextPrayerIndexSelected == 4) {
                     alarm4_play = false
                     alarmStatus = "false"
                     setAlarm4(alarm4_time, this@MainActivity)
                 } else if (nextPrayerIndexSelected == 5) {
                     alarm5_play = false
                     alarmStatus = "false"
                     setAlarm5(alarm5_time, this@MainActivity)
                 }*//*

//            }

            *//* if (isChecked) {
                 txt_check.setText("Active")
                 val set_volume = 100
                 audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, set_volume, 0)
             } else {
                 txt_check.setText("On Mute")
                 val set_volume = 0
                 audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, set_volume, 0)
             }*//*
//            Toast.makeText(this, isChecked.toString(), Toast.LENGTH_SHORT).show()
        }*/

        alarm1_status = sharedPref.getBoolean("alarm1", true)
        alarm2_status = sharedPref.getBoolean("alarm2", true)
        alarm3_status = sharedPref.getBoolean("alarm3", true)
        alarm4_status = sharedPref.getBoolean("alarm4", true)
        alarm5_status = sharedPref.getBoolean("alarm5", true)

        Log.e("alarm1", ".....1...." + alarm2_status)
        if (alarm1_status) {
            check_alaram1.isChecked = true
        } else {
            check_alaram1.isChecked = false
        }

        if (alarm2_status) {
            check_alaram2.isChecked = true
        } else {
            check_alaram2.isChecked = false

        }

        if (alarm3_status) {
            check_alaram3.isChecked = true
        } else {
            check_alaram3.isChecked = false

        }

        if (alarm4_status) {
            check_alaram4.isChecked = true
        } else {
            check_alaram4.isChecked = false

        }

        if (alarm5_status) {
            check_alaram5.isChecked = true
        } else {
            check_alaram5.isChecked = false

        }


        check_alaram1.setOnCheckedChangeListener { _, isChecked ->


            if (isChecked) {
                with(sharedPref.edit()) {
                    putBoolean("alarm1", true)
                    apply()
                    commit()
                }
                alarm1_stop = false

                alarm1_status = true

            } else {
                with(sharedPref.edit()) {
                    putBoolean("alarm1", false)
                    apply()
                    commit()
                }
                alarm1_stop = true
                alarm1_status = true

            }
            fromCheck = true
            Log.e("alarm1", "status check......" + alarm1_status)
            setAlarm1(alarm1_time, this@MainActivity)


        }

        check_alaram2.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                with(sharedPref.edit()) {
                    putBoolean("alarm2", true)
                    apply()
                    commit()
                }
                alarm2_status = true

                alarm2_stop = false

            } else {
                with(sharedPref.edit()) {
                    putBoolean("alarm2", false)
                    apply()
                    commit()
                }
                alarm2_status = true
                alarm2_stop = true

            }
            fromCheck = true
            setAlarm6(alarm2_time, this@MainActivity)


        }

        check_alaram3.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                with(sharedPref.edit()) {
                    putBoolean("alarm3", true)
                    apply()
                    commit()
                }
                alarm3_status = true

                alarm3_stop = false

            } else {
                with(sharedPref.edit()) {
                    putBoolean("alarm3", false)
                    apply()
                    commit()
                }
                alarm3_status = true

                alarm3_stop = true
            }
            fromCheck = true
            setAlarm33(alarm3_time, this@MainActivity)


        }

        check_alaram4.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                with(sharedPref.edit()) {
                    putBoolean("alarm4", true)
                    apply()
                    commit()
                }
                alarm4_status = true
                alarm4_stop = false
            } else {
                with(sharedPref.edit()) {
                    putBoolean("alarm4", false)
                    apply()
                    commit()
                }
                alarm4_stop = true
                alarm4_status = true

            }
            fromCheck = true

            setAlarm4(alarm4_time, this@MainActivity)


        }

        check_alaram5.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                with(sharedPref.edit()) {
                    putBoolean("alarm5", true)
                    apply()
                    commit()
                }
                alarm5_status = true

                alarm5_stop = true

            } else {
                with(sharedPref.edit()) {
                    putBoolean("alarm5", false)
                    apply()
                    commit()
                }
                alarm5_stop = false
                alarm5_status = true

            }
            fromCheck = true

            setAlarm5(alarm5_time, this@MainActivity)


        }


        play_icon.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (isMyServiceRunning(BackgroundSoundService::class.java)) {
                    val myService = Intent(this@MainActivity, BackgroundSoundService::class.java)
                    stopService(myService)
                    resetAudio()
                    onClickAddDetails("true")
                    play_icon.isVisible = true
                    pause_icon.isVisible = false
                    if (mediaPlayer != null) {
                        if (mediaPlayer!!.isPlaying())
                            mediaPlayer!!.stop();
                        mediaPlayer!!.reset();
                        mediaPlayer!!.release();
                        createMediaPlayer(selectedReciterId)
                    }
                } else {

                    check_active.setChecked(true)

                    playAudio()
                }
                alarmStatus = "true"

                with(sharedPref.edit()) {
                    putBoolean("alarm1_play", true)
                    apply()
                    commit()
                }
            }

        })
        pause_icon.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {

                if (isMyServiceRunning(BackgroundSoundService::class.java)) {
                    val myService = Intent(this@MainActivity, BackgroundSoundService::class.java)
                    stopService(myService)
                    onClickAddDetails("true")

                    resetAudio()
                    play_icon.isVisible = true
                    pause_icon.isVisible = false
                    if (mediaPlayer != null) {
                        if (mediaPlayer!!.isPlaying())
                            mediaPlayer!!.stop();
                        mediaPlayer!!.reset();
                        mediaPlayer!!.release();
                        createMediaPlayer(selectedReciterId)

                    }
                    Log.e("prayer is playing", ".......playing background.....")
                } else {
                    if (mediaPlayer.isPlaying) {
                        resetAudio()

                    } else {
                        play_icon.isVisible = true
                        pause_icon.isVisible = false
                    }

                }

                alarmStatus = "false"
                with(sharedPref.edit()) {
                    putBoolean("alarm1_play", false)
                    putInt("alarm_index", nextPrayerIndexSelected)
                    apply()
                    commit()
                }



            }

        })
//        alarmManager.nextAlarmClock



        alarmManager_open = getSystemService(ALARM_SERVICE) as AlarmManager
        alarmIntent_open = Intent(this@MainActivity, AlarmReciver::class.java)

        alarmIntent_open.putExtra("to_open", "false");

        val pendingIntent_open = PendingIntent.getBroadcast(
            this@MainActivity, 0, alarmIntent_open, PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager_open.cancel(pendingIntent_open)


        openApp()


        imageUrl = ArrayList()

        imageUrl =
            (imageUrl + "https://practice.geeksforgeeks.org/_next/image?url=https%3A%2F%2Fmedia.geeksforgeeks.org%2Fimg-practice%2Fbanner%2Fdsa-self-paced-thumbnail.png&w=1920&q=75") as ArrayList<String>
        imageUrl =
            (imageUrl + "https://practice.geeksforgeeks.org/_next/image?url=https%3A%2F%2Fmedia.geeksforgeeks.org%2Fimg-practice%2Fbanner%2Fdata-science-live-thumbnail.png&w=1920&q=75") as ArrayList<String>
        imageUrl =
            (imageUrl + "https://practice.geeksforgeeks.org/_next/image?url=https%3A%2F%2Fmedia.geeksforgeeks.org%2Fimg-practice%2Fbanner%2Ffull-stack-node-thumbnail.png&w=1920&q=75") as ArrayList<String>


        val spinnerItems = resources.getStringArray(R.array.Languages)
        val spinnerItemscountry = resources.getStringArray(R.array.countries)



        if (spinner_country != null) {
//            val adapterCountry = ArrayAdapter(this, R.layout.simple_spinner_item_, spinnerItemscountry)
//            adapterCountry.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner_country?.adapter = customDropDownAdapter

            spinner_country?.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?, position: Int, id: Long
                ) {
                    val cName: String = modelList.get(position).cName

                    val cCode: String = modelList.get(position).cCode

                    val time:String = modelList.get(position).cTime
                    val image:String = modelList.get(position).CImage
                    setDataBadesCountry(cName,cCode,time,image)
//                    Toast.makeText(this@MainActivity,"spinner item...$position......",Toast.LENGTH_LONG).show()
//                    grtTimes(text)
                    val sharedPref = getSharedPreferences("salaah", Context.MODE_PRIVATE)

                    with(sharedPref.edit()) {
                        putInt("countrycode", position)
                        apply()
                        commit()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }


        if (spinner != null) {
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item, spinnerItems
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner?.adapter = adapter

            spinner?.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?, position: Int, id: Long
                ) {

                    resetAudio()
                    if (position == 0) {
                        var selectedRandom :Int = generateRandom()
                        if(selectedRandom==1) {
                            mediaPlayer = MediaPlayer.create(this@MainActivity, R.raw.a1)

                        }else if(selectedRandom ==2){
                            mediaPlayer = MediaPlayer.create(this@MainActivity, R.raw.a2)

                        }else if(selectedRandom ==3){
                            mediaPlayer = MediaPlayer.create(this@MainActivity, R.raw.a3)

                        }
                        editReciter(0)

                    } else if (position == 1) {

                        mediaPlayer = MediaPlayer.create(this@MainActivity, R.raw.a1)
                        editReciter(1)

                    } else if (position == 2) {
                        mediaPlayer = MediaPlayer.create(this@MainActivity, R.raw.a2)
                        editReciter(2)


                    } else if (position == 3) {
                        mediaPlayer = MediaPlayer.create(this@MainActivity, R.raw.a3)
                        editReciter(3)

                    }

                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }


//        val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
//        val defaultValue = resources.getInteger(R.integer.saved_high_score_default_key)
        val selectedId = sharedPref.getInt(getString(R.string.saved_high_score_key), 0)
//        val selectedId = sharedPreference?.getInt("salaah_prayed_id",0)

        Log.e("selected id","..........$selectedId")
        selectedReciterId = selectedId
        spinner?.setSelection(selectedId!!)
        spinner?.post(Runnable {
            spinner?.setSelection(selectedId)
        })

        // Seek bar change listener
        seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                if (b) {
                    mediaPlayer.seekTo(i * 1000)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        })


    }



    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionId
        )
    }

    fun createMediaPlayer(pos: Int) {


        if (pos == 0) {
            var selectedRandom :Int = generateRandom()
            if(selectedRandom==1) {
                mediaPlayer = MediaPlayer.create(this@MainActivity, R.raw.a1)

            }else if(selectedRandom ==2){
                mediaPlayer = MediaPlayer.create(this@MainActivity, R.raw.a2)

            }else if(selectedRandom ==3){
                mediaPlayer = MediaPlayer.create(this@MainActivity, R.raw.a3)

            }
            editReciter(0)

        } else if (pos == 1) {

            mediaPlayer = MediaPlayer.create(this@MainActivity, R.raw.a1)
            editReciter(1)

        } else if (pos == 2) {
            mediaPlayer = MediaPlayer.create(this@MainActivity, R.raw.a2)
            editReciter(2)


        } else if (pos == 3) {
            mediaPlayer = MediaPlayer.create(this@MainActivity, R.raw.a3)
            editReciter(3)

        }


        /*else if (pos == 3) {
            mediaPlayer = MediaPlayer.create(applicationContext, R.raw.a4)
            editReciter(3)


        } else if (pos == 4) {
            mediaPlayer = MediaPlayer.create(applicationContext, R.raw.a5)
            editReciter(4)


        } else if (pos == 5) {
            mediaPlayer = MediaPlayer.create(applicationContext, R.raw.a6)
            editReciter(5)


        } else if (pos == 6) {
            mediaPlayer = MediaPlayer.create(applicationContext, R.raw.a7)
            editReciter(6)


        }*/
    }

    private fun editReciter(itemPosition: Int) {
        val sharedPref = getSharedPreferences("salaah", Context.MODE_PRIVATE)

//        val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putInt(getString(R.string.saved_high_score_key), itemPosition)
            apply()
        }
    }


    private fun setDataBadesCountry(cName: String, cCode: String, ctime: String, image: String) {
        val handlertime = Handler()

        val id =resources.getIdentifier(image, "drawable", this@MainActivity.packageName)
        Glide.with(this@MainActivity)
            .load(id)
            .into(img_flag)


        var countryName = cName
        txt_country_name.text = "Time in $countryName"

        Log.e("time zone",".......time"+ctime)
            val c = Calendar.getInstance()
            val format = SimpleDateFormat("HH:mm", Locale.getDefault())
            format.timeZone = TimeZone.getTimeZone(ctime)
            var timeZonenew= TimeZone.getTimeZone(ctime)
            var  time =  format.format(c.getTime())

            Log.e("time format",".....in....."+time)

        txt_time.text = time
        runnabletime = Runnable {

            val c = Calendar.getInstance()
            val formater = SimpleDateFormat("HH:mm", Locale.getDefault())
            formater.timeZone = timeZonenew
            val timenew =  formater.format(c.getTime())
            txt_time.text = timenew
            handlertime.removeCallbacks(runnabletime)
            handlertime.postDelayed(runnabletime, 60000)

//            handler.postDelayed(runnabletime, 1000)
        }
       /* handler.postDelayed(object : Runnable {
            override fun run() {
                //now is every 2 minutes
            }
        }, 60000)*/

    }

    private fun grtTimes(countryCode : String){
        var time:String =""
        var countryName = "South Africa"
        var timeZonenew=TimeZone.getTimeZone("GMT+02:00")
        if(countryCode.equals("IN")){
            val c = Calendar.getInstance()
            val format = SimpleDateFormat("HH:mm", Locale.getDefault())
            format.timeZone = TimeZone.getTimeZone("GMT+05:30")
            timeZonenew= TimeZone.getTimeZone("GMT+05:30")
            time =  format.format(c.getTime())
            Glide.with(this@MainActivity)
                .load(R.drawable.india)
                .into(img_flag)
//            imageSource = "https://flagcdn.com/80x60/in.png"
            countryName = "India"
            Log.e("time format",".....in....."+time)

        }else if(countryCode.equals("ZA")){

            val c = Calendar.getInstance()
            val format = SimpleDateFormat("HH:mm", Locale.getDefault())
            format.timeZone = TimeZone.getTimeZone("GMT+02:00")
            timeZonenew= TimeZone.getTimeZone("GMT+02:00")
             time =  format.format(c.getTime())
            Glide.with(this@MainActivity)
                .load(R.drawable.south_africa)
                .into(img_flag)
//            imageSource = "https://flagcdn.com/80x60/za.png"
            countryName = "South Africa"
            Log.e("time format",".....za....."+time)

        } else if(countryCode.equals("SO")){
            val c = Calendar.getInstance()
            val format = SimpleDateFormat("HH:mm", Locale.getDefault())
            format.timeZone = TimeZone.getTimeZone("GMT+03:00")
            timeZonenew= TimeZone.getTimeZone("GMT+03:00")
            time = format.format(c.getTime())
            Glide.with(this@MainActivity)
                .load(R.drawable.somalia)
                .into(img_flag)
//            imageSource = "https://flagcdn.com/80x60/so.png"
            countryName = "Somalia"
            Log.e("time format",".....SO....."+time)

        }else if(countryCode.equals("BD")){
            val c = Calendar.getInstance()
            val format = SimpleDateFormat("HH:mm", Locale.getDefault())
            format.timeZone = TimeZone.getTimeZone("GMT+06:00")
            timeZonenew =  TimeZone.getTimeZone("GMT+06:00")
            time = format.format(c.getTime())
//            imageSource = "https://flagcdn.com/80x60/bd.png"
            Glide.with(this@MainActivity)
                .load(R.drawable.bangladesh)
                .into(img_flag)
            countryName = "Bangladesh"
            Log.e("time format",".....BD....."+time)

        }else if(countryCode.equals("ZW")){
            val c = Calendar.getInstance()
            val format = SimpleDateFormat("HH:mm", Locale.getDefault())
            format.timeZone = TimeZone.getTimeZone("GMT+02:00")
            timeZonenew =TimeZone.getTimeZone("GMT+02:00")
            time = format.format(c.getTime())
//            imageSource = "https://flagcdn.com/80x60/zw.png"
            Glide.with(this@MainActivity)
                .load(R.drawable.zimbabwe)
                .into(img_flag)
            countryName = "Zimbabwe"
            Log.e("time format",".....ZW....."+time)

        }else if(countryCode.equals("PK")){
            val c = Calendar.getInstance()
            val format = SimpleDateFormat("HH:mm", Locale.getDefault())
            format.timeZone = TimeZone.getTimeZone("GMT+05:00")
            timeZonenew =TimeZone.getTimeZone("GMT+05:00")
            time =  format.format(c.getTime())
            Glide.with(this@MainActivity)
                .load(R.drawable.pakistan)
                .into(img_flag)
//            imageSource = "https://flagcdn.com/80x60/pk.png"
            countryName = "Pakistan"
            Log.e("time format",".....PK....."+time)
        }

        txt_time.text = time

        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                val c = Calendar.getInstance()
                val formater = SimpleDateFormat("HH:mm", Locale.getDefault())
                formater.timeZone = timeZonenew
               val timenew =  formater.format(c.getTime())
                txt_time.text = timenew
                handler.postDelayed(this, 60000) //now is every 2 minutes
            }
        }, 60000)
        txt_country_name.text = "Time in $countryName"

        Log.e("time format",".....format....."+time)


    }
    private fun openApp() {

        alarmManager_open = getSystemService(ALARM_SERVICE) as AlarmManager
        var alarmIntent_open = Intent(this@MainActivity, AlarmReciver::class.java)
        alarmIntent_open.putExtra("to_open", "true");
        alarmIntent_open.putExtra("alarm_play", "true");


        var pendingIntent_open =
            PendingIntent.getBroadcast(
                this@MainActivity,
                7,
                alarmIntent_open,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        alarmIntent_open.setData(Uri.parse("custom://" + System.currentTimeMillis()))

//
        val alarmStartTime = Calendar.getInstance()
        val now = Calendar.getInstance()

        alarmStartTime[Calendar.HOUR_OF_DAY] = 6
        alarmStartTime[Calendar.MINUTE] = 0
        alarmStartTime[Calendar.SECOND] = 0
        if (now.after(alarmStartTime)) {
            alarmStartTime.add(Calendar.DATE, 1)
        }

        alarmManager_open.setRepeating(
            AlarmManager.RTC_WAKEUP,
            alarmStartTime.getTimeInMillis(),
            AlarmManager.INTERVAL_DAY,
            pendingIntent_open
        )


    }

    private fun stopAudio() {

        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            pause = true
            play_icon.isVisible = true
            pause_icon.isVisible = false
//            stopBtn.isEnabled = true
//            Toast.makeText(this, "media pause", Toast.LENGTH_SHORT).show()
        }
    }

    private fun resetAudio() {

       /* if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            pause = false
            play_icon.isVisible = true
            pause_icon.isVisible = false

//            stopBtn.isEnabled = true
//            Toast.makeText(this, "media reset ", Toast.LENGTH_SHORT).show()
        }*/
        play_icon.isVisible = true
        pause_icon.isVisible = false

        if (mediaPlayer != null) {
            if (mediaPlayer!!.isPlaying())
                mediaPlayer!!.stop();
            mediaPlayer!!.reset();
            mediaPlayer!!.release();
            createMediaPlayer(selectedReciterId)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDataFromMuslimApi(countryCode: String, cityName: String, showNext: Boolean,asrType :String) {
        lifecycleScope.launch {
            val repository = MuslimRepository(this@MainActivity)
            val locationList = repository.searchLocation("cityName")
            val location = repository.geocoder(countryCode, cityName)

            Log.e("api call","............")
            if (isMyServiceRunning(BackgroundSoundService::class.java)) {
                play_icon.visibility = View.GONE
                pause_icon.visibility = View.VISIBLE
            } else {
                if(mediaPlayer.isPlaying){
                    play_icon.visibility = View.GONE
                    pause_icon.visibility = View.VISIBLE
                }else{
                    play_icon.visibility = View.VISIBLE
                    pause_icon.visibility = View.GONE
                }
            }
            var attribute :PrayerAttribute
            if(asrType.equals("shafi")){

                 attribute = PrayerAttribute(
                    CalculationMethod.MAKKAH,
                    AsrMethod.SHAFII,
                    HigherLatitudeMethod.ANGLE_BASED,
                    intArrayOf(0, 0, 0, 0, 0, 0)
                )
            }
            else{
                attribute = PrayerAttribute(
                    CalculationMethod.MAKKAH,
                    AsrMethod.HANAFI,
                    HigherLatitudeMethod.ANGLE_BASED,
                    intArrayOf(0, 0, 0, 0, 0, 0)
                )
            }

            val prayerTime = repository.getPrayerTimes(
                location!!,
                Date(),
                attribute
            )

            Log.e("api call","......prayer time......"+prayerTime[0])


            time1 = prayerTime[0].toString()
            time2 = prayerTime[2].toString()
            time3 = prayerTime[3].toString()
            time4 = prayerTime[4].toString()
            time5 = prayerTime[5].toString()

            cancelAlarmS(this@MainActivity)
            alarm1_time = time1
            alarm2_time = time2
            alarm3_time = time3
            alarm4_time = time4
            alarm5_time = time5
            Log.e("api call","......prayer time.input....."+time1)

//            fajr_time_text_view.text = "time1"

            fajr_time_text_view.setText(changeDateFormat(alarm1_time))
            txt_dhuhr_text_view.setText(changeDateFormat(alarm2_time))
            txt_asr_text_view.setText(changeDateFormat(alarm3_time))
            txt_maghrib_text_view.setText(changeDateFormat(alarm4_time))
            txt_isha_text_view.setText(changeDateFormat(alarm5_time))

            nextPrayerIndex = getNextPrayer()


            fromCheck = false

            setAlarm1(alarm1_time, this@MainActivity)
//            setAlarm2(alarm2_time, this@MainActivity)
            setAlarm6(alarm2_time, this@MainActivity)
            setAlarm33(alarm3_time, this@MainActivity)
            setAlarm4(alarm4_time, this@MainActivity)
            setAlarm5(alarm5_time, this@MainActivity)

            if (showNext) {
                updateTimes(nextPrayerIndex)

                runnable_update = Runnable {
                    if (isMyServiceRunning(BackgroundSoundService::class.java)) {
                        play_icon.visibility = View.GONE
                        pause_icon.visibility = View.VISIBLE
                    } else {
                        play_icon.visibility = View.VISIBLE
                        pause_icon.visibility = View.GONE
                    }
                    getIntent().removeExtra("fromNotification");

                    Log.e("next prayer time","........"+nextPrayerIndex)
                    updateTimes(nextPrayerIndex)
                }
                if(handler_update!=null) {
                    if (runnable_update != null) {

                        handler_update.postDelayed(runnable_update, 30 * 60000)
                    }
                }
            } else {
                updateTimes(nextPrayerIndex)
            }
            val fromVas = intent.getStringExtra("muteFromVas")
            val sharedPref = getSharedPreferences("salaah", Context.MODE_PRIVATE)

            pd.dismiss()

            if (fromVas.equals("true")) {
                check_active.setChecked(true)
                onClickAddDetails("true")


            } else if (fromVas.equals("false")) {
                check_active.setChecked(false)
                onClickAddDetails("false")

            } else {

                if (alarm_play) {

                    check_active.setChecked(true)
                    txt_check.setText("Active")
                    onClickAddDetails("true")
                } else {

                   /* if (alarm_index == nextPrayerIndex) {
                        check_active.setChecked(false)
                        onClickAddDetails("false")

                        txt_check.setText("On Mute")
                    } else {
                        check_active.setChecked(true)
                        onClickAddDetails("true")

                        txt_check.setText("Active")
                    }*/
                }
            }
//            setAlarm(this@MainActivity);
        }
    }

    private fun getNextPrayer(): Int {
        val firstTime = changeDateFormat(time1)
        val secondTime = changeDateFormat(time2)
        val thirdTime = changeDateFormat(time3)
        val fourthTime = changeDateFormat(time4)
        val fifthTime = changeDateFormat(time5)

        val date = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val currentTime = dateFormat.format(date)
        var prayerIndex = 5

        /*if (firstTime.equals(currentTime)) {
            prayerIndex = 0
            return 1
        } else if (secondTime.equals(currentTime)) {
            prayerIndex = 3

            return 3
        } else if (thirdTime.equals(currentTime)) {
            prayerIndex = 4

            return 4
        } else if (fourthTime.equals(currentTime)) {
            prayerIndex = 5

            return 5
        } else if (fifthTime.equals(currentTime)) {
            prayerIndex = 5

            return 5
        } else {*/


            if (calculateDuration(firstTime, currentTime)) {
                prayerIndex = 0
                return 0
            } else if (calculateDuration(secondTime, currentTime)) {
                prayerIndex = 2

                return 2
            } else if (calculateDuration(thirdTime, currentTime)) {
                prayerIndex = 3

                return 3
            } else if (calculateDuration(fourthTime, currentTime)) {
                prayerIndex = 4
                return 4
            } else if (calculateDuration(fifthTime, currentTime)) {

                prayerIndex = 5
                return 5

            }else{
                prayerIndex = 0
                return 0
            }
//        }
        return prayerIndex
    }

    private fun updateTimes(nextPrayerTimeIndex: Int) {
        Log.e("next prayer index", ".........index...." + nextPrayerTimeIndex)
        if (nextPrayerTimeIndex == 0) {
            fajr_time_text_view.setTextColor(resources.getColor(R.color.light_green1))
            txt_dhuhr_text_view.setTextColor(resources.getColor(R.color.dark_green))
            txt_asr_text_view.setTextColor(resources.getColor(R.color.dark_green))
            txt_maghrib_text_view.setTextColor(resources.getColor(R.color.dark_green))
            txt_isha_text_view.setTextColor(resources.getColor(R.color.dark_green))

            rl_time1.setBackgroundColor(resources.getColor(R.color.dark_green))
            rl_time2.setBackgroundColor(resources.getColor(R.color.light_green1))
            rl_time3.setBackgroundColor(resources.getColor(R.color.light_green1))
            rl_time4.setBackgroundColor(resources.getColor(R.color.light_green1))
            rl_time5.setBackgroundColor(resources.getColor(R.color.light_green1))

            rl_fjr_light.visibility= View.VISIBLE
            rl_dhuhr_light.visibility = View.GONE
            rl_asr_light.visibility = View.GONE
            rl_maghrib_light.visibility = View.GONE
            rl_isha_light.visibility = View.GONE

            rl_fjr_dark.visibility=View.GONE
            rl_dhuhr_dark.visibility=View.VISIBLE
            rl_asr_dark.visibility=View.VISIBLE
            rl_maghrib_dark.visibility=View.VISIBLE
            rl_isha_dark.visibility=View.VISIBLE

        } else if (nextPrayerTimeIndex == 2) {
//                txt_dhuhr_text_view.setBackgroundColor(resources.getColor(R.color.green))
            fajr_time_text_view.setTextColor(resources.getColor(R.color.dark_green))
            txt_dhuhr_text_view.setTextColor(resources.getColor(R.color.light_green1))
            txt_asr_text_view.setTextColor(resources.getColor(R.color.dark_green))
            txt_maghrib_text_view.setTextColor(resources.getColor(R.color.dark_green))
            txt_isha_text_view.setTextColor(resources.getColor(R.color.dark_green))

            rl_time1.setBackgroundColor(resources.getColor(R.color.light_green1))
            rl_time2.setBackgroundColor(resources.getColor(R.color.dark_green))
            rl_time3.setBackgroundColor(resources.getColor(R.color.light_green1))
            rl_time4.setBackgroundColor(resources.getColor(R.color.light_green1))
            rl_time5.setBackgroundColor(resources.getColor(R.color.light_green1))

            rl_fjr_light.visibility= View.GONE
            rl_dhuhr_light.visibility = View.VISIBLE
            rl_asr_light.visibility = View.GONE
            rl_maghrib_light.visibility = View.GONE
            rl_isha_light.visibility = View.GONE

            rl_fjr_dark.visibility=View.VISIBLE
            rl_dhuhr_dark.visibility=View.GONE
            rl_asr_dark.visibility=View.VISIBLE
            rl_maghrib_dark.visibility=View.VISIBLE
            rl_isha_dark.visibility=View.VISIBLE

        } else if (nextPrayerTimeIndex == 3) {
//                txt_asr_text_view.setBackgroundColor(resources.getColor(R.color.green))
            fajr_time_text_view.setTextColor(resources.getColor(R.color.dark_green))
            txt_dhuhr_text_view.setTextColor(resources.getColor(R.color.dark_green))
            txt_asr_text_view.setTextColor(resources.getColor(R.color.light_green1))
            txt_maghrib_text_view.setTextColor(resources.getColor(R.color.dark_green))
            txt_isha_text_view.setTextColor(resources.getColor(R.color.dark_green))

            rl_time1.setBackgroundColor(resources.getColor(R.color.light_green1))
            rl_time2.setBackgroundColor(resources.getColor(R.color.light_green1))
            rl_time3.setBackgroundColor(resources.getColor(R.color.dark_green))
            rl_time4.setBackgroundColor(resources.getColor(R.color.light_green1))
            rl_time5.setBackgroundColor(resources.getColor(R.color.light_green1))
            rl_fjr_light.visibility= View.GONE
            rl_dhuhr_light.visibility = View.GONE
            rl_asr_light.visibility = View.VISIBLE
            rl_maghrib_light.visibility = View.GONE
            rl_isha_light.visibility = View.GONE

            rl_fjr_dark.visibility=View.VISIBLE
            rl_dhuhr_dark.visibility=View.VISIBLE
            rl_asr_dark.visibility=View.GONE
            rl_maghrib_dark.visibility=View.VISIBLE
            rl_isha_dark.visibility=View.VISIBLE

        } else if (nextPrayerTimeIndex == 4) {
            rl_fjr_light.visibility= View.GONE
            rl_dhuhr_light.visibility = View.GONE
            rl_asr_light.visibility = View.GONE
            rl_maghrib_light.visibility = View.VISIBLE
            rl_isha_light.visibility = View.GONE

            rl_fjr_dark.visibility=View.VISIBLE
            rl_dhuhr_dark.visibility=View.VISIBLE
            rl_asr_dark.visibility=View.VISIBLE
            rl_maghrib_dark.visibility=View.GONE
            rl_isha_dark.visibility=View.VISIBLE

            fajr_time_text_view.setTextColor(resources.getColor(R.color.dark_green))
            txt_dhuhr_text_view.setTextColor(resources.getColor(R.color.dark_green))
            txt_asr_text_view.setTextColor(resources.getColor(R.color.dark_green))
            txt_maghrib_text_view.setTextColor(resources.getColor(R.color.light_green1))
            txt_isha_text_view.setTextColor(resources.getColor(R.color.dark_green))

            rl_time1.setBackgroundColor(resources.getColor(R.color.light_green1))
            rl_time2.setBackgroundColor(resources.getColor(R.color.light_green1))
            rl_time3.setBackgroundColor(resources.getColor(R.color.light_green1))
            rl_time4.setBackgroundColor(resources.getColor(R.color.dark_green))
            rl_time5.setBackgroundColor(resources.getColor(R.color.light_green1))


        } else if (nextPrayerTimeIndex == 5) {
            rl_fjr_light.visibility= View.GONE
            rl_dhuhr_light.visibility = View.GONE
            rl_asr_light.visibility = View.GONE
            rl_maghrib_light.visibility = View.GONE
            rl_isha_light.visibility = View.VISIBLE

            rl_fjr_dark.visibility=View.VISIBLE
            rl_dhuhr_dark.visibility=View.VISIBLE
            rl_asr_dark.visibility=View.VISIBLE
            rl_maghrib_dark.visibility=View.VISIBLE
            rl_isha_dark.visibility=View.GONE
            fajr_time_text_view.setTextColor(resources.getColor(R.color.light_green1))
            txt_dhuhr_text_view.setTextColor(resources.getColor(R.color.light_green1))
            txt_asr_text_view.setTextColor(resources.getColor(R.color.light_green1))
            txt_maghrib_text_view.setTextColor(resources.getColor(R.color.light_green1))
            txt_isha_text_view.setTextColor(resources.getColor(R.color.dark_green))

            rl_time1.setBackgroundColor(resources.getColor(R.color.light_green1))
            rl_time2.setBackgroundColor(resources.getColor(R.color.light_green1))
            rl_time3.setBackgroundColor(resources.getColor(R.color.light_green1))
            rl_time4.setBackgroundColor(resources.getColor(R.color.light_green1))
            rl_time5.setBackgroundColor(resources.getColor(R.color.dark_green))

        }

    }


    private fun cancelAlarmS(context: MainActivity) {
        val alarmManager1 = getSystemService(ALARM_SERVICE) as AlarmManager

        val alarmIntent1 = Intent(this@MainActivity, AlarmReciver::class.java)

        val pendingIntent1 =
            PendingIntent.getBroadcast(
                context,
                1,
                alarmIntent1,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        alarmManager1.cancel(pendingIntent1) //important
        pendingIntent1.cancel()


        val alarmManager2 = getSystemService(ALARM_SERVICE) as AlarmManager
        val alarmIntent2 = Intent(this@MainActivity, AlarmReciver::class.java)


        val pendingIntent2 =
            PendingIntent.getBroadcast(
                context,
                2,
                alarmIntent2,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        alarmManager2.cancel(pendingIntent2) //important
        pendingIntent2.cancel()


        val alarmManager33 = getSystemService(ALARM_SERVICE) as AlarmManager

        val alarmIntent33 = Intent(this@MainActivity, AlarmReciver::class.java)

        val pendingIntent33 =
            PendingIntent.getBroadcast(
                context,
                3,
                alarmIntent33,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        alarmManager33.cancel(pendingIntent33) //important
        pendingIntent33.cancel()


        val alarmManager4 = getSystemService(ALARM_SERVICE) as AlarmManager

        val alarmIntent4 = Intent(this@MainActivity, AlarmReciver::class.java)
        val pendingIntent4 =
            PendingIntent.getBroadcast(
                context,
                4,
                alarmIntent4,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        alarmManager4.cancel(pendingIntent4) //important
        pendingIntent4.cancel()


        val alarmManager5 = getSystemService(ALARM_SERVICE) as AlarmManager

        val alarmIntent5 = Intent(this@MainActivity, AlarmReciver::class.java)

        val pendingIntent5 =
            PendingIntent.getBroadcast(
                context,
                5,
                alarmIntent5,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

        alarmManager5.cancel(pendingIntent5) //important

        pendingIntent5.cancel()

    }


    private fun changeDateFormat(prayer1: String): String {

        val simpleDateFormat =
            SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy", Locale.getDefault())
        val dateresponse = SimpleDateFormat(
            " hh:mm a",
            Locale.getDefault()
        ).format(simpleDateFormat.parse(prayer1)!!)
        return dateresponse
    }


    public fun playAudio() {
        if (isMyServiceRunning(BackgroundSoundService::class.java)) {

            Log.e("background running", ".....play....");
        } else {
            if (pause) {
                mediaPlayer.seekTo(mediaPlayer.currentPosition)
                mediaPlayer.start()
                pause = false
//            Toast.makeText(this, "media playing", Toast.LENGTH_SHORT).show()
            } else {
                /*if (mediaPlayer != null) {
                    mediaPlayer.reset()
    //                mediaPlayer!!.reset();
                }
    */
                mediaPlayer.start()
//            Toast.makeText(this, "media playing", Toast.LENGTH_SHORT).show()

            }

            initializeSeekBar()

        }
        play_icon.isVisible = false
        pause_icon.isVisible = true
//        stopBtn.isEnabled = true


    }

    private fun initializeSeekBar() {
        try {
            if(mediaPlayer.isPlaying) {
                seekbar.max = mediaPlayer.seconds

                runnable = Runnable {
                    seekbar.progress = mediaPlayer.currentSeconds


                    txt_play_time.text =
                        "${getDurationString(mediaPlayer.currentSeconds)} "

                    txt_play_time_end.text ="${getDurationString(mediaPlayer.seconds)}"

                    handler.postDelayed(runnable, 1000)
                }
                handler.postDelayed(runnable, 1000)
            }
        } catch (e: Exception) {

        }

    }

    val MediaPlayer.seconds: Int
        get() {
            return this.duration / 1000
        }

    // Creating an extension property to get media player current position in seconds
    val MediaPlayer.currentSeconds: Int
        get() {
            return this.currentPosition / 1000
        }

    private fun getDurationString(seconds: Int): String {
        var seconds = seconds
        val hours = seconds / 3600
        val minutes = seconds % 3600 / 60
        seconds = seconds % 60
        return twoDigitString(minutes)+ ":" +twoDigitString(seconds)
    }

    private fun twoDigitString(number: Int): String {
        if (number == 0) {
            return "00"
        }
        return if (number / 10 == 0) {
            "0$number"
        } else number.toString()
    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        Log.e("service name","....Service..running...."+serviceClass.getName())

        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.getName() == service.service.className) {
                return true
            }
        }
        return false
    }

    @SuppressLint("Range")
    override fun onResume() {
        super.onResume()
         pd = ProgressDialog(this@MainActivity)

        pd.setMessage("Loading...");
//pd.setProgress(R.color.yellow);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        val cursor = contentResolver.query(
            Uri.parse("content://com.demo.user.provider/users"),
            null,
            null,
            null,
            null
        )


        if(cursor!=null) {
            if (cursor!!.moveToFirst()) {
                val strBuild = StringBuilder()
                while (!cursor!!.isAfterLast) {
                    strBuild.append(
                        """
                
                ${cursor!!.getString(cursor!!.getColumnIndex("id"))}-
                """.trimIndent() + cursor!!.getString(
                            cursor!!.getColumnIndex("name")
                        )
                    )
                    cursor!!.moveToNext()
                }
                Log.e("from service ", "...........from....content if....." + strBuild)

            } else {
                Log.e("from service ", "...........from....content else.....")
            }
        }



        if (isMyServiceRunning(BackgroundSoundService::class.java)) {
            play_icon.visibility = View.GONE
            pause_icon.visibility = View.VISIBLE
        }else if(mediaPlayer.isPlaying){
            play_icon.visibility = View.GONE
            pause_icon.visibility = View.VISIBLE
        }

        val sharedPref = getSharedPreferences("salaah", Context.MODE_PRIVATE)

        alarm_play = sharedPref.getBoolean("alarm1_play", true)
        alarm_index = sharedPref.getInt("alarm_index", -1)
        schoolType = sharedPref.getString("school", "")!!

        Log.e("selected","school type.."+alarm_play)
        if(schoolType.equals("shafi")){
           radioschool.check(R.id.radioshafi)
        }else{
            radioschool.check(R.id.radiohanafi)

        }

        fromService = intent.getStringExtra("fromNotification")
        Log.e("from service ", "...........from....else....." + fromService)

        if (fromService.equals("true")) {
            pd.show()
//            getDataFromMuslimApi("za", "cape town", true,schoolType)

            getDataFromMuslimApi("za", "Johannesburg", true,schoolType)

        } else {
            pd.show()

            if (isMyServiceRunning(BackgroundSoundService::class.java)) {
//                getDataFromMuslimApi("za", "cape town", true,schoolType)

                getDataFromMuslimApi("za", "Johannesburg", true,schoolType)

            } else {

//                getDataFromMuslimApi("za", "cape town", false,schoolType)
                getDataFromMuslimApi("za", "Johannesburg", false,schoolType)


            }


        }


    }


    fun setAlarm1(time1: String, context: Context?) {
        val sdf = SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy", Locale.getDefault())
        val cal1 = Calendar.getInstance()

        try {
            cal1.time = sdf.parse(time1)


        } catch (e: ParseException) {
            e.printStackTrace()
        }


//
        val now1 = Calendar.getInstance()
        val mdformat1 = SimpleDateFormat("HH:mm:ss")

        var hour1: Int = cal1.get(Calendar.HOUR_OF_DAY)
        var minute1: Int = cal1.get(Calendar.MINUTE)
        var second1: Int = cal1.get(Calendar.SECOND)
        Log.e("alarm 1", "$minute1......hour..1...." + hour1)
        val alarmStartTime1 = Calendar.getInstance()

        alarmStartTime1[Calendar.HOUR_OF_DAY] = hour1
        alarmStartTime1[Calendar.MINUTE] = minute1
        alarmStartTime1[Calendar.SECOND] = 0
        if (now1.after(alarmStartTime1)) {
            Log.d("Hey", "Added a day")
            alarmStartTime1.add(Calendar.DATE, 1)
        }

        val alarmManager1 = getSystemService(ALARM_SERVICE) as AlarmManager

        val alarmIntent1 = Intent(this@MainActivity, AlarmReciver::class.java)
        alarmIntent1.putExtra("to_open", "false");
        alarmIntent1.putExtra("alarm_play", alarm1_play);
        alarmIntent1.putExtra("alarm_stop", alarm1_stop);

        if (alarm1_status) {

            alarmIntent1.putExtra("alarm_set", "true");

            val pendingIntent1 =
                PendingIntent.getBroadcast(
                    context,
                    1,
                    alarmIntent1,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            alarmIntent1.setData(Uri.parse("custom://" + System.currentTimeMillis()))
            alarmManager1.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                alarmStartTime1.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent1
            )
            Log.e("alarm1 ", "status.. alarm set..1....." + alarm1_status)

        } else {
            Log.e("alarm1 ", "status....1.alarm cancel...." + alarm1_status)

            alarmIntent1.putExtra("alarm_set", "false");

            val pendingIntent1 =
                PendingIntent.getBroadcast(
                    context,
                    1,
                    alarmIntent1,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )


            alarmManager1.cancel(pendingIntent1) //important

            pendingIntent1.cancel()
        }


    }


    private fun setAlarm33(alarm3Time: String, context: MainActivity) {
        val sdf = SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy", Locale.getDefault())

        val cal33 = Calendar.getInstance()

        try {
            cal33.time = sdf.parse(alarm3Time)
            Log.i("TAG", "${cal33.get(Calendar.HOUR_OF_DAY)}time = " + cal33.time)

        } catch (e: ParseException) {
            e.printStackTrace()
        }


        val alarmStartTime33 = Calendar.getInstance()
        val now33 = Calendar.getInstance()
        var hour33: Int = cal33.get(Calendar.HOUR_OF_DAY)
        var minute33: Int = cal33.get(Calendar.MINUTE)
        var second33: Int = cal33.get(Calendar.SECOND)
        Log.e("alarm 2", "$minute33......hour...3..." + hour33)


        alarmStartTime33[Calendar.HOUR_OF_DAY] = hour33
        alarmStartTime33[Calendar.MINUTE] = minute33
        alarmStartTime33[Calendar.SECOND] = 0
        if (now33.after(alarmStartTime33)) {
            Log.d("Hey", "Added a day")
            alarmStartTime33.add(Calendar.DATE, 1)
        }
        val alarmManager33 = getSystemService(ALARM_SERVICE) as AlarmManager

        val alarmIntent33 = Intent(this@MainActivity, AlarmReciver::class.java)
        alarmIntent33.putExtra("to_open", "false");
        alarmIntent33.putExtra("alarm_play", alarm3_play);
        alarmIntent33.putExtra("alarm_stop", alarm3_stop);

        if (alarm3_status) {

            alarmIntent33.putExtra("alarm_set", "true");

            val pendingIntent33 =
                PendingIntent.getBroadcast(
                    context,
                    2,
                    alarmIntent33,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            alarmIntent33.setData(Uri.parse("custom://" + System.currentTimeMillis()))
            alarmManager33.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                alarmStartTime33.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent33
            )
            Log.e("alarm2 ", "status....2..alarm set..." + alarm3_status)

        } else {

            alarmIntent33.putExtra("alarm_set", "false");


            val pendingIntent33 =
                PendingIntent.getBroadcast(
                    context,
                    2,
                    alarmIntent33,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )


            alarmManager33.cancel(pendingIntent33) //important
//
            pendingIntent33.cancel()
            Log.e("alarm2 ", "status....2..alarm cancel..." + alarm3_status)

        }

    }

    fun setAlarm4(time4: String, context: Context?) {
        val sdf = SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy", Locale.getDefault())

        val cal4 = Calendar.getInstance()

        try {
            cal4.time = sdf.parse(time4)

        } catch (e: ParseException) {
            e.printStackTrace()
        }


        val alarmStartTime4 = Calendar.getInstance()
        val now4 = Calendar.getInstance()
        val mdformat3 = SimpleDateFormat("HH:mm:ss")

        var hour4: Int = cal4.get(Calendar.HOUR_OF_DAY)
        var minute4: Int = cal4.get(Calendar.MINUTE)
        var second4: Int = cal4.get(Calendar.SECOND)
        Log.e("alarm 4", "$minute4......hour...4..." + hour4)

        alarmStartTime4[Calendar.HOUR_OF_DAY] = hour4
        alarmStartTime4[Calendar.MINUTE] = minute4
        alarmStartTime4[Calendar.SECOND] = 0
        if (now4.after(alarmStartTime4)) {
            alarmStartTime4.add(Calendar.DATE, 1)
        }

        val alarmManager4 = getSystemService(ALARM_SERVICE) as AlarmManager

        val alarmIntent4 = Intent(this@MainActivity, AlarmReciver::class.java)
        alarmIntent4.putExtra("to_open", "false");
        alarmIntent4.putExtra("alarm_play", alarm4_play);
        alarmIntent4.putExtra("alarm_stop", alarm4_stop);

        if (alarm4_status) {


            alarmIntent4.putExtra("alarm_set", "true");


            val pendingIntent4 =
                PendingIntent.getBroadcast(
                    context,
                    4,
                    alarmIntent4,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            alarmIntent4.setData(Uri.parse("custom://" + System.currentTimeMillis()))
            alarmManager4.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                alarmStartTime4.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent4
            )
            Log.e("alarm4 ", "status....4.alarm set...." + alarm4_status)

        } else {

            alarmIntent4.putExtra("alarm_set", "false");


            val pendingIntent4 =
                PendingIntent.getBroadcast(
                    context,
                    4,
                    alarmIntent4,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )

            alarmManager4.cancel(pendingIntent4) //important

            pendingIntent4.cancel()
            Log.e("alarm4 ", "status....4..alarm cancel..." + alarm4_status)

        }


    }

    fun setAlarm5(time5: String, context: Context?) {
        val sdf = SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy", Locale.getDefault())

        val cal5 = Calendar.getInstance()

        try {
            cal5.time = sdf.parse(time5)
            Log.i("TAG", "${cal5.get(Calendar.HOUR_OF_DAY)}time = " + cal5.time)

        } catch (e: ParseException) {
            e.printStackTrace()
        }


        val alarmStartTime5 = Calendar.getInstance()
        val now5 = Calendar.getInstance()
        val mdformat4 = SimpleDateFormat("HH:mm:ss")

        var hour5: Int = cal5.get(Calendar.HOUR_OF_DAY)
        var minute5: Int = cal5.get(Calendar.MINUTE)
        var second5: Int = cal5.get(Calendar.SECOND)
        Log.e("alarm 5", "$minute5......hour...5..." + hour5)

        alarmStartTime5[Calendar.HOUR_OF_DAY] = hour5
        alarmStartTime5[Calendar.MINUTE] = minute5
        alarmStartTime5[Calendar.SECOND] = 0
        if (now5.after(alarmStartTime5)) {
            alarmStartTime5.add(Calendar.DATE, 1)
        }

        val alarmManager5 = getSystemService(ALARM_SERVICE) as AlarmManager

        val alarmIntent5 = Intent(this@MainActivity, AlarmReciver::class.java)
        alarmIntent5.putExtra("to_open", "false");
        alarmIntent5.putExtra("alarm_play", alarm5_play);
        alarmIntent5.putExtra("alarm_stop", alarm5_stop);

        if (alarm5_status) {

            alarmIntent5.putExtra("alarm_set", "true");

            val pendingIntent5 =
                PendingIntent.getBroadcast(
                    context,
                    5,
                    alarmIntent5,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            alarmIntent5.setData(Uri.parse("custom://" + System.currentTimeMillis()))

            alarmManager5.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                alarmStartTime5.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent5
            )
            Log.e("alarm5 ", "status....5..alarm set ..." + alarm5_status)

        } else {

            alarmIntent5.putExtra("alarm_set", "false");

            val pendingIntent5 =
                PendingIntent.getBroadcast(
                    context,
                    5,
                    alarmIntent5,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )

            alarmManager5.cancel(pendingIntent5) //important

            pendingIntent5.cancel()
//            alarmManager.cancel(pendingIntent4)

            Log.e("alarm5 ", "status....5..alarm cancel..." + alarm5_status)

        }

    }


    private fun setAlarm6(time6: String, context: Context) {
        val sdf = SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy", Locale.getDefault())

        val cal6 = Calendar.getInstance()

        try {
            cal6.time = sdf.parse(time6)
            Log.i("TAG", "${cal6.get(Calendar.HOUR_OF_DAY)}time = " + cal6.time)

        } catch (e: ParseException) {
            e.printStackTrace()
        }


        val alarmStartTime6 = Calendar.getInstance()
        val now6 = Calendar.getInstance()
        var hour6: Int = cal6.get(Calendar.HOUR_OF_DAY)
        var minute6: Int = cal6.get(Calendar.MINUTE)
        var second6: Int = cal6.get(Calendar.SECOND)
        Log.e("alarm 2", "$minute6......hour...2..." + hour6)

        alarmStartTime6[Calendar.HOUR_OF_DAY] = hour6
        alarmStartTime6[Calendar.MINUTE] = minute6
        alarmStartTime6[Calendar.SECOND] = 0
        if (now6.after(alarmStartTime6)) {
            alarmStartTime6.add(Calendar.DATE, 1)
        }

        val alarmManager6 = getSystemService(ALARM_SERVICE) as AlarmManager

        val alarmIntent6 = Intent(this@MainActivity, AlarmReciver::class.java)
        alarmIntent6.putExtra("to_open", "false");
        alarmIntent6.putExtra("alarm_play", alarm2_play);
        alarmIntent6.putExtra("alarm_stop", alarm2_stop);

        if (alarm2_status) {

            alarmIntent6.putExtra("alarm_set", "true");

            val pendingIntent6 =
                PendingIntent.getBroadcast(
                    context,
                    6,
                    alarmIntent6,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            alarmIntent6.setData(Uri.parse("custom://" + System.currentTimeMillis()))

            alarmManager6.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                alarmStartTime6.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent6
            )
            Log.e("alarm5 ", "status....2..alarm set ..." + alarm2_time)

        } else {

            alarmIntent6.putExtra("alarm_set", "false");

            val pendingIntent6 =
                PendingIntent.getBroadcast(
                    context,
                    6,
                    alarmIntent6,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )

            alarmManager6.cancel(pendingIntent6) //important

            pendingIntent6.cancel()
//            alarmManager.cancel(pendingIntent4)

            Log.e("alarm5 ", "status....5..alarm cancel..." + alarm5_status)

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("on destroy","on destroy............")
        if (mediaPlayer != null) {
            Log.e("on destroy","on destroy......stop......")
            if (mediaPlayer!!.isPlaying())
                mediaPlayer!!.stop();
            mediaPlayer!!.reset();
            mediaPlayer!!.release();
            if(handler!=null){
                if(this::runnable.isInitialized){
                    handler.removeCallbacks(runnable)

                }

            }
        }
    }

    fun generateRandom():Int{
        val randomNum = Random.nextInt(1, 3 + 1)

        return randomNum
    }
    private fun calculateDuration(date1cal: String, date2cal: String): Boolean {
        var status: Boolean = false;

        try {
            val format = SimpleDateFormat("hh:mm aa")
            val date1 = format.parse(date1cal)
            val date2 = format.parse(date2cal)

            val mills = date1.time - date2.time
            Log.v("Data1", "" + date1.time)
            Log.v("Data2", "" + date2.time)
            val hours = (mills / (1000 * 60 * 60)).toInt()
            val mins = (mills / (1000 * 60)).toInt() % 60
            val diff = "$hours:$mins" // updated value every1 second
            if (hours >= 0) {
                if (mins >= 0) {
                    Log.i("======= Hours", " :mins: $diff")
                    if (hours == 0 && mins == 0) {

                        status = true

                    } else {
                        status = true

                    }
                } else {
                    Log.e("test.....","Hours . else "+mins)

                    if(hours<=0 && mins > -30){
                        Log.e("test.....","Hours . else if")
                        status = true

                    }else{
                         status = false
                     }
                }
            } else {
                status = false
            }
            Log.i("======= Hours", "hours:$hours mins $mins status  $status:: $diff")
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return status
    }
    fun onClickAddDetails(insertText:String) {


        try{
            // class to add values in the database
            val values = ContentValues()

            // fetching text from user
            values.put(MyProvider.name, insertText)

            val cursor = contentResolver.query(MyProvider.CONTENT_URI, null, null, null, null)


            if (cursor!!.moveToFirst()) {
                Log.e("update", "........")
                contentResolver.update(MyProvider.CONTENT_URI, values, null, null)
            } else {
                Log.e("insert", "........")
                contentResolver.insert(MyProvider.CONTENT_URI, values)
            }

        }catch (e:Exception){

        }

    }


}




