package com.reselling.visionary.data.repository

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.app.ActivityCompat
import com.reselling.visionary.data.models.books.BooksResponseModel
import com.reselling.visionary.data.models.userModel.User
import com.reselling.visionary.data.network.apis.BooksApi
import com.reselling.visionary.data.network.networkResponseType.MySafeApiRequest
import com.reselling.visionary.data.network.networkResponseType.Resource
import com.reselling.visionary.utils.getUniqueName
import com.reselling.visionary.utils.toImageRequestBody
import com.reselling.visionary.utils.toMultipartReq
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.lang.Exception
import javax.inject.Inject

private const val TAG = "BookRepository"

class BookRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val booksApi: BooksApi,
) : MySafeApiRequest() {

    val externalStorage = android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    val permissions = arrayOf(externalStorage)

    //return true -> permission is  granted
    fun checkExternalStoragePermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            externalStorage
        ) == PackageManager.PERMISSION_GRANTED
    }

    suspend fun convertUriToBytes(uri: Uri): ConvertImageEvents {

        return withContext(Dispatchers.Default) {

            try {

                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                val byteBuff = ByteArrayOutputStream()
                val buff = ByteArray(1024)
                var len = 0

                while (inputStream!!.read(buff).also { len = it } != -1) {

                    byteBuff.write(buff, 0, len)
                }

                val imageBytesArray = byteBuff.toByteArray()
                if (imageBytesArray.isNotEmpty()) {
                    return@withContext ConvertImageEvents.SuccessfulConvert(imageBytesArray)
                } else {
                    return@withContext ConvertImageEvents.ShowInvalidInputMessage("Can Not Convert this Image Please Choose Another")
                }


            } catch (exception: Exception) {
                return@withContext ConvertImageEvents.ShowInvalidInputMessage(
                    exception.localizedMessage ?: "Can Not Convert this Image"
                )
            }

        }

    }

    suspend fun deleteBook(id: String): Resource<BooksResponseModel> {
        return withContext(Dispatchers.IO) {
            apiRequest { booksApi.deleteBook(id) }
        }
    }


    fun convertByteArrayToMultiPart(
        userId: String,
        imageByteArray: ByteArray
    ): MultipartBody.Part? {


        val name = userId.getUniqueName()
        val multipartBody = imageByteArray.toImageRequestBody()

        return MultipartBody.Part.createFormData("bookImage", name, multipartBody)
    }

    suspend fun addBook(
        user: User,
        bookImage: MultipartBody.Part,
        bookName: String,
        bookDescription: String,
        bookOriginalPrize: String,
        bookSellingPrize: String,
        bookCategory: String
    ): Resource<BooksResponseModel> {

        return apiRequest {
            booksApi.addBook(
                bookName = bookName.toMultipartReq(),
                bookDescription = bookDescription.toMultipartReq(),
                bookOriginalPrize = bookOriginalPrize.toMultipartReq(),
                bookSellingPrize = bookSellingPrize.toMultipartReq(),
                bookCategory = bookCategory.toMultipartReq(),
                userPhoneNumber = user.phone.toMultipartReq(),
                userId = user.id.toMultipartReq(),
                BookCity = user.city.toMultipartReq(),
                BookLocation = user.location.toMultipartReq(),
                BookAddress = user.address.toMultipartReq(),
                BookDistrict = user.district.toMultipartReq(),
                bookImage = bookImage
            )

        }


    }

    sealed class ConvertImageEvents() {
        data class ShowInvalidInputMessage(val msg: String) : ConvertImageEvents()
        data class SuccessfulConvert(val imageByteArray: ByteArray) : ConvertImageEvents()
    }

    suspend fun getBooksAddedByUser(userId: String) = apiRequest {
        booksApi.getBooksAddedByUser(userId)
    }

    suspend fun getSellerById(sellerId: String) = apiRequest {
        booksApi.getSellerById(sellerId)
    }

    suspend fun getBooksByCategory(district: String, userId: String, category: String, query: String) =
        apiRequest {
            booksApi.getBooksByCategory(
                    district = district,
                    userId = userId,
                    category = category,
                    query = query)
        }
}
