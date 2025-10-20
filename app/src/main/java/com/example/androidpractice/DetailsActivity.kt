package com.example.androidpractice

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import com.example.androidpractice.ui.theme.AndroidPracticeTheme
import kotlinx.serialization.*
import kotlinx.serialization.json.*

class DetailsActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidPracticeTheme {
                val person = Person(intent.getStringExtra("PERSON_FIRSTNAME").toString(),
                    intent.getStringExtra("PERSON_LASTNAME").toString(),
                    intent.getStringExtra("PERSON_BIO").toString(),
                    intent.getStringExtra("PERSON_GENDER").toString(),
                    Company("", intent.getStringExtra("PERSON_JOBTITLE").toString()),
                    intent.getStringExtra("PERSON_IMAGE_URL").toString())

                DetailsActivityScreen(person, onBackClick = {finish()})
            }
        }
    }
}