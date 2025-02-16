package dev.realism.tracks.di

import dev.realism.data.model.Track

interface NavigationProvider {
    fun navigateToPlayFragment(trackList:List<Track>,track: Track)
}
