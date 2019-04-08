package com.tk.vanishtalk.network

import com.tk.vanishtalk.model.data.local.LocalUserForNetwork
import com.tk.vanishtalk.model.data.remote.RemoteUser
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface RetrofitInterface {

    @GET("/users/{uid}")
    fun getUserInfo(@Path("uid") uid: String): Observable<Response<RemoteUser>>

    @GET("/users/{uid}/allfriends")
    fun getFriendsList(@Path("uid") uid: String): Observable<Response<ArrayList<RemoteUser>>>

    @Multipart
    @POST("/users/{uid}/img")
    fun setUserImg(@Path("uid") uid: String, @Part file: MultipartBody.Part): Observable<Response<String>>

    @POST("/users/{uid}")
    fun setUserInfo(@Path("uid") uid: String): Observable<Response<String>>

    @PUT("/users/{uid}")
    fun updateUserInfo(@Path("uid") uid: String, @Body user: LocalUserForNetwork): Observable<Response<String>>

    @PUT("/users/{uid}/friend")
    fun updateUserFriendsInfo(@Path("uid") uid: String, @Body friend: LocalUserForNetwork): Observable<Response<RemoteUser>>

    @DELETE("/users/{uid}")
    fun deleteUserInfo(@Path("uid") uid: String): Observable<Response<String>>
}