package com.rtree.core.rtree;

public enum InternalStructure {

    /**
     * <p>
     * Tree structure where nodes are stored in a single linear array and
     * extracted on demand. This is a memory saving option (work on one-third
     * the space) with some access performance penalties compared to the DEFAULT
     * structure (assume one-tenth for search throughput).
     * <p>
     * <p>
     * Suited most for a static dataSet loaded from storage because additions
     * and deletions are not made to the single underlying array but rather
     * combines the array with an object graph. Deletions are not recovered from
     * the single array and after enough of them happen the dataStructure should
     * be rewritten to a new array to recover space.
     */
    SINGLE_ARRAY,

    /**
     * Tree structure using an object graph. Offers maximal performance for
     * search/addition/deletion but consumes more memory than SINGLE_ARRAY.
     */
    DEFAULT;

}
