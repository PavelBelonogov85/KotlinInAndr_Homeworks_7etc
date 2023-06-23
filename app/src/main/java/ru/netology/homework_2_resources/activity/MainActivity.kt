package ru.netology.homework_2_resources.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import ru.netology.homework_2_resources.R
import ru.netology.homework_2_resources.adapter.PostAdapter
import ru.netology.homework_2_resources.adapter.PostListener
import ru.netology.homework_2_resources.dto.Post
import ru.netology.homework_2_resources.databinding.ActivityMainBinding
import ru.netology.homework_2_resources.utils.AndroidUtils
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

        val newPostContract = registerForActivityResult(NewPostActivity.Contract) {result ->
            result ?: return@registerForActivityResult
            viewModel.changeContent(result)
            viewModel.save()
        }

        val adapter = PostAdapter(
            object : PostListener {
                override fun onLike(post: Post) {
                    viewModel.likeById(post.id)
                }

                override fun onShare(post: Post) {
                    viewModel.share(post.id)

                    val intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, post.content)
                        type="text/plain"
                    }
                    val startIntent = Intent.createChooser(intent,getString(R.string.chooser_share_post))
                    startActivity(startIntent)
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
        /*
        viewModel.edited.observe(this) {
            if (it.id == 0L) { // это новый пост - создание
                activityMainBinding.groupEditHeader.visibility = View.GONE // группа с отменой редактирования невидима
                return@observe
            } else {
                activityMainBinding.groupEditHeader.visibility = View.VISIBLE // группа с отменой редактирования видима
            }
            activityMainBinding.content.requestFocus()
            activityMainBinding.content.setText(it.content)
        }
         */

        /*
        activityMainBinding.save.setOnClickListener{
            with(activityMainBinding.content) {
                val content = text?.toString()
                if (content.isNullOrBlank()) {
                    Toast.makeText(this@MainActivity, R.string.empty_post_error, Toast.LENGTH_SHORT).show() // "всплывашка" с предупреждением о пустом тексте
                    return@setOnClickListener  // через @ собаку возвращается значение лямбда-функции
                }
                viewModel.changeContent(content)
                viewModel.save()

                // возвращаем все "как было" :
                setText("")
                clearFocus()
                AndroidUtils.hideKeyboard(this)
            }
        }
        */

        /* добавляем поведение для "своей" кнопки отмены редактирования: */
        activityMainBinding.editCancel.setOnClickListener {
            /* Post оставляем нетронутым как был: */
            viewModel.editCancel()
            /* очищаем поле с контентом и снимаем фокус, аналогично как в save.setOnClickListener : */
            activityMainBinding.content.setText("")
            activityMainBinding.content.clearFocus()
            AndroidUtils.hideKeyboard(it)
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

        /* событие на кнопку добавления нового поста: */
        activityMainBinding.add.setOnClickListener {
            newPostContract.launch(Unit)
        }
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