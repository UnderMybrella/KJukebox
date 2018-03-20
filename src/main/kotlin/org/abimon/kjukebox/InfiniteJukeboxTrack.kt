package org.abimon.kjukebox

data class InfiniteJukeboxTrack(
        val info: InfiniteJukeboxInfo,
        val analysis: InfiniteJukeboxAnalysis,
        val summary: EternalJukeboxSummary?
) {
    fun process() {
        preprocess(this)
        dynamicCalculateNearestNeighbors(analysis.beatsArray)
    }
}