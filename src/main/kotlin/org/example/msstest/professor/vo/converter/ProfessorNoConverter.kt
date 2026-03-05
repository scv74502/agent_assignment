package org.example.msstest.professor.vo.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import org.example.msstest.professor.vo.ProfessorNo

@Converter(autoApply = false)
class ProfessorNoConverter : AttributeConverter<ProfessorNo, String> {
    override fun convertToDatabaseColumn(attribute: ProfessorNo?): String? = attribute?.value

    override fun convertToEntityAttribute(dbData: String?): ProfessorNo? = dbData?.let { ProfessorNo(it) }
}
