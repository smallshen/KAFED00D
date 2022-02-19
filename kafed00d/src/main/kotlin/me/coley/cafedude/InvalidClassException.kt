package me.coley.cafedude

/**
 * Exception type caused by IO actions on class files.
 *
 * @author Matt Coley
 */
class InvalidClassException : Exception {
    /**
     * @param msg
     * Cause message.
     */
    constructor(msg: String?) : super(msg)

    /**
     * @param t
     * Cause exception.
     */
    constructor(t: Throwable?) : super(t)
}