package com.yusril.mvpmonitoring.core.vo

data class Resource<T>(val status: Status, val data: T?, val message: String?) {
    companion object {
        fun <T> loading() : Resource<T> = Resource(Status.LOADING, null, null)
        fun <T> success(data: T) : Resource<T> = Resource(Status.SUCCESS, data, null)
        fun <T> empty() : Resource<T> = Resource(Status.EMPTY, null, null)
        fun <T> error(message: String) : Resource<T> = Resource(Status.ERROR, null, message)
    }
}
