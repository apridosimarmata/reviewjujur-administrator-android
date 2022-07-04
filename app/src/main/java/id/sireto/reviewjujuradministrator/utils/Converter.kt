package id.sireto.reviewjujuradministrator.utils

import com.google.gson.internal.LinkedTreeMap
import id.sireto.reviewjujuradministrator.models.AuthenticationResponse
import id.sireto.reviewjujuradministrator.models.BusinessResponse
import id.sireto.reviewjujuradministrator.models.ReviewResponse
import id.sireto.reviewjujuradministrator.models.UserResponse

object Converter {
    fun anyToAthenticationResponse(any : LinkedTreeMap<String, Any>) =
        AuthenticationResponse(any["token"].toString())

    fun anyToUserResponse(any : LinkedTreeMap<*, *>) =
        UserResponse(any["uuid"].toString(), any["name"].toString(), any["email"].toString(), any["whatsappNo"].toString(), any["unsuspendAt"].toString())

    /*fun anyToProvinceResponse(any: LinkedTreeMap<String, Any>) =
        ProvinceResponse(any["uid"].toString(), any["name"].toString())

    fun anyToLocationResponse(any: LinkedTreeMap<String, Any>) =
        LocationResponse(any["uid"].toString(), any["province_uid"].toString() ,any["name"].toString())*/

    fun anyToBusinessResponse(any: LinkedTreeMap<*, *>) =
        BusinessResponse(
            any["uid"].toString(),
            any["reviewsCount"].toString().split(".")[0].toInt(),
            any["totalScore"].toString().split(".")[0].toInt(),
            any["ownerUid"].toString(),
            any["locationUid"].toString(),
            any["provinceUid"].toString(),
            any["location"].toString(),
            any["province"].toString(),
            any["name"].toString(),
            any["address"].toString(),
            any["photo"].toString(),
            any["createdAt"].toString(),
            any["modifiedAt"].toString(),
        )

    /*
    fun anyToBusinessPagination(any: LinkedTreeMap<String, Any>) : BusinessPagination {
        val result = BusinessPagination(
            any["limit"].toString().split(".")[0].toInt(),
            any["page"].toString().split(".")[0].toInt(),
            any["sort"].toString(),
            any["search"].toString(),
            any["locationUid"].toString(),
            arrayListOf(),
            any["location"].toString(),
            any["province"].toString()
        )
        (any["rows"] as List<Any>).map {
            result.rows.add(anyToBusinessResponse(it as LinkedTreeMap<String, Any>))
        }

        return result
    }
    */
    fun anyToReviewResponse(any: LinkedTreeMap<*, *>) : ReviewResponse =
        ReviewResponse(
            any["userUid"].toString(),
            any["businessUid"].toString(),
            any["text"].toString(),
            any["uid"].toString(),
            any["score"].toString().toFloat().toInt(),
            any["createdAt"].toString(),
            any["status"].toString(),
        )

}