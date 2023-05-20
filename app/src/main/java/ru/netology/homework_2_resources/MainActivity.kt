package ru.netology.homework_2_resources

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import ru.netology.homework_2_resources.adapter.PostAdapter
import ru.netology.homework_2_resources.adapter.PostListener
import ru.netology.homework_2_resources.dto.Post
import ru.netology.homework_2_resources.databinding.ActivityMainBinding
import ru.netology.homework_2_resources.databinding.CardPostBinding
import ru.netology.homework_2_resources.utils.AndroidUtils
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
            object : PostListener {
                override fun onLike(post: Post) {
                    viewModel.likeById(post.id)
                }

                override fun onShare(post: Post) {
                    viewModel.share(post.id)
                }

                override fun onRemove(post: Post) {
                    viewModel.removeById(post.id)
                }

                override fun onEdit(post: Post) {
                    viewModel.edit(post)
                }
            }
        )

        /* после того, как был выбран пост для редактирования, на нем надо сфокусироваться: */
        viewModel.edited.observe(this) {
            if (it.id == 0L) {
                return@observe
            }
            activityMainBinding.content.requestFocus()
            activityMainBinding.content.setText(it.content)
        }

        activityMainBinding.save.setOnClickListener{
            with(activityMainBinding.content) {
                val content = text?.toString()
                if (content.isNullOrBlank()) {
                    Toast.makeText(this@MainActivity, R.string.empty_post_error, Toast.LENGTH_SHORT).show() /* "всплывашка" с предупреждением о пустом тексте */
                    return@setOnClickListener  /* через @ собаку возвращается значение лямбда-функции */
                }

                viewModel.changeContent(content)
                viewModel.save()

                /* возвращаем все "как было" : */
                setText("")
                clearFocus()
                AndroidUtils.hideKeyboard(this)
            }
        }

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