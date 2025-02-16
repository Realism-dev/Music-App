package dev.realism.tracks.di

import dev.realism.tracks.BaseTracksFragment
import dev.realism.tracks.PlayFragment

interface TracksComponent {
    fun injectBaseTracksFragment(baseTracksFragment: BaseTracksFragment)
    fun injectPlayFragment(playFragment: PlayFragment)
}