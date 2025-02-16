package dev.realism.tracks

import android.os.Bundle
import android.view.View
import android.widget.EditText
import dev.realism.data.DataSource

class DownloadedTracksFragment : BaseTracksFragment() {
    private lateinit var searchEditText: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setDataSource(DataSource.LOCAL)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Обновляем подсказку в строке поиска
        searchEditText = view.findViewById(R.id.searchEditText)
        searchEditText.hint = getString(R.string.track_downloaded_search_hint)
    }
}


