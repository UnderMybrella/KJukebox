package org.abimon.kjukebox

data class InfiniteJukeboxSection(
        override val confidence: Double,
        val mode_confidence: Double,
        val time_signature: Int,
        val key_confidence: Double,
        val tempo: Double,
        val time_signature_confidence: Double,
        override val start: Double,
        val tempo_confidence: Double,
        val mode: Int,
        val key: Int,
        override var duration: Double,
        val loudness: Double
) : InfiniteJukeboxComponent<InfiniteJukeboxSection>() {
    override val componentList: Array<InfiniteJukeboxSection>
        get() = track.analysis.sections
}