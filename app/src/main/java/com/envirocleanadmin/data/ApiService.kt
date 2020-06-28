package com.envirocleanadmin.data

import com.envirocleanadmin.base.BaseResponse
import com.envirocleanadmin.data.response.AddCommunityResponse
import com.envirocleanadmin.data.response.CommunityAreaListResponse
import com.envirocleanadmin.data.response.ListOfCommunityResponse
import com.envirocleanadmin.data.response.LoginResponse

import io.reactivex.Observable
import retrofit2.http.*


interface ApiService {
    // [START] Demo APIs
    /*@GET("test=123")
    fun apiGet(): Observable<BaseResponse>

    @FormUrlEncoded
    @POST("login")
    fun apiSignIn(@FieldMap params: HashMap<String, String>): Observable<BaseResponse>

    @GET("profile")
    fun apiProfile(@QueryMap params: HashMap<String, String>): Observable<BaseResponse>

    @Multipart
    @POST("update_profile")
    fun apiUpdateProfile(@PartMap params: HashMap<String, RequestBody>): Observable<BaseResponse>*/
    // [END] Demo APIs

    @FormUrlEncoded
    @POST("admin/login")
    fun apiLogin(@FieldMap params: HashMap<String, String>): Observable<LoginResponse>

    @FormUrlEncoded
    @POST("password/email")
    fun apiForgotPassword(@FieldMap params: HashMap<String, String>): Observable<BaseResponse>

    @FormUrlEncoded
    @POST("admin/get_list_of_communities")
    fun apiCommunityList(@FieldMap params: HashMap<String, Any>): Observable<ListOfCommunityResponse>

    @FormUrlEncoded
    @POST("admin/get_all_communities_areas")
    fun apiCommunityArea(@FieldMap params: HashMap<String, Any>): Observable<CommunityAreaListResponse>

    @FormUrlEncoded
    @POST("admin/create_area")
    fun apiAddCommunityArea(@FieldMap params: HashMap<String, Any>): Observable<AddCommunityResponse>

    @FormUrlEncoded
    @POST("admin/update_area")
    fun apiEditCommunityArea(@FieldMap params: HashMap<String, Any>): Observable<AddCommunityResponse>
}