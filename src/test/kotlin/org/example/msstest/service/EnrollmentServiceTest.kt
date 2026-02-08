package org.example.msstest.service

import org.example.msstest.IntegrationTestBase
import org.example.msstest.domain.entity.Course
import org.example.msstest.domain.entity.CourseSchedule
import org.example.msstest.domain.entity.EnrollmentStatus
import org.example.msstest.domain.entity.Professor
import org.example.msstest.domain.entity.Student
import org.example.msstest.exception.EnrollmentException
import org.example.msstest.repository.CourseRepository
import org.example.msstest.repository.CourseScheduleRepository
import org.example.msstest.repository.EnrollmentRepository
import org.example.msstest.repository.ProfessorRepository
import org.example.msstest.repository.StudentRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import java.time.DayOfWeek
import java.time.LocalTime

@DisplayName("EnrollmentService 테스트")
class EnrollmentServiceTest : IntegrationTestBase() {
    @Autowired
    private lateinit var enrollmentService: EnrollmentService

    @Autowired
    private lateinit var studentRepository: StudentRepository

    @Autowired
    private lateinit var professorRepository: ProfessorRepository

    @Autowired
    private lateinit var courseRepository: CourseRepository

    @Autowired
    private lateinit var courseScheduleRepository: CourseScheduleRepository

    @Autowired
    private lateinit var enrollmentRepository: EnrollmentRepository

    private lateinit var student: Student
    private lateinit var professor: Professor
    private lateinit var course: Course

    @BeforeEach
    fun setup() {
        enrollmentRepository.deleteAll()
        courseScheduleRepository.deleteAll()
        courseRepository.deleteAll()
        studentRepository.deleteAll()
        professorRepository.deleteAll()

        professor = professorRepository.save(Professor.create("P001", "김교수", "컴퓨터공학과"))
        student =
            studentRepository.save(
                Student.create("20240001", "홍길동", "컴퓨터공학과", 1),
            )
        course =
            courseRepository.save(
                Course.create("CS101", "자료구조", 3, 30, professor),
            )
    }

    @Nested
    @DisplayName("수강신청")
    inner class EnrollTests {
        @Test
        @DisplayName("정상 수강신청 성공")
        fun enroll_success() {
            val result = enrollmentService.enroll(student.id, course.id)

            assertNotNull(result)
            assertEquals(student.id, result.studentId)
            assertEquals(course.id, result.courseId)
            assertEquals(EnrollmentStatus.ENROLLED, result.status)

            val updatedCourse = courseRepository.findById(course.id).get()
            assertEquals(1, updatedCourse.currentEnrollment)
        }

        @Test
        @DisplayName("중복 수강신청 시 예외 발생")
        fun enroll_alreadyEnrolled_throws() {
            enrollmentService.enroll(student.id, course.id)

            assertThrows<EnrollmentException.AlreadyEnrolled> {
                enrollmentService.enroll(student.id, course.id)
            }
        }

        @Test
        @DisplayName("존재하지 않는 학생으로 수강신청 시 예외 발생")
        fun enroll_studentNotFound_throws() {
            assertThrows<EnrollmentException.StudentNotFound> {
                enrollmentService.enroll(9999L, course.id)
            }
        }

        @Test
        @DisplayName("존재하지 않는 강좌로 수강신청 시 예외 발생")
        fun enroll_courseNotFound_throws() {
            assertThrows<EnrollmentException.CourseNotFound> {
                enrollmentService.enroll(student.id, 9999L)
            }
        }
    }

