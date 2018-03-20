package org.abimon.kjukebox

data class InfiniteJukeboxEdge(
        val track: InfiniteJukeboxTrack,
        val id: Int,
        val srcUUID: String,
        val destUUID: String,
        val distance: Double
) {
    val src: InfiniteJukeboxComponent<*>
        get() = track.analysis.sections.firstOrNull { section -> section.uuid == srcUUID }
                ?: track.analysis.bars.firstOrNull { bar -> bar.uuid == srcUUID }
                ?: track.analysis.beats.firstOrNull { beat -> beat.uuid == srcUUID }
                ?: track.analysis.segments.firstOrNull { segment -> segment.uuid == srcUUID }
                ?: track.analysis.tatums.firstOrNull { tatum -> tatum.uuid == srcUUID }
                ?: throw IllegalStateException("$srcUUID does not match up to any components we have!")

    val dest: InfiniteJukeboxComponent<*>
        get() = track.analysis.sections.firstOrNull { section -> section.uuid == destUUID }
                ?: track.analysis.bars.firstOrNull { bar -> bar.uuid == destUUID }
                ?: track.analysis.beats.firstOrNull { beat -> beat.uuid == destUUID }
                ?: track.analysis.segments.firstOrNull { segment -> segment.uuid == destUUID }
                ?: track.analysis.tatums.firstOrNull { tatum -> tatum.uuid == destUUID }
                ?: throw IllegalStateException("$destUUID does not match up to any components we have!")
}