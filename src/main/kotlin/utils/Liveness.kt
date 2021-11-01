package utils

import java.lang.Exception

object Liveness {
    var isAlive = true
        private set

    fun kill(årsak: String, exception: Exception) {
        isAlive = false
    }
}