package org.abimon.kjukebox

import java.util.*

abstract class InfiniteJukeboxComponent<SELF : InfiniteJukeboxComponent<SELF>> {
    val uuid: String = UUID.randomUUID().toString()

    lateinit var track: InfiniteJukeboxTrack
    lateinit var index: Number
    abstract val componentList: Array<SELF>

    abstract var duration: Double
    abstract val start: Double
    abstract val confidence: Double

    var prevUUID: String? = null
    var nextUUID: String? = null

    val childrenUUIDs: MutableList<String> = ArrayList()
    val overlappingSegmentUUIDs: MutableList<String> = ArrayList()
    var neighbors: Array<InfiniteJukeboxEdge>? = null
    var allNeighbors: Array<InfiniteJukeboxEdge>? = null
    var nextBranchIndex: Int? = null
    var nextBranchStart: Double? = null

    var parentUUID: String? = null
    var indexInParent: Int? = null

    var osegUUID: String? = null

    var reach: Int? = null

    val prev: SELF?
        get() = if (prevUUID == null) null else componentList.firstOrNull { bar -> bar.uuid == uuid }

    val next: SELF?
        get() = if (nextUUID == null) null else componentList.firstOrNull { bar -> bar.uuid == uuid }

    val children: Array<InfiniteJukeboxComponent<*>>
        get() = childrenUUIDs.mapNotNull { uuid ->
            track.analysis.sections.firstOrNull { section -> section.uuid == uuid }
                    ?: track.analysis.bars.firstOrNull { bar -> bar.uuid == uuid }
                    ?: track.analysis.beats.firstOrNull { beat -> beat.uuid == uuid }
                    ?: track.analysis.segments.firstOrNull { segment -> segment.uuid == uuid }
                    ?: track.analysis.tatums.firstOrNull { tatum -> tatum.uuid == uuid }
        }.toTypedArray()

    val parent: InfiniteJukeboxComponent<*>
        get() = track.analysis.sections.firstOrNull { section -> section.uuid == parentUUID }
                ?: track.analysis.bars.firstOrNull { bar -> bar.uuid == parentUUID }
                ?: track.analysis.beats.firstOrNull { beat -> beat.uuid == parentUUID }
                ?: track.analysis.segments.firstOrNull { segment -> segment.uuid == parentUUID }
                ?: track.analysis.tatums.firstOrNull { tatum -> tatum.uuid == parentUUID }
                ?: throw IllegalStateException("$parentUUID does not match up to any components we have!")

    val overlappingSegments: Array<InfiniteJukeboxSegment>
        get() = overlappingSegmentUUIDs.mapNotNull { uuid -> track.analysis.segments.firstOrNull { segment -> segment.uuid == uuid } }.toTypedArray()

    fun overlappingSegment(index: Int): InfiniteJukeboxSegment = track.analysis.segments.first { segment -> segment.uuid == overlappingSegmentUUIDs[index] }
}