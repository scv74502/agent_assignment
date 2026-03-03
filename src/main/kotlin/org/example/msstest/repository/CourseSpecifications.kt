package org.example.msstest.repository

import jakarta.persistence.criteria.JoinType
import org.example.msstest.domain.entity.Course
import org.example.msstest.domain.entity.CourseType
import org.springframework.data.jpa.domain.Specification

object CourseSpecifications {
    fun cursorAfter(cursor: Long?): Specification<Course> =
        Specification { root, _, cb ->
            cursor?.let { cb.greaterThan(root.get("id"), it) }
        }

    fun departmentEquals(department: String?): Specification<Course> =
        Specification { root, _, cb ->
            department?.let { cb.equal(root.get<String>("department"), it) }
        }

    fun courseTypeEquals(courseType: CourseType?): Specification<Course> =
        Specification { root, _, cb ->
            courseType?.let { cb.equal(root.get<CourseType>("courseType"), it) }
        }

    fun isAvailable(): Specification<Course> =
        Specification { root, _, cb ->
            cb.lessThan(root.get<Int>("currentEnrollment"), root.get<Int>("capacity"))
        }

    fun fetchProfessor(): Specification<Course> =
        Specification { root, query, _ ->
            if (query?.resultType != Long::class.java && query?.resultType != java.lang.Long::class.java) {
                root.fetch<Any, Any>("professor", JoinType.INNER)
            }
            null
        }
}
