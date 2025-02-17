package dev.realism.tracks

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.realism.data.DataSource
import dev.realism.data.TracksRepository
import dev.realism.tracks.di.NavigationProvider
import dev.realism.tracks.di.TracksComponentProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


abstract class BaseTracksFragment : Fragment(R.layout.fragment_tracks) {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    @Inject
    lateinit var repository: TracksRepository

    private lateinit var navigationProvider: NavigationProvider

    private lateinit var clearButton: ImageView
    private lateinit var searchEditText: EditText
    private lateinit var emptyListPlaceholder:TextView

    protected lateinit var viewModel: TracksViewModel
    private lateinit var adapter: TracksAdapter
    private lateinit var dataSource: DataSource

    override fun onAttach(context: Context) {
        super.onAttach(context)
        //Инъекция зависимостей через интерфейсы
        (context.applicationContext as TracksComponentProvider)
            .getTracksComponent()
            .injectBaseTracksFragment(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Инициализация навигации
        navigationProvider = requireActivity() as NavigationProvider

        // Инициализация UI элементов
        clearButton = view.findViewById(R.id.clearButton)
        searchEditText = view.findViewById(R.id.searchEditText)
        emptyListPlaceholder = view.findViewById(R.id.emptyListPlaceholder)

        //Создаем экземпляр viewmodel
        viewModel = ViewModelProvider(this, ViewModelFactory(repository, dataSource))[TracksViewModel::class.java]

        // Инициализация адаптера
        adapter = TracksAdapter{ track ->
            navigationProvider.navigateToPlayFragment(adapter.currentList,track)
        }
        val recyclerView: RecyclerView = view.findViewById(R.id.tracksRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        // Подписываемся на обновления в tracks и отображаем их в адаптере
        viewModel.tracks.observe(viewLifecycleOwner) { tracks ->
            adapter.submitList(tracks)  // Загружаем треки в адаптер
            // Управление пустым состоянием
            emptyListPlaceholder.visibility = if (tracks.isNullOrEmpty()) TextView.VISIBLE else TextView.GONE
        }

        viewModel.downloadedTracks.observe(viewLifecycleOwner){tracks->
            adapter.downloadedTracks = tracks
            Log.d("TRACKS DOWNLOADED LIST ADAPTER", adapter.downloadedTracks.toString())
        }

        //Выполняем первичный поиск загруженных треков
        viewModel.searchTracks("")
        viewModel.getDownloadedTracks()

        // Обработчик нажатия на кнопку очистки
        clearButton.setOnClickListener {
            searchEditText.text.clear()  // Очищаем текст в поле
        }

        // Установка слушателя на поле поиска
        val searchEditText: EditText = view.findViewById(R.id.searchEditText)
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Создаем новую задачу с задержкой
                viewModel.viewModelScope.launch {
                    delay(1000)  // Задержка 1000 миллисекунд
                    val query = s.toString()
                    viewModel.searchTracks(query) // Выполняем запрос
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Показываем или скрываем кнопку очистки в зависимости от текста
                clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
            }
        })
    }

    //Забираем из Bundle тип источника данных, по умолчанию ставим LOCAL
    protected fun getDataSourceFromBundle(bundle: Bundle?):DataSource{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle?.getParcelable(ARG_DATA_SOURCE, DataSource::class.java)
        } else {
            @Suppress("DEPRECATION")//API <33
            bundle?.getParcelable(ARG_DATA_SOURCE)
        }?: DataSource.LOCAL
    }

    protected fun setDataSource(dataSource: DataSource){
        this.dataSource = dataSource
    }
}