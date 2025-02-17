package dev.realism.tracks


import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import dev.realism.data.TracksRepository
import dev.realism.data.model.Track
import dev.realism.tracks.di.TracksComponentProvider
import javax.inject.Inject

class PlayFragment : Fragment(R.layout.fragment_play) {

    @Inject
    lateinit var repository: TracksRepository
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var playViewModel: PlayViewModel

    private lateinit var trackImage: ImageView
    private lateinit var trackTitle: TextView
    private lateinit var trackArtist: TextView
    private lateinit var progressBar: SeekBar
    private lateinit var currentTime: TextView
    private lateinit var playPauseButton: ImageButton
    private lateinit var previousTrackButton: ImageButton
    private lateinit var nextTrackButton: ImageButton
    private lateinit var downloadButton: ImageButton
    private lateinit var loadingProgress:ProgressBar

    private var trackList: List<Track> = emptyList()
    private var currentTrackIndex: Int = 0
    private var currentTrack: Track? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context.applicationContext as TracksComponentProvider)
            .getTracksComponent()
            .injectPlayFragment(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getTracksFromBundle(arguments)

        // Получаем ViewModel
        playViewModel = ViewModelProvider(this, viewModelFactory)[PlayViewModel::class.java]

        // Привязка элементов
        trackImage = view.findViewById(R.id.trackImage)
        trackTitle = view.findViewById(R.id.trackTitlePlay)
        trackArtist = view.findViewById(R.id.trackArtistPlay)
        progressBar = view.findViewById(R.id.progressBar)
        currentTime = view.findViewById(R.id.currentTime)
        playPauseButton = view.findViewById(R.id.playPauseButton)
        previousTrackButton = view.findViewById(R.id.previousTrack)
        nextTrackButton = view.findViewById(R.id.nextTrack)
        downloadButton = view.findViewById(R.id.downloadButton)
        loadingProgress = view.findViewById(R.id.loadingProgress)


        //Настройка перемотки
        progressBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Если изменение происходит от пользователя (не от автоматического обновления)
                if (fromUser) {
                    playViewModel.seekToProgress(progress) // Устанавливаем позицию в MediaPlayer
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Наблюдаем за изменениями в ViewModel
        playViewModel.trackTitle.observe(viewLifecycleOwner) {
            trackTitle.text = it
        }

        playViewModel.trackArtist.observe(viewLifecycleOwner)  {
            trackArtist.text = it
        }

        playViewModel.trackImage.observe(viewLifecycleOwner)  {imageUrl->
            Glide.with(requireContext())
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .into(trackImage)
        }

        playViewModel.currentTime.observe(viewLifecycleOwner)  {
            currentTime.text = it
        }

        playViewModel.progress.observe(viewLifecycleOwner)  {
            progressBar.progress = it
        }

        playViewModel.duration.observe(viewLifecycleOwner)  {
            progressBar.max = it
        }

        playViewModel.isPlaying.observe(viewLifecycleOwner) {
            if (it) {
                playPauseButton.setImageResource(R.drawable.ic_pause)
            } else {
                playPauseButton.setImageResource(R.drawable.ic_play)
            }
        }

        // Здесь проверяем, скачан ли трек или нет. Если нет, показываем кнопку скачивания
        playViewModel.isDownLoaded.observe(viewLifecycleOwner) { isDownloaded->
            if (isDownloaded)
                downloadButton.visibility = View.GONE
            else
                downloadButton.visibility = View.VISIBLE
        }

        // Здесь проверяем, скачивается ли трек. Если нет, показываем индикатор скачивания
        playViewModel.isDownLoading.observe(viewLifecycleOwner) { isDownloading->
            if (isDownloading)
                loadingProgress.visibility = View.VISIBLE
            else
                loadingProgress.visibility = View.GONE
        }

        downloadButton.setOnClickListener {
            playViewModel.downloadCurrentTrack(currentTrack!!)
        }

        // Инициализация медиаплеера и других действий
        playPauseButton.setOnClickListener {
            playViewModel.togglePlayPause()
        }

        nextTrackButton.setOnClickListener {
            playViewModel.playNextTrack()
        }

        previousTrackButton.setOnClickListener {
            playViewModel.playPreviousTrack()
        }

        // Загрузка списка треков
        playViewModel.setTrackList(trackList,currentTrackIndex)

        // Устанавливаем трек
        playViewModel.loadTrack(trackList[currentTrackIndex])
    }

    override fun onDestroyView() {
        super.onDestroyView()
        playViewModel.clear() // Очистка ресурсов
    }




    @Suppress("DEPRECATION")
    private fun getTracksFromBundle(bundle: Bundle?){
        var track: Track? = null
        var trackListFromBundle: List<Track> = emptyList()

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.TIRAMISU){
            if (bundle != null) {
                track = bundle.getParcelable(ARG_TRACK, Track::class.java)
                trackListFromBundle = bundle.getParcelable(ARG_TRACK_LIST, Array<Track>::class.java)?.toList()
                    ?: emptyList()
            }
        }
        else {
            if (bundle != null) {
                track = bundle.getParcelable(ARG_TRACK)
                trackListFromBundle = bundle.getParcelableArray(ARG_TRACK_LIST)?.filterIsInstance<Track>() ?: emptyList()
            }
        }

        if (trackListFromBundle.isNotEmpty() && track != null)
        {
            var index = trackListFromBundle.indexOf(track)
            if(index==-1) index = 0
            currentTrackIndex = index
            trackList = trackListFromBundle
            currentTrack = track
        }
    }
}
