package id.sireto.reviewjujuradministrator.services.api

import id.sireto.reviewjujuradministrator.models.BaseResponse
import id.sireto.reviewjujuradministrator.models.CodeVerificationRequest
import id.sireto.reviewjujuradministrator.models.SuspendUserRequest
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("/auth/administrator/authentication/whatsapp/{whatsappNo}")
    suspend fun requestVerificationCode(@Path("whatsappNo") whatsappNo: String) : Response<BaseResponse>

    @POST("/auth/administrator/authentication/whatsapp")
    suspend fun verifyWhatsApp(@Body codeVerificationRequest: CodeVerificationRequest) : Response<BaseResponse>

    @GET("/auth/administrator/authorize")
    suspend fun authorizeAdministrator(@Header("Access-Token") token : String) : Response<BaseResponse>

    @GET("/administrator/users")
    suspend fun searchUser(@Header("Access-Token") token : String, @Query("query") query : String, @Query("page") page : Int?) : Response<BaseResponse>

    @GET("/businesses/search")
    suspend fun searchBusiness(@Query("query") query : String, @Query("page") page : Int?) : Response<BaseResponse>

    @GET("/businesses/{businessUid}")
    suspend fun getBusinessByUid(@Path("businessUid") businessUid : String) : Response<BaseResponse>

    @GET("/administrator/reviews/user/{userUid}")
    suspend fun getUserReviews(@Path("userUid") userUid: String, @Header("Access-Token") token: String, @Query("createdAt") createdAt : String) : Response<BaseResponse>

    @GET("/reviews/business")
    suspend fun getBusinessReviews(@Query("businessUid") businessUid : String, @Query("createdAt") createdAt : String) : Response<BaseResponse>

    @PATCH("/administrator/users/suspend")
    suspend fun suspendUser(@Header("Access-Token") token : String, @Body suspendUserRequest: SuspendUserRequest) : Response<BaseResponse>
}