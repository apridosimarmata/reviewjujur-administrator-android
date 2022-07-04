package id.sireto.reviewjujuradministrator.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UserResponse(
    @SerializedName("uuid")
    var uid : String,

    @SerializedName("name")
    var name : String,

    @SerializedName("email")
    var email : String,

    @SerializedName("whatsappNo")
    var whatsappNo : String,

    @SerializedName("unsuspendAt")
    var unsuspendAt : String?
) : Serializable {
    constructor() : this("", "","", "", "")
}
