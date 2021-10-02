package com.reselling.visionary.ui.addBooks

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reselling.visionary.data.models.books.BooksResponseModel
import com.reselling.visionary.data.network.networkResponseType.Resource
import com.reselling.visionary.data.preferences.PreferencesManager
import com.reselling.visionary.data.repository.AuthRepository
import com.reselling.visionary.data.repository.BookRepository
import com.reselling.visionary.utils.Choose
import com.theartofdev.edmodo.cropper.CropImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import javax.inject.Inject

//private const val TAG = "AddBookViewModel"

@HiltViewModel
class AddBookViewModel @Inject constructor(
        private val state: SavedStateHandle,
        private val bookRepository: BookRepository,
        private val preferencesManager: PreferencesManager,
        private val userRepo: AuthRepository
) : ViewModel() {


    fun addImageBtnClicked() = viewModelScope.launch {
        when {
            //if do not have external storage permission
            !bookRepository.checkExternalStoragePermission() -> {
                _addBookChannel.send(AddBookEvents.RequestPermission(bookRepository.permissions))
            }
            else -> {
                _addBookChannel.send(AddBookEvents.AddBookImageEvent)
            }
        }
    }

    lateinit var multipartImage: MultipartBody.Part


    var bookName = ""
        set(value) {
            field = value
        }

    var bookDescription = ""
        set(value) {
            field = value
        }

    var bookImage = ""
        set(value) {
            field = value
        }

    var bookSellingPrize = ""
        set(value) {
            field = value
        }

    var bookOriginalPrize = ""
        set(value) {
            field = value
        }

    var bookCategory = Choose
        set(value) {
            field = value
        }

    private val _addBookChannel = Channel<AddBookEvents>()
    val addBookEvents = _addBookChannel.receiveAsFlow()


    sealed class AddBookEvents {
        data class ShowInvalidInputMessage(val msg: String) : AddBookEvents()
        data class RequestPermission(val permissions: Array<String>) : AddBookEvents()
        object InternetProblem : AddBookEvents()
        object LoadingEvent : AddBookEvents()
        object AddBookImageEvent : AddBookEvents()
        object AddedBookEvent : AddBookEvents()
        object RerequestPermission : AddBookEvents()
        data class ShowImage(val uri: Uri) : AddBookEvents()
        data class AddLocation(val address: String) : AddBookEvents()
    }

    private fun showInvalidInputMessage(text: String) = viewModelScope.launch {
        _addBookChannel.send(AddBookEvents.ShowInvalidInputMessage(text))
    }

    fun handelPermissionResult() = viewModelScope.launch {

        when {
            //If User Granted Permission
            bookRepository.checkExternalStoragePermission() -> {
                _addBookChannel.send(AddBookEvents.AddBookImageEvent)
            }
            else -> {
                _addBookChannel.send(AddBookEvents.RerequestPermission)
            }
        }
    }

    fun handelImageUri(uri: Uri?) = viewModelScope.launch {

        if (uri != null) {
            val result = bookRepository.convertUriToBytes(uri)

            when (result) {
                is BookRepository.ConvertImageEvents.ShowInvalidInputMessage -> {
                    showInvalidInputMessage(result.msg)
                }
                is BookRepository.ConvertImageEvents.SuccessfulConvert -> {
                    _addBookChannel.send(AddBookEvents.ShowImage(uri))
                    bookImage = uri.toString()
                    preferencesManager.preferenceFlow.collect {
                        makeMutipartImage(it.id, result.imageByteArray)
                    }
                }
            }
        } else {
            showInvalidInputMessage("Please Choose Another Image")
        }
    }

    private fun makeMutipartImage(id: String, imageByteArray: ByteArray) {
        val image: MultipartBody.Part? =
                bookRepository.convertByteArrayToMultiPart(id, imageByteArray)
        if (image == null) {
            showInvalidInputMessage("Please Choose Another Image")

        } else {
            multipartImage = image
        }
    }

    fun addBookBtnClicked() {

        //logBookValues()

        when {
            bookName.trim().isBlank() -> showInvalidInputMessage("Please Enter Book Name")
            bookDescription.trim().isBlank() -> showInvalidInputMessage("Please Enter Book Description")
            bookSellingPrize.trim().isBlank() -> showInvalidInputMessage("Please Enter Selling prize")
            bookCategory == Choose -> showInvalidInputMessage("Please Choose Category")
            bookImage.trim().isBlank() -> showInvalidInputMessage("Please Choose Image")
            bookOriginalPrize.trim().isBlank() -> showInvalidInputMessage("Please Enter Original prize")
            bookOriginalPrize.toInt() < bookSellingPrize.toInt() -> showInvalidInputMessage("Selling prize can not be greater than original Prize")
            bookOriginalPrize.toInt() > 10000  ->  showInvalidInputMessage("Original Prize is too much")
            bookSellingPrize.toInt() > 10000  ->  showInvalidInputMessage("Selling Prize is too much")
            else -> {
//                Log.e(TAG, "addBookBtnClicked: Else Part Now", )
//                logBookValues()
                makeRequestCall()
            }
        }
    }

    /* private fun logBookValues() {
         Log.e(
             TAG, "addBookBtnClicked: " + """

                 book name ${bookName}

                 bookDescription ${bookDescription}

                 bookSellingPrize ${bookSellingPrize}

                 bookCategory ${bookCategory}

                 bookImage ${bookImage}

                 bookOriginal ${bookOriginalPrize}

                 compare $bookOriginalPrize < $bookSellingPrize  ${bookOriginalPrize < bookSellingPrize}

             """.trimIndent()
         )
     }
 */
    private fun makeRequestCall() = viewModelScope.launch {
        _addBookChannel.send(AddBookEvents.LoadingEvent)
        userRepo.getUserFlow().collect {
            val bookResponse: Resource<BooksResponseModel> = bookRepository.addBook(
                    it,
                    multipartImage,
                    bookName,
                    bookDescription,
                    bookOriginalPrize,
                    bookSellingPrize,
                    bookCategory
            )
            handelAddedBookResponse(bookResponse)
        }
    }

    private fun handelAddedBookResponse(response: Resource<BooksResponseModel>) =
            viewModelScope.launch {
                when (response) {
                    is Resource.NoInterException ->
                        _addBookChannel.send(AddBookEvents.InternetProblem)

                    is Resource.Failure -> {
                        showInvalidInputMessage("Error : ${response.errorBody}")
                    }

                    is Resource.Success -> {
                        withContext(Dispatchers.IO) {

                            deleteAllValue()

                            _addBookChannel.send(AddBookEvents.AddedBookEvent)
                        }
                    }

                }
            }

    private fun deleteAllValue() {
        bookName = ""
        bookImage = ""
        bookDescription = ""
        bookSellingPrize = ""
        bookOriginalPrize = ""
        /* Log.e(TAG, "deleteAllValue: Deleted Values", )
         logBookValues()*/
    }

    fun addLocation() = viewModelScope.launch {
        userRepo.getUserFlow().collect {
            try {
                _addBookChannel.send(AddBookEvents.AddLocation(it.address))
            } catch (e: Exception) {
//                Log.e(TAG, "addLocation: ${e.localizedMessage}")
            }
        }
    }

    class GetImageFromGalleryContract : ActivityResultContract<Int, Uri?>() {
        override fun createIntent(context: Context, ringtoneType: Int) =
                CropImage.activity().setAspectRatio(16, 16).getIntent(context)

        override fun parseResult(resultCode: Int, result: Intent?): Uri? {
            if (resultCode != Activity.RESULT_OK) {
                return null
            }

            return CropImage.getActivityResult(result).uri
        }
    }


}



