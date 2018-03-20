package org.abimon.kjukebox

import java.util.ArrayList
import kotlin.Comparator
import kotlin.math.abs

fun dynamicCalculateNearestNeighbors(array: Array<InfiniteJukeboxComponent<*>>): Int {
    var count = 0
    val targetBranchCount = array.size / 6
    var maxThreshold = 80

    precalculateNearestNeighbors(array)

    for (threshold in 10 until 80 step 5) {
        count = collectNearestNeighbors(array, threshold)
        if (count >= targetBranchCount) {
            maxThreshold = threshold
            break
        }
    }

    postProcessNearestNeighbors(array, maxThreshold)

    precalculateFollowingBranches(array)
    return count
}

fun precalculateFollowingBranches(array: Array<InfiniteJukeboxComponent<*>>) {
    for (i in array.indices) {
        val q1 = array[i]

        for (j in i + 1 until array.size) {
            val q2 = array[j]
            if (q2.neighbors?.isNotEmpty() == true) {
                q1.nextBranchIndex = j
                q1.nextBranchStart = q2.start
                break
            }
        }
    }
}

fun postProcessNearestNeighbors(array: Array<InfiniteJukeboxComponent<*>>, maxThreshold: Int) {
    if (longestBackwardBranch(array) < 50)
        insertBestBackwardBranch(array, maxThreshold, 65)
    else
        insertBestBackwardBranch(array, maxThreshold, 55)

    calculateReachability(array)
    //jukeboxData.lastBranchPoint = findBestLastBeat(type);
    filterOutBadBranches(array, findBestLastBeat(array))
}

fun filterOutBadBranches(array: Array<InfiniteJukeboxComponent<*>>, lastIndex: Int) {
    for (i in 0 until lastIndex) {
        val q = array[i]

        q.neighbors = q.neighbors?.filter { edge -> edge.dest.index.toInt() < lastIndex }?.toTypedArray()
    }
}

fun findBestLastBeat(array: Array<InfiniteJukeboxComponent<*>>): Int {
    val reachThreshold = 50
    var longest = 0
    var longestReach = 0

    for (i in array.indices.reversed()) {
        val q = array[i]

        val distanceToEnd = array.size - i

        // if q is the last quanta, then we can never go past it
        // which limits our reach

        val reach = ((q.reach ?: continue) - distanceToEnd) * 100 / array.size

        if (reach > longestReach && q.neighbors?.isNotEmpty() == true) {
            longestReach = reach
            longest = i

            if (reach >= reachThreshold)
                break
        }
    }

//        jukeboxData.totalBeats = quanta.length;
//        jukeboxData.longestReach = longestReach;

    return longest
}

fun calculateReachability(array: Array<InfiniteJukeboxComponent<*>>) {
    val maxIter = 1000
//        var iter = 0

    for (i in array.indices) {
        val q = array[i]
        q.reach = array.size - q.index.toInt()
    }

    for (iter in 0 until maxIter) {
        var changeCount = 0

        for (qi in array.indices) {
            val q = array[qi]
            var changed = false
            val neighbors = q.neighbors ?: continue

            for (i in neighbors.indices) {
                val q2 = neighbors[i].dest
                if (q2.reach ?: continue > q.reach ?: break) {
                    q.reach = q2.reach
                    changed = true
                }
            }

            if (qi < array.size - 1) {
                val q2 = array[qi + 1]
                if (q2.reach ?: continue > q.reach ?: break) {
                    q.reach = q2.reach
                    changed = true
                }
            }

            if (changed) {
                changeCount++

                for (j in 0 until q.index.toInt()) {
                    val q2 = array[j]
                    if (q2.reach ?: continue < q.reach ?: break) {
                        q.reach = q2.reach
                        changed = true
                    }
                }
            }
        }

        if (changeCount == 0)
            break
    }
}

fun insertBestBackwardBranch(array: Array<InfiniteJukeboxComponent<*>>, threshold: Int, maxThreshold: Int) {
    var found = false
    val branches = ArrayList<InfiniteJukeboxBranch>()

    for (i in array.indices) {
        val q = array[i]
        val allNeighbors = q.allNeighbors ?: continue

        for (j in allNeighbors.indices) {
            val neighbor = allNeighbors[j]

            val which = neighbor.dest.index.toInt()
            val thresh = neighbor.distance

            val delta = i - which

            if (delta > 0 && thresh < maxThreshold) {
                val percent = delta * 100.0 / array.size
                branches.add(InfiniteJukeboxBranch(percent, i, which, q, neighbor))
            }
        }
    }

    if (branches.isEmpty())
        return

    val sorted = branches.sortedBy { branch -> branch.percent }.asReversed()
    val best = sorted[0]
    val bestQ = best.q
    val bestNeighbor = best.neighbor
    val bestThreshold = bestNeighbor.distance

    if (bestThreshold > threshold) {
        val bestNeighbors = bestQ.neighbors
        if (bestNeighbors == null)
            bestQ.neighbors = arrayOf(bestNeighbor)
        else
            bestQ.neighbors = bestNeighbors.plus(bestNeighbor)
    }
}

