package dev.eternalbox.kjukebox

data class InfiniteJukeboxBeat(
        override var duration: Double,
        override val start: Double,
        override val confidence: Double
) : InfiniteJukeboxComponent<InfiniteJukeboxBeat>() {
    override val componentList: Array<InfiniteJukeboxBeat>
        get() = track.analysis.beats
}