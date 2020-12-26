package github.sachin2dehury.nitrmail.api.data

import com.google.gson.annotations.SerializedName

data class Sender(
    @SerializedName("a") val email: String = "",
    @SerializedName("d") val firstName: String = "",
    @SerializedName("p") val name: String = "",
    @SerializedName("t") val isReceiver: String = ""
)