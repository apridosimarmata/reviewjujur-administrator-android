package id.sireto.reviewjujuradministrator.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class BusinessResponse(
    @SerializedName("uid")
    val uid : String,

    @SerializedName("reviewsCount")
    val reviewsCount : Int,

    @SerializedName("totalScore")
    val totalScore : Int,

    @SerializedName("ownerUid")
    val onwerUid : String,

    @SerializedName("locationUid")
    val locationUid : String,

    @SerializedName("provinceUid")
    val provinceUid : String,

    @SerializedName("location")
    var location : String,

    @SerializedName("province")
    var province : String,

    @SerializedName("name")
    var name : String,

    @SerializedName("address")
    var address : String,

    @SerializedName("photo")
    val photo: String,

    @SerializedName("createdAt")
    val createdAt : String,

    @SerializedName("modifiedAt")
    val modifiedAt : String
) : Serializable {
    constructor() : this("", 0, 0,"", "", "", "", "", "", "", "", "", "")
}
