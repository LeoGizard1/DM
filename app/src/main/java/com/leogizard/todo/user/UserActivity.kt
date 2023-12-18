package com.leogizard.todo.user

import android.Manifest
import android.content.ContentValues
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.leogizard.todo.data.Api
import com.leogizard.todo.user.ui.theme.TODOLeoGizardTheme
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class UserActivity : ComponentActivity() {

    private fun Bitmap.toRequestBody(): MultipartBody.Part {
        val tmpFile = File.createTempFile("avatar", "jpg")
        tmpFile.outputStream().use { // *use* se charge de faire open et close
            this.compress(Bitmap.CompressFormat.JPEG, 100, it) // *this* est le bitmap ici
        }
        return MultipartBody.Part.createFormData(
            name = "avatar",
            filename = "avatar.jpg",
            body = tmpFile.readBytes().toRequestBody()
        )
    }

    private fun Uri.toRequestBody(): MultipartBody.Part {
        val fileInputStream = contentResolver.openInputStream(this)!!
        val fileBody = fileInputStream.readBytes().toRequestBody()
        return MultipartBody.Part.createFormData(
            name = "avatar",
            filename = "avatar.jpg",
            body = fileBody
        )
    }

    private val capturedUri by lazy {
        contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, ContentValues())
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var bitmap: Bitmap? by remember {mutableStateOf(null)}
            var uri: Uri? by remember { mutableStateOf(null)}

            val composeScope = rememberCoroutineScope()
            val takePicture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
                success ->
                if (success) uri = capturedUri
                composeScope.launch {
                    Api.userWebService.updateAvatar(uri!!.toRequestBody())
                }
            }

            val pickPhoto = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) {
                uri = it
                composeScope.launch {
                    if(uri != null) {
                        Api.userWebService.updateAvatar(uri!!.toRequestBody())
                    }
                }
            }

            val getPerm = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {

            }

            Column {
                AsyncImage(
                    modifier = Modifier.fillMaxHeight(.2f),
                    model = bitmap ?: uri,
                    contentDescription = null
                )
                Button(
                    onClick = {takePicture.launch(capturedUri)},
                    content = { Text("Take picture") }
                )
                Button(
                    onClick = {
                              getPerm.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                              pickPhoto.launch(PickVisualMediaRequest((ActivityResultContracts.PickVisualMedia.ImageOnly)))
                    },
                    content = { Text("Pick photo") }
                )
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TODOLeoGizardTheme {
        Greeting("Android")
    }
}