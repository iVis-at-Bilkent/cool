public enum OrthogonalAngle {
    /** A 90 degree angle, or a left turn. */
    LEFT,

    /** A 180 degree angle, or a straight line. */
    STRAIGHT,

    /** A 270 degree angle, or a right turn. */
    RIGHT,

    /** A 360 degree angle, or full circle. */
    FULL;

    /**
     * Maps ordinal to the enum.
     * 
     * @param angleInt
     *            angle as ordinal.
     * @return angle to the given ordinal.
     */
    public static OrthogonalAngle map(final int angleInt) {
        switch (angleInt) {
        case 0:
            return LEFT;
        case 1:
            return STRAIGHT;
        case 2:
            return RIGHT;
            // CHECKSTYLEOFF MagicNumber
        case 3:
            // CHECKSTYLEON MagicNumber
            return FULL;
        default:
            throw new IllegalArgumentException(
                    "Orthogonal Represenstation: the mapping fails because"
                            + "of an unknown angle int: " + angleInt);
        }
    }
}