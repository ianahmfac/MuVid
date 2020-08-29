package id.ianahmfac.muvid.utils

sealed class Resources<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : Resources<T>(data)
    class Error<T>(msg: String, data: T? = null) : Resources<T>(data, msg)
    class Loading<T> : Resources<T>()
}