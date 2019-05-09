package dev.eternalbox.kjukebox

data class InfiniteJukeboxSegment(
        override val confidence: Double,
        val timbre: DoubleArray,
        val pitches: DoubleArray,
        override val start: Double,
        val loudness_max_time: Double,
        val loudness_start: Double,
        override var duration: Double,
        val loudness_max: Double
) : InfiniteJukeboxComponent<InfiniteJukeboxSegment>() {
    override val componentList: Array<InfiniteJukeboxSegment>
        get() = track.analysis.segments
}