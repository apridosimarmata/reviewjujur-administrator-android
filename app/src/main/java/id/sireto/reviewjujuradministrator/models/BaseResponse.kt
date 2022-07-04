package id.sireto.reviewjujuradministrator.models

import com.google.gson.annotations.SerializedName

data class BaseResponse(
    @SerializedName("meta")
    var meta : Meta,

    @SerializedName("result")
    var result : Any?
) {
    constructor() : this(Meta(400, "Client error"), null)
}

data class Meta(
    @SerializedName("code")
    var code : Int,

    @SerializedName("message")
    var message : String?
)
