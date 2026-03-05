package org.example.msstest.student.vo.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import org.example.msstest.student.vo.StudentNo

@Converter(autoApply = false)
class StudentNoConverter : AttributeConverter<StudentNo, String> {
    override fun convertToDatabaseColumn(attribute: StudentNo?): String? = attribute?.value

    override fun convertToEntityAttribute(dbData: String?): StudentNo? = dbData?.let { StudentNo(it) }
}
