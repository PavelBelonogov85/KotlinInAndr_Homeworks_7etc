package ru.netology.homework_2_resources

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import ru.netology.homework_2_resources.adapter.PostAdapter
import ru.netology.homework_2_resources.dto.Post
import ru.netology.homework_2_resources.databinding.ActivityMainBinding
import ru.netology.homework_2_resources.databinding.CardPostBinding
import ru.netology.homework_2_resources.utils.StringsVisability
import ru.netology.homework_2_resources.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i("pvl_info", "Запустилось")

        val activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        //////
        // читаем данные поста из JSON:
        /*
        val fileJSON = File("./src/main/res/raw/posts.json") // почему не открывается файл??
        var jsonString:String
        FileReader(fileJSON).use {
            val chars = CharArray(fileJSON.length().toInt())
            it.read(chars)
            jsonString = String(chars)
        }
        val mapper = jacksonObjectMapper()
        */
        // вот на этой строке ошибка:
        // val postFromJson = mapper.readValue<Post>(jsonString) // ошибка "None of the following functions can be called with the arguments supplied."
        //////

        val viewModel: PostViewModel by viewModels() // функция «by viewModels()» означает, что сколько бы раз activity не пересоздавался, мы будем получать одну и ту же ссылку на одну и ту же модель (ViewModel)

        val adapter = PostAdapter(
            onLikeClicked = {
                viewModel.likeById(it.id)
        },
            onShareClicked = {
                viewModel.share(it.id)
            })

        viewModel.data.observe(this) { posts -> // posts это уже List<Post>. Наблюдаем (observe) за изменением data. Если оно происходит - получаем на входе объект posts...
            adapter.submitList(posts)
            /*
                /* все изменения визуализации (тексты, картинки... типа author.text = post.author) переезжают в класс PostViewHolder
                а все изменения скрытых данных (счетчики и др.) будут в функциях viewModel */

            /* теперь добавляем все получившиеся (заполненные) вьюшки на родителя - на activityMainBinding */
            views.forEach() {
                activityMainBinding.root.addView(it.root)
            }
            */
        }

        activityMainBinding.list.adapter = adapter
    }


    /* << тестирование подписок на события (д/з 4.2) */
    /*binding.root.setOnClickListener {
        Log.i("pvl_info", "click on root")
    }
    binding.avatar.setOnClickListener {
        Log.i("pvl_info", "click on avatar")
    }*/
    /* тестирование подписок на события (д/з 4.2) >> */
}