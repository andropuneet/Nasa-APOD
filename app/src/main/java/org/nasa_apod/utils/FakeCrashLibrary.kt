package org.nasa_apod.utils

class FakeCrashLibrary private constructor() {
    companion object {
        @JvmStatic
        fun log(priority: Int, tag: String?, message: String?) {}
        @JvmStatic
        fun logWarning(t: Throwable?) {}
        @JvmStatic
        fun logError(t: Throwable?) {}
    }

    init {
        throw AssertionError("No instances.")
    }
}