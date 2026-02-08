package org.example.msstest.domain.vo.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import org.example.msstest.domain.vo.Credits

@Converter(autoApply = false)
class CreditsConverter : AttributeConverter<Credits, Int> {
    override fun convertToDatabaseColumn(attribute: Credits?): Int? = attribute?.value

    override fun convertToEntityAttribute(dbData: Int?): Credits? = dbData?.let { Credits(it) }
}
