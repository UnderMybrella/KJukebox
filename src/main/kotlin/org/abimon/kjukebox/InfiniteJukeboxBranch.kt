package org.abimon.kjukebox

import org.abimon.kjukebox.InfiniteJukeboxComponent
import org.abimon.kjukebox.InfiniteJukeboxEdge

data class InfiniteJukeboxBranch(
        val percent: Double,
        val i: Int,
        val which: Int,
        val q: InfiniteJukeboxComponent<*>,
        val neighbor: InfiniteJukeboxEdge
)