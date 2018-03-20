package org.abimon.kjukebox

data class InfiniteJukeboxTatum(
        override var duration: Double,
        override val start: Double,
        override val confidence: Double
) : InfiniteJukeboxComponent<InfiniteJukeboxTatum>() {
    override val componentList: Array<InfiniteJukeboxTatum>
        get() = track.analysis.tatums
}