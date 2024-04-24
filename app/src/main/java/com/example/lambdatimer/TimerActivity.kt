package com.example.lambdatimer

import android.animation.ValueAnimator
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.*
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.example.lambdatimer.databinding.ActivityTimerBinding
import java.util.concurrent.TimeUnit

class TimerActivity : AppCompatActivity() {
    private lateinit var b: ActivityTimerBinding
    private lateinit var timer: CountDownTimer
    lateinit var get: SharedPreferences
    lateinit var set: SharedPreferences.Editor
    lateinit var nm: NotificationManager
    lateinit var notif: NotificationCompat.Builder
    private lateinit var finNotif: NotificationCompat.Builder
    private lateinit var toast: Toast
    private var pause = false
    var msWork = 0L
    var msBreak = 0L
    var msBb = 0L
    var thisMs = 0L
    var afterBreak = 0
    var thisAfterBreak = 0
    var run = Run.WORK
    var a = 0

    override fun onDestroy() {
        timer.cancel()
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        b.apply {
            textStat.setText(R.string.taptopause)
            textStat.startAnimation(anim(R.anim.hideandseek))
            Handler(Looper.getMainLooper()).postDelayed({ if (run == Run.WORK) textStat.setText(R.string.work)
                                                        }, 3250)

            circleProgress.setOnClickListener {
                if (pause) {
                    tvTime.clearAnimation()
                    timerStart(thisMs)
                } else {
                    tvTime.startAnimation(anim(R.anim.pause))
                    timer.cancel()
                }; pause = !pause
            }
            stop.setOnClickListener {
                if (circleProgress.max == 2020) {
                    toast.show()
                }; toast.cancel()
                timer.cancel()
                arrowBack.visibility = View.GONE
                arrowGo.visibility = View.GONE
                val anim = ValueAnimator.ofInt(it.width, (it.parent as View).measuredWidth)
                anim.setDuration(200)
                anim.interpolator = DecelerateInterpolator()
                anim.addUpdateListener { animation ->
                    it.layoutParams.width = animation.animatedValue as Int
                    it.layoutParams.height = it.height
                    it.requestLayout()
                }; anim.start()
                Handler(Looper.getMainLooper()).postDelayed({
                    finish()
                }, 200)
            }
            arrowGo.setOnClickListener {
                timer.cancel()
                thisMs = 1
                arrowGo.startAnimation(anim(R.anim.arrowgo))
                timerStart(thisMs)
            }
            arrowBack.setOnClickListener {
                timer.cancel()
                arrowBack.startAnimation(anim(R.anim.arrowback))
                if (get.getInt("afterPicker", 0) == 0) {
                    if (run == Run.WORK) {
                        thisMs = msBreak
                        timerAnim()
                        run = Run.BREAK
                        textStat.setText(R.string._break)
                    } else {
                        thisMs = msWork
                        timerAnim()
                        run = Run.WORK
                        textStat.setText(R.string.work)
                    }
                } else {
                    if (run == Run.WORK) {
                        if (thisAfterBreak == 0) {
                            thisMs = msBb
                            timerAnim()
                            run = Run.BB
                            thisAfterBreak = afterBreak
                            textStat.setText(R.string.big_break)
                        } else {
                            thisMs = msBreak
                            timerAnim()
                            run = Run.BREAK
                            thisAfterBreak--
                            textStat.setText(R.string._break)
                        }
                    } else {
                        thisMs = msWork
                        timerAnim()
                        run = Run.WORK
                        textStat.setText(R.string.work)
                    }
                }
            }
            add1Btn.setOnClickListener {
                timer.cancel()
                thisMs += 60000
                tvTime.startAnimation(anim(R.anim.shake))
                timerStart(thisMs)
            }
        }
    }

    private fun timerStart(long: Long) {
        b.apply {
            timer = object: CountDownTimer(long, 50) {
                override fun onTick(millisUntilFinished: Long) {
                    tvTime.text = String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)-
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)))
                    circleProgress.progress = millisUntilFinished.toInt()
                    thisMs = millisUntilFinished
                    if (a == 20) {
                        notif.setProgress(10000, (thisMs.toFloat() / circleProgress.max.toFloat() * 10000).toInt(), false)
                        notif.setContentText(tvTime.text.toString())
                        nm.notify(100, notif.build())
                        a = 0
                    } else a++
                }
                override fun onFinish() {
                    if (get.getInt("afterPicker", 0) == 0) {
                        if (run == Run.WORK) {
                            thisMs = msBreak
                            timerAnim()
                            notifInit(getString(R.string._break))
                            run = Run.BREAK
                            textStat.setText(R.string._break)
                        } else {
                            thisMs = msWork
                            timerAnim()
                            notifInit(getString(R.string.work))
                            run = Run.WORK
                            textStat.setText(R.string.work)
                        }
                    } else {
                        if (run == Run.WORK) {
                            if (thisAfterBreak == afterBreak) {
                                thisMs = msBb
                                timerAnim()
                                notifInit(getString(R.string.big_break))
                                run = Run.BB
                                thisAfterBreak = 0
                                textStat.setText(R.string.big_break)
                            } else {
                                thisMs = msBreak
                                timerAnim()
                                notifInit(getString(R.string._break))
                                run = Run.BREAK
                                thisAfterBreak++
                                textStat.setText(R.string._break)
                            }
                        } else {
                            thisMs = msWork
                            timerAnim()
                            notifInit(getString(R.string.work))
                            run = Run.WORK
                            textStat.setText(R.string.work)
                        }
                    }
                }
            }.start()
        }
    }

    private fun timerAnim() {
        b.apply {
            tvTime.text = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(thisMs),
                TimeUnit.MILLISECONDS.toSeconds(thisMs)-
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(thisMs)))
            false.also {
                stop.isEnabled = it
                circleProgress.isEnabled = it
                statsBtn.isEnabled = it
                add1Btn.isEnabled = it
                arrowBack.isEnabled = it
                arrowGo.isEnabled = it
            }
            circleProgress.max = 2020
            circleProgress.startAnimation(anim(R.anim.pop))
            tvTime.startAnimation(anim(R.anim.pause))
            textStat.startAnimation(anim(R.anim.pause))
            object: CountDownTimer(1010, 10) {
                override fun onTick(millisUntilFinished: Long) { circleProgress.progress = (1010 - millisUntilFinished.toInt())*2 }
                override fun onFinish() {
                    true.also {
                        stop.isEnabled = it
                        circleProgress.isEnabled = it
                        statsBtn.isEnabled = it
                        add1Btn.isEnabled = it
                        arrowBack.isEnabled = it
                        arrowGo.isEnabled = it
                    }
                    nm.cancel(300)
                    circleProgress.visibility = View.INVISIBLE
                    circleProgress.max = thisMs.toInt()
                    circleProgress.visibility = View.VISIBLE
                    tvTime.clearAnimation()
                    textStat.clearAnimation()
                    timerStart(thisMs)
                }
            }.start()
        }
    }

    private fun anim(int: Int): android.view.animation.Animation {
        return AnimationUtils.loadAnimation(this@TimerActivity, int)
    }

    private fun init() {
        b = ActivityTimerBinding.inflate(layoutInflater)
        setContentView(b.root)
        get = this.getSharedPreferences("value", Context.MODE_PRIVATE)
        set = get.edit()
        toast = Toast.makeText(this, getString(R.string.wait), Toast.LENGTH_SHORT)

        nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            nm.createNotificationChannel(NotificationChannel("channelOnTick", getString(R.string.channelOnTick), NotificationManager.IMPORTANCE_LOW))
            nm.createNotificationChannel(NotificationChannel("channelOnFin", getString(R.string.channelOnFin), NotificationManager.IMPORTANCE_HIGH))
        }
        notif = NotificationCompat.Builder(this, "channelOnTick")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(getString(R.string.work) + "time")
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
        finNotif = NotificationCompat.Builder(this, "channelOnFin")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(getString(R.string.work) + "finish")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        msWork = (((get.getInt("workPickerMinute", 0)*60)+(get.getInt("workPickerSecond", 0)))*1000).toLong()
        msBreak = (((get.getInt("breakPickerMinute", 0)*60)+(get.getInt("breakPickerSecond", 0)))*1000).toLong()
        afterBreak = get.getInt("afterPicker", 0)
        if (afterBreak != 0)
            msBb = (((get.getInt("bbPickerMinute", 1)*60)+ (get.getInt("bbPickerSecond", 0)))*1000).toLong()
        thisMs = msWork
        timerAnim()
    }

    fun notifInit(string: String) {
        finNotif.setContentTitle("$string finish")
        nm.notify(300, finNotif.build())
    }

}