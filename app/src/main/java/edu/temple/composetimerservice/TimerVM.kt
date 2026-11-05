package edu.temple.composetimerservice

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

// AndroidViewModel gives access to Android context. This context
// sits above and survives all component-level context objects (Activity/Service)
// It is bad practice to give Activity-level context to a ViewModel
class TimerVM(private val application: Application) : AndroidViewModel(application) {

    private var timerBinder : TimerService.TimerBinder? = null
    var currentTimer by mutableIntStateOf(0)
    private val serviceConnection : ServiceConnection by lazy {
        object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                timerBinder = service as TimerService.TimerBinder
                viewModelScope.launch {
                    timerBinder?.getCurrentTime()?.collect {
                        currentTimer = it
                    }
                }
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                timerBinder = null
            }
        }
    }

    init {
        application.bindService(
            Intent(
                application,
                TimerService::class.java
            ),
            serviceConnection,
            Context.BIND_AUTO_CREATE
        )
    }

    fun startTimer() {
        timerBinder?.startTimer()
    }

    fun stopTimer() {
        timerBinder?.stopTimer()
    }

    override fun onCleared() {
        super.onCleared()
        application.unbindService(serviceConnection)
    }
}