fun longestBackwardBranch(array: Array<InfiniteJukeboxComponent<*>>): Int {
    /**
     * var longest = 0
    var quanta = track.analysis[type];
    for (var i = 0; i < quanta.length; i++) {
    var q = quanta[i];
    for (var j = 0; j < q.neighbors.length; j++) {
    var neighbor = q.neighbors[j];
    var which = neighbor.dest.which;
    var delta = i - which;
    if (delta > longest) {
    longest = delta;
    }
    }
    }
    var lbb = longest * 100 / quanta.length;
    return lbb;
     */

    var longest = 0
    for (i in array.indices) {
        val q = array[i]
        val neighbors = q.neighbors ?: continue

        for (j in neighbors.indices) {
            val neighbor = neighbors[j]
            val which = neighbor.dest.index.toInt()
            val delta = i - which

            if (delta > longest)
                longest = delta
        }
    }

    return longest * 100 / array.size
}

fun precalculateNearestNeighbors(array: Array<InfiniteJukeboxComponent<*>>) {
    val maxBranches = 4
    val maxBranchThreshold = 80

    if (array[0].allNeighbors != null)
        return

    for (i in array.indices) {
        val q = array[i]
        calculateNearestNeighborsForQuantum(array, q)
    }
}

fun collectNearestNeighbors(array: Array<InfiniteJukeboxComponent<*>>, threshold: Int): Int {
    var branchingCount = 0
    for (i in array.indices) {
        val q = array[i]
        q.neighbors = extractNearestNeighbors(q, threshold)

        if (q.neighbors?.isNotEmpty() == true)
            branchingCount++
    }

    return branchingCount
}

fun extractNearestNeighbors(q: InfiniteJukeboxComponent<*>, threshold: Int): Array<InfiniteJukeboxEdge> {
    val neighbors = ArrayList<InfiniteJukeboxEdge>()
    val allNeighbors = q.allNeighbors ?: return emptyArray()

    for (i in allNeighbors.indices) {
        val neighbor = allNeighbors[i]

        val distance = neighbor.distance
        if (distance <= threshold)
            neighbors.add(neighbor)
    }

    return neighbors.toTypedArray()
}

fun calculateNearestNeighborsForQuantum(array: Array<InfiniteJukeboxComponent<*>>, q1: InfiniteJukeboxComponent<*>) {
    val edges = ArrayList<InfiniteJukeboxEdge>()
    var id = 0

    for (i in array.indices) {
        if (i == q1.index.toInt())
            continue

        val q2 = array[i]
        var sum = 0.0

        for (j in q1.overlappingSegmentUUIDs.indices) {
            val seg1 = q1.overlappingSegment(j)
            var distance = 100.0

            if (j < q2.overlappingSegmentUUIDs.size) {
                val seg2 = q2.overlappingSegment(j)
                // some segments can overlap many quantums,
                // we don't want this self segue, so give them a
                // high distance

                if (seg1.index == seg2.index)
                    distance = 100.0
                else
                    distance = getSegmentDistances(seg1, seg2)
            }

            sum += distance
        }

        val pdistance = if (q1.indexInParent == q2.indexInParent) 0 else 100
        val totalDistance = sum / q1.overlappingSegmentUUIDs.size + pdistance

        if (totalDistance < 80) {
            edges.add(InfiniteJukeboxEdge(q1.track, id, q1.uuid, q2.uuid, totalDistance))
            id++
        }
    }

    edges.sortWith(Comparator { a, b -> a.distance.compareTo(b.distance) })

    q1.allNeighbors = Array(minOf(4, edges.size)) { i -> edges[i] }
}

val timbreWeight = 1
var pitchWeight = 10
val loudStartWeight = 1
var loudMaxWeight = 1
val durationWeight = 100
var confidenceWeight = 1

fun getSegmentDistances(s1: InfiniteJukeboxSegment, s2: InfiniteJukeboxSegment): Double {
    val timbre = getSegmentDistance(s1.timbre, s2.timbre) * timbreWeight
    val pitch = getSegmentDistance(s1.pitches, s2.pitches) * pitchWeight
    val sloudStart = abs(s1.loudness_start - s2.loudness_start) * loudStartWeight
    val sloudMax = abs(s1.loudness_max - s2.loudness_max) * loudMaxWeight
    val duration = abs(s1.duration - s2.duration) * durationWeight
    val confidence = abs(s1.confidence - s2.confidence) * confidenceWeight

    return timbre + pitch
}

