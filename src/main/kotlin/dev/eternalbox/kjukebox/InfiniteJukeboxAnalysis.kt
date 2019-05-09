package dev.eternalbox.kjukebox

data class InfiniteJukeboxAnalysis(
        val sections: Array<InfiniteJukeboxSection>,
        val bars: Array<InfiniteJukeboxBar>,
        val beats: Array<InfiniteJukeboxBeat>,
        val segments: Array<InfiniteJukeboxSegment>,
        val tatums: Array<InfiniteJukeboxTatum>
) {
    val sectionsArray = sections.map { section -> section as InfiniteJukeboxComponent<*> }.toTypedArray()
    val barsArray = bars.map { bar -> bar as InfiniteJukeboxComponent<*> }.toTypedArray()
    val beatsArray = beats.map { beat -> beat as InfiniteJukeboxComponent<*> }.toTypedArray()
    val segmentsArray = segments.map { segment -> segment as InfiniteJukeboxComponent<*> }.toTypedArray()
    val tatumsArray = tatums.map { tatum -> tatum as InfiniteJukeboxComponent<*> }.toTypedArray()

    lateinit var filteredSegments: Array<InfiniteJukeboxSegment>
}