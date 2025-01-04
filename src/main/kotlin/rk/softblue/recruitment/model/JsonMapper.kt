package rk.softblue.recruitment.model

import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.text.SimpleDateFormat

object JsonMapper {
    val gitHubResponseMapper: ObjectMapper = jacksonObjectMapper()

    init {
        gitHubResponseMapper.configure(SerializationFeature.INDENT_OUTPUT, true)
        gitHubResponseMapper.setDefaultPrettyPrinter(DefaultPrettyPrinter().apply {
            indentArraysWith(DefaultPrettyPrinter.FixedSpaceIndenter.instance)
            indentObjectsWith(DefaultIndenter("  ", "\n"))
        })
        gitHubResponseMapper.registerModule(JavaTimeModule())
        gitHubResponseMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        gitHubResponseMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        gitHubResponseMapper.setDateFormat(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
    }
}