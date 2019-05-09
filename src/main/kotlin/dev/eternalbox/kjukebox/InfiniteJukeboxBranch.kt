package dev.eternalbox.kjukebox

import dev.eternalbox.kjukebox.InfiniteJukeboxComponent
import dev.eternalbox.kjukebox.InfiniteJukeboxEdge

data class InfiniteJukeboxBranch(
        val percent: Double,
        val i: Int,
        val which: Int,
        val q: InfiniteJukeboxComponent<*>,
        val neighbor: InfiniteJukeboxEdge
)