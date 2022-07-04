package id.sireto.reviewjujuradministrator.models

import com.google.gson.annotations.SerializedName

data class AuthenticationResponse (
    @SerializedName("token")
    val token : String,
) {
    constructor() : this("")
}