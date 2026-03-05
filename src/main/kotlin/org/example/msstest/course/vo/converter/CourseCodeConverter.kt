package org.example.msstest.course.vo.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import org.example.msstest.course.vo.CourseCode

@Converter(autoApply = false)
class CourseCodeConverter : AttributeConverter<CourseCode, String> {
    override fun convertToDatabaseColumn(attribute: CourseCode?): String? = attribute?.value

    override fun convertToEntityAttribute(dbData: String?): CourseCode? = dbData?.let { CourseCode(it) }
}
