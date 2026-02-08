package org.example.msstest.domain.vo.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import org.example.msstest.domain.vo.ProfessorNo

@Converter(autoApply = false)
class ProfessorNoConverter : AttributeConverter<ProfessorNo, String> {
    override fun convertToDatabaseColumn(attribute: ProfessorNo?): String? = attribute?.value

    override fun convertToEntityAttribute(dbData: String?): ProfessorNo? = dbData?.let { ProfessorNo(it) }
}
