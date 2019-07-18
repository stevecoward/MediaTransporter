package io.sugarstack.mediatransporter

import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

/**
 * Holds logger methods for various debug levels
 */
class TransportLogger {
    fun trace(subject: String) = logger.trace { subject }
    fun debug(subject: String) = logger.debug { subject }
    fun info(subject: String) = logger.info { subject }
    fun warn(subject: String) = logger.warn { subject }
    fun error(subject: String) = logger.error { subject }
}