package edu.temple.composetimerservice

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import edu.temple.composetimerservice.TimerInfo._currentTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds


/*
    Method 1: Allow global access to TimerInfo - allows direct
    access to any client, including Activity
 */
object TimerInfo {
    private val _currentTime = MutableStateFlow(0)
    val currentTime = _currentTime.asStateFlow()

    fun postNewTime(currentTime: Int) {
        _currentTime.value = currentTime
    }
}

class TimerService : Service() {

    /*
        Method 2a: Allow limited access to TimerInfo - allows proper layer
        separation, e.g. : Service <-> ViewModel <-> Activity
    */
    private val currentTime = MutableStateFlow(0)

    private var job: Job? = null

    override fun onBind(intent: Intent): IBinder {
        return TimerBinder()
    }

    inner class TimerBinder : Binder() {
        fun startTimer() {
            this@TimerService.startTimer()
        }

        fun stopTimer() {
            this@TimerService.stopTimer()
        }

        /*
        Method 2b: expose StateFlow via Binder
    */
        fun getCurrentTime() : StateFlow<Int> {
            return this@TimerService.currentTime.asStateFlow()
        }

        val isRunning = job?.isActive
    }


    fun startTimer() {
        job?.cancel()
        job = CoroutineScope(Dispatchers.Default).launch {
            for (i in 100 downTo 0) {
                Log.d("Countdown", "$i")

                // Send timer data via global and local channel
                TimerInfo.postNewTime(i) // Global
                postNewTime(i) // Local
                delay(1.seconds)
            }
        }
    }

    fun stopTimer() {
        job?.cancel()
    }

    fun postNewTime(time: Int) {
        this.currentTime.value = time
    }
}