package com.example.androidpractice

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.*
import java.io.File

@Serializable
data class Person(
    val firstName: String,
    val lastName: String,
    val bio: String,
    val sex: String,
    val gender: String,
    val jobTitle: String,
    val personPortraitPath: String
)
@Composable
fun ScreenList(){
    val paulson = Person("Роберт",
        "Полсон",
        "Роберт Полсон, он же Боб, он же \"чувак с легендарными... гм, обнимашками\" — это не просто участник Бойцовского клуба, а настоящий герой андеграунда, который доказал, что даже с \"чемоданами\" на груди можно быть звездой! Этот здоровяк с сердцем размером с его бицепсы (а они немаленькие) превращает каждый бой в эпичное шоу: \"Сдавайтесь, или я задушу вас своей харизмой!\"",
        "Мужчина",
        "Гетеро",
        "Боец",
        "robert.png"
    )

    val pechkin = Person(
        "Игорь",
        lastName = "Печкин",
        bio = """
                Печкин предпенсионного возраста. 
                Ему 64 года. 
                Он довольно вредный, любопытный, трусливый и занудный, а также склонен к формализму: соблюдает все инструкции, не делая никому исключений. 
                Ещё он часто без спроса заходит в гости к Дяде Фёдору в дом. 
                Очень любит свою работу на почте. 
            """.trimIndent(),
        sex = "Мужской",
        gender = "Гетеро",
        jobTitle = "Почтальон",
        personPortraitPath = "pechkin.png"
    )

    val persons = mutableListOf(paulson, pechkin)
    for (i in 1..5) {
        persons.add(paulson)
        persons.add(pechkin)
    }



    LazyColumn (modifier = Modifier.fillMaxSize()) {
        items(persons) { person ->
            Row(modifier = Modifier.fillMaxWidth().padding(5.dp).height(80.dp),
                verticalAlignment = Alignment.CenterVertically){

                val context = LocalContext.current
                val intent = Intent(context, DetailsActivity::class.java)

                intent.putExtra("PERSON_FIRSTNAME", person.firstName)
                intent.putExtra("PERSON_LASTNAME", person.lastName)
                intent.putExtra("PERSON_BIO", person.bio)
                intent.putExtra("PERSON_SEX", person.sex)
                intent.putExtra("PERSON_GENDER", person.gender)
                intent.putExtra("PERSON_JOBTITLE", person.jobTitle)
                intent.putExtra("PERSON_PERSONPORTRAITPATH", person.personPortraitPath)

                Button(onClick = {
                    context.startActivity(intent)
                }, modifier = Modifier.fillMaxWidth()) {
                    val assetMan = context.assets
                    val img = BitmapFactory.decodeStream(assetMan.open(person.personPortraitPath))
                    if (img != null) {
                        Image(
                            bitmap = img.asImageBitmap(),
                            modifier = Modifier.padding(end = 10.dp).width(50.dp),
                            contentDescription = "Person Image",
                        )
                    }
                Text(text = "${person.firstName} ${person.lastName}")
            }
            }
        }
    }
}