    @Nested
    @DisplayName("학점 제한")
    inner class CreditLimitTests {
        @Test
        @DisplayName("18학점 초과 시 수강신청 실패")
        fun enroll_creditLimitExceeded_throws() {
            val courses =
                listOf(
                    courseRepository.save(Course.create("CS102", "알고리즘", 6, 30, professor)),
                    courseRepository.save(Course.create("CS103", "운영체제", 6, 30, professor)),
                    courseRepository.save(Course.create("CS104", "네트워크", 6, 30, professor)),
                )

            courses.forEach { c ->
                enrollmentService.enroll(student.id, c.id)
            }

            val currentCredits = enrollmentRepository.sumCreditsByStudentId(student.id)
            assertEquals(18, currentCredits)

            val additionalCourse =
                courseRepository.save(
                    Course.create("CS105", "데이터베이스", 3, 30, professor),
                )

            assertThrows<EnrollmentException.CreditLimitExceeded> {
                enrollmentService.enroll(student.id, additionalCourse.id)
            }
        }

        @Test
        @DisplayName("정확히 18학점까지는 수강신청 가능")
        fun enroll_exactlyMaxCredits_success() {
            val course6 = courseRepository.save(Course.create("CS102", "알고리즘", 6, 30, professor))
            val course9 = courseRepository.save(Course.create("CS103", "운영체제", 6, 30, professor))
            val course6b = courseRepository.save(Course.create("CS104", "네트워크", 6, 30, professor))

            enrollmentService.enroll(student.id, course6.id)
            enrollmentService.enroll(student.id, course9.id)
            enrollmentService.enroll(student.id, course6b.id)

            val currentCredits = enrollmentRepository.sumCreditsByStudentId(student.id)
            assertEquals(18, currentCredits)
        }
    }

    @Nested
    @DisplayName("시간표 중복")
    inner class ScheduleConflictTests {
        @Test
        @DisplayName("시간표 중복 시 수강신청 실패")
        fun enroll_scheduleConflict_throws() {
            val schedule1 =
                CourseSchedule.create(
                    course = course,
                    dayOfWeek = DayOfWeek.MONDAY,
                    startTime = LocalTime.of(10, 0),
                    endTime = LocalTime.of(12, 0),
                    location = "공학관 101",
                )
            courseScheduleRepository.save(schedule1)

            enrollmentService.enroll(student.id, course.id)

            val course2 = courseRepository.save(Course.create("CS102", "알고리즘", 3, 30, professor))
            val schedule2 =
                CourseSchedule.create(
                    course = course2,
                    dayOfWeek = DayOfWeek.MONDAY,
                    startTime = LocalTime.of(11, 0),
                    endTime = LocalTime.of(13, 0),
                    location = "공학관 102",
                )
            courseScheduleRepository.save(schedule2)

            assertThrows<EnrollmentException.ScheduleConflict> {
                enrollmentService.enroll(student.id, course2.id)
            }
        }

        @Test
        @DisplayName("경계 시간이 맞닿으면 중복 아님 (11:00 종료, 11:00 시작)")
        fun enroll_boundaryTime_noConflict() {
            val schedule1 =
                CourseSchedule.create(
                    course = course,
                    dayOfWeek = DayOfWeek.MONDAY,
                    startTime = LocalTime.of(9, 0),
                    endTime = LocalTime.of(11, 0),
                    location = "공학관 101",
                )
            courseScheduleRepository.save(schedule1)

            enrollmentService.enroll(student.id, course.id)

            val course2 = courseRepository.save(Course.create("CS102", "알고리즘", 3, 30, professor))
            val schedule2 =
                CourseSchedule.create(
                    course = course2,
                    dayOfWeek = DayOfWeek.MONDAY,
                    startTime = LocalTime.of(11, 0),
                    endTime = LocalTime.of(13, 0),
                    location = "공학관 102",
                )
            courseScheduleRepository.save(schedule2)

            val result = enrollmentService.enroll(student.id, course2.id)
            assertNotNull(result)
        }
    }

    @Nested
    @DisplayName("수강취소")
    inner class CancelTests {
        @Test
        @DisplayName("정상 수강취소 성공")
        fun cancel_success() {
            enrollmentService.enroll(student.id, course.id)

            val result = enrollmentService.cancel(student.id, course.id)

            assertEquals(EnrollmentStatus.CANCELLED, result.status)

            val updatedCourse = courseRepository.findById(course.id).get()
            assertEquals(0, updatedCourse.currentEnrollment)
        }

        @Test
        @DisplayName("수강신청하지 않은 강좌 취소 시 예외 발생")
        fun cancel_notEnrolled_throws() {
            assertThrows<EnrollmentException.EnrollmentNotFound> {
                enrollmentService.cancel(student.id, course.id)
            }
        }
    }
}
