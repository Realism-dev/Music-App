package dev.realism.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class DataSource : Parcelable {
    @Parcelize
    data object LOCAL : DataSource()

    @Parcelize
    data object NETWORK : DataSource()

//    @Parcelize
//    data class NETWORK(val baseUrl: String, val token: String) : DataSource()
}
