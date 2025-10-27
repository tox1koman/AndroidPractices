package com.example.androidpractice.ui.screens.details

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3Api
import com.example.androidpractice.domain.model.Company
import com.example.androidpractice.domain.model.Person
import com.example.androidpractice.ui.theme.AndroidPracticeTheme

class DetailsActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidPracticeTheme {
                val person = Person(
                    intent.getIntExtra("PERSON_ID", 0),
                    intent.getStringExtra("PERSON_FIRSTNAME").toString(),
                    intent.getStringExtra("PERSON_LASTNAME").toString(),
                    intent.getStringExtra("PERSON_BIO").toString(),
                    intent.getStringExtra("PERSON_GENDER").toString(),
                    Company("", intent.getStringExtra("PERSON_JOBTITLE").toString()),
                    intent.getStringExtra("PERSON_IMAGE_URL").toString()
                )

                DetailsActivityScreen(person, onBackClick = { finish() })
            }
        }
    }
}