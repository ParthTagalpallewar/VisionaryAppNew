package com.reselling.visionary.data.network.apis

import com.reselling.visionary.data.models.books.BooksResponseModel
import com.reselling.visionary.data.models.userModel.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface BooksApi {

    @FormUrlEncoded
    @POST("deleteingSingleBook")
    suspend fun deleteBook(
        @Field("book_id") id: String
    ): Response<BooksResponseModel>

    @GET("getBooksPage")
    suspend fun getBooksUsingPaging(
        @Query("limit") per_page: Int,
        @Query("start") page: Int,
        @Query("district") district: String?,
        @Query("id") userId: String,
        @Query("query") query: String,
    ): BooksResponseModel


    @GET("getBooksPage")
    suspend fun getBooksInHome(
        @Query("limit") per_page: Int,
        @Query("start") page: Int = 0,
        @Query("district") district: String?,
        @Query("id") userId: String,
        @Query("query") query: String,
    ): Response<BooksResponseModel>

    @GET("getBooksByCategories")
    suspend fun getBooksByCategory(
        @Query("district") district: String,
        @Query("id") userId: String,
        @Query("category") category: String,
        @Query("query") query: String,
    ): Response<BooksResponseModel>

    //add New Book
    @Multipart
    @POST("addBook")
    suspend fun addBook(
        @Part("book_name") bookName: RequestBody,
        @Part("book_desc") bookDescription: RequestBody,
        @Part("book_orig") bookOriginalPrize: RequestBody,
        @Part("book_selling") bookSellingPrize: RequestBody,
        @Part("book_category") bookCategory: RequestBody,
        @Part("phone") userPhoneNumber: RequestBody,
        @Part("user_id") userId: RequestBody,
        @Part bookImage: MultipartBody.Part?,
        @Part("city") BookCity: RequestBody,
        @Part("location") BookLocation: RequestBody,
        @Part("address") BookAddress: RequestBody,
        @Part("district") BookDistrict: RequestBody
    ): Response<BooksResponseModel>

    //Get All books Added by user
    @FormUrlEncoded
    @POST("getBooksAddedByUser")
    suspend fun getBooksAddedByUser(
        @Field("userid") id: String
    ): Response<BooksResponseModel>


    @FormUrlEncoded
    @POST("getSellerById")
    suspend fun getSellerById(
        @Field("sellerId") sellerId: String,
    ): Response<User>

}