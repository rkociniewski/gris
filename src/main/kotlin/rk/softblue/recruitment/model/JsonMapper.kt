package rk.softblue.recruitment.model

import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.TimeZone

/**
 * Utility object providing preconfigured ObjectMapper instances for JSON processing.
 */
object JsonMapper {
    /**
     * Preconfigured ObjectMapper instance optimized for GitHub API responses.
     * This mapper handles various date formats, configures proper indentation,
     * and handles null values appropriately.
     */
    val gitHubResponseMapper: ObjectMapper = jacksonObjectMapper().apply {
        configure(SerializationFeature.INDENT_OUTPUT, true)
        setDefaultPrettyPrinter(DefaultPrettyPrinter().apply {
            indentArraysWith(DefaultPrettyPrinter.FixedSpaceIndenter.instance)
            indentObjectsWith(DefaultIndenter("  ", "\n"))
        })

        registerModule(JavaTimeModule().apply {
            addDeserializer(LocalDateTime::class.java, FlexibleLocalDateTimeDeserializer())
        })
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true)
        configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)

        configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)

        configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
        setDefaultSetterInfo(JsonSetter.Value.forValueNulls(Nulls.AS_EMPTY))

        dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
    }

    /**
     * Custom deserializer for LocalDateTime that attempts multiple date format patterns.
     * This enables the parser to handle various datetime formats returned by the GitHub API.
     */
    private class FlexibleLocalDateTimeDeserializer : JsonDeserializer<LocalDateTime>() {
        private val formatters = listOf(
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            DateTimeFormatter.ISO_ZONED_DATE_TIME
        )

        /**
         * Attempts to parse a datetime string using multiple formatters.
         *
         * @param p The JsonParser containing the text to parse
         * @param ctxt The deserialization context
         * @return The parsed LocalDateTime
         * @throws DeserializationException if the date format is not supported
         */
        override fun deserialize(p: JsonParser, ctxt: DeserializationContext): LocalDateTime {
            return formatters.firstNotNullOfOrNull { formatter ->
                try {
                    LocalDateTime.parse(p.text, formatter)
                } catch (_: DateTimeParseException) {
                    null
                }
            } ?: throw ctxt.weirdStringException(p.text, LocalDateTime::class.java, "Unsupported date format")
        }
    }
}
