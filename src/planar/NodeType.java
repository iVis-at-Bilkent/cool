public enum NodeType {
    /**
     * Normal graph nodes. Most nodes are of this type.
     */
    NORMAL,

    /**
     * A node that represents a face, normally used in network graphs.
     */
    FACE,

    /**
     * Compound nodes. Compound nodes are nodes that again represent graphs. They contain nodes
     * and edges themselves, and may even contain other compound nodes.
     */
    COMPOUND
}