fun getSegmentDistance(d1: DoubleArray, d2: DoubleArray): Double {
    var sum = 0.0

    for (i in d1.indices) {
        val delta = d2[i] - d1[i]
        sum += delta * delta
    }

    return Math.sqrt(sum)
}

fun preprocess(track: InfiniteJukeboxTrack) {
    val sections = track.analysis.sectionsArray
    val bars = track.analysis.barsArray
    val beats = track.analysis.beatsArray
    val segments = track.analysis.segmentsArray
    val tatums = track.analysis.tatumsArray

    connect(track, sections)
    connect(track, bars)
    connect(track, beats)
    connect(track, segments)
    connect(track, tatums)

    connectQuanta(track, sections, bars)
    connectQuanta(track, bars, beats)
    connectQuanta(track, beats, tatums)
    connectQuanta(track, tatums, segments)

    connectFirstOverlappingSegment(track, bars)
    connectFirstOverlappingSegment(track, beats)
    connectFirstOverlappingSegment(track, tatums)

    connectAllOverlappingSegments(track, bars)
    connectAllOverlappingSegments(track, beats)
    connectAllOverlappingSegments(track, tatums)

    filterSegments(track)
}

fun connect(track: InfiniteJukeboxTrack, array: Array<InfiniteJukeboxComponent<*>>) {
    for (i in array.indices) {
        val q = array[i]
        q.track = track
        q.index = i
        if (i > 0)
            q.prevUUID = array[i - 1].uuid

        if (i < array.size - 1)
            q.nextUUID = array[i + 1].uuid
    }
}

fun connectQuanta(track: InfiniteJukeboxTrack, parents: Array<InfiniteJukeboxComponent<*>>, children: Array<InfiniteJukeboxComponent<*>>) {
    var last = 0

    for (i in parents.indices) {
        val parent = parents[i]
        parent.childrenUUIDs.clear()

        val parentDurationRange = parent.start..parent.start + parent.duration

        for (j in last until children.size) {
            val child = children[j]

            if (child.start in parentDurationRange) {
                child.parentUUID = parent.uuid
                child.indexInParent = parent.childrenUUIDs.size
                parent.childrenUUIDs.add(child.uuid)
                last = j
            } else if (child.start > parent.start)
                break
        }
    }
}

fun connectFirstOverlappingSegment(track: InfiniteJukeboxTrack, quanta: Array<InfiniteJukeboxComponent<*>>) {
    var last = 0
    val segs = track.analysis.segments

    for (i in quanta.indices) {
        val q = quanta[i]

        for (j in last until segs.size) {
            val seg = segs[j]
            if (seg.start >= q.start) {
                q.osegUUID = seg.uuid
                last = j
                break
            }
        }
    }
}

fun connectAllOverlappingSegments(track: InfiniteJukeboxTrack, quanta: Array<InfiniteJukeboxComponent<*>>) {
    var last = 0
    val segs = track.analysis.segments

    for (i in quanta.indices) {
        val q = quanta[i]
        q.overlappingSegmentUUIDs.clear()

        for (j in last until segs.size) {
            val seg = segs[j]
            if ((seg.start + seg.duration) < q.start)
                continue
            if (seg.start > (q.start + q.duration))
                break

            last = j
            q.overlappingSegmentUUIDs.add(seg.uuid)
        }
    }
}

fun filterSegments(track: InfiniteJukeboxTrack) {
    val threshold = 0.3
    val filteredSegments: MutableList<InfiniteJukeboxSegment> = ArrayList()
    filteredSegments.add(track.analysis.segments[0])

    for (i in 0 until track.analysis.segments.size) {
        val seg = track.analysis.segments[i]
        val last = filteredSegments.last()

        if (isSimilar(seg, last) && seg.confidence < threshold) {
            filteredSegments[filteredSegments.size - 1].duration += seg.duration
        } else {
            filteredSegments.add(seg)
        }
    }

    track.analysis.filteredSegments = filteredSegments.toTypedArray()
}

fun isSimilar(segment1: InfiniteJukeboxSegment, segment2: InfiniteJukeboxSegment): Boolean {
    val threshold = 1

    var sum = 0.0
    val v1 = segment1.timbre
    val v2 = segment2.timbre

    for (i in 0 until 3) {
        val delta = v2[i] - v1[i]
        sum += delta * delta
    }

    return Math.sqrt(sum) < threshold
}

//    function euclidean_distance(v1, v2) {
//        var sum = 0;
//        for (var i = 0; i < 3; i++) {
//            var delta = v2[i] - v1[i];
//            sum += delta * delta;
//        }
//        return Math.sqrt(sum);
//    }
//
//    function timbral_distance(s1, s2) {
//        return euclidean_distance(s1.timbre, s2.timbre);
//    }