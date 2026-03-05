package org.example.msstest.service

import org.example.msstest.IntegrationTestBase
import org.example.msstest.domain.entity.Course
import org.example.msstest.domain.entity.CourseType
import org.example.msstest.domain.entity.EnrollmentStatus
import org.example.msstest.domain.entity.Professor
import org.example.msstest.domain.entity.Student
import org.example.msstest.repository.CourseRepository
import org.example.msstest.repository.EnrollmentRepository
import org.example.msstest.repository.ProfessorRepository
import org.example.msstest.repository.StudentRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.CountDownLatch
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

@DisplayName("수강신청 동시성 통합 테스트")
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class EnrollmentServiceConcurrencyTest : IntegrationTestBase() {
    @Autowired
    private lateinit var enrollmentService: EnrollmentService

    @Autowired
    private lateinit var studentRepository: StudentRepository

    @Autowired
    private lateinit var professorRepository: ProfessorRepository

    @Autowired
    private lateinit var courseRepository: CourseRepository

    @Autowired
    private lateinit var enrollmentRepository: EnrollmentRepository

    private lateinit var professor: Professor
    private lateinit var course: Course
    private lateinit var students: List<Student>

    @AfterEach
    fun cleanup() {
        enrollmentRepository.deleteAll()
        courseRepository.deleteAll()
        studentRepository.deleteAll()
        professorRepository.deleteAll()
    }

    @BeforeEach
    fun setup() {
        enrollmentRepository.deleteAll()
        courseRepository.deleteAll()
        studentRepository.deleteAll()
        professorRepository.deleteAll()

        professor = professorRepository.save(Professor.create("P001", "김교수", "컴퓨터공학과"))

        course =
            courseRepository.save(
                Course.create(
                    courseCode = "CS101",
                    courseName = "자료구조",
                    credits = 3,
                    capacity = 1,
                    professor = professor,
                    courseType = CourseType.MAJOR_REQUIRED,
                    department = "컴퓨터공학과",
                ),
            )

        students =
            (1..100).map { i ->
                studentRepository.save(
                    Student.create(
                        studentNo = "2024%05d".format(i),
                        name = "학생$i",
                        department = "컴퓨터공학과",
                        grade = 1,
                    ),
                )
            }
    }

    @Test
    @DisplayName("100명 동시 수강신청 시 정원 1명인 강좌에 1명만 성공")
    fun concurrentEnrollment_onlyOneSucceeds() {
        val threadCount = 100
        val executor = Executors.newFixedThreadPool(threadCount)
        val barrier = CyclicBarrier(threadCount)
        val latch = CountDownLatch(threadCount)
        val successCount = AtomicInteger(0)
        val failCount = AtomicInteger(0)

        students.forEach { student ->
            executor.submit {
                try {
                    barrier.await()
                    enrollmentService.enroll(student.id, course.id)
                    successCount.incrementAndGet()
                } catch (e: Exception) {
                    failCount.incrementAndGet()
                } finally {
                    latch.countDown()
                }
            }
        }

        latch.await()
        executor.shutdown()

        val enrolledCount =
            enrollmentRepository.countByCourseIdAndStatus(
                course.id,
                EnrollmentStatus.ENROLLED,
            )

        assertEquals(1, successCount.get(), "성공한 수강신청은 1건이어야 함")
        assertEquals(99, failCount.get(), "실패한 수강신청은 99건이어야 함")
        assertEquals(1L, enrolledCount, "DB에 등록된 수강신청은 1건이어야 함")

        val updatedCourse = courseRepository.findById(course.id).get()
        assertEquals(1, updatedCourse.currentEnrollment, "현재 수강인원은 1명이어야 함")
    }

    @Test
    @DisplayName("50명 동시 수강신청 시 정원 30명인 강좌에 30명만 성공")
    fun concurrentEnrollment_thirtySucceed() {
        enrollmentRepository.deleteAll()
        courseRepository.deleteAll()
        val course30 =
            courseRepository.save(
                Course.create(
                    courseCode = "CS102",
                    courseName = "알고리즘",
                    credits = 3,
                    capacity = 30,
                    professor = professor,
                    courseType = CourseType.MAJOR_REQUIRED,
                    department = "컴퓨터공학과",
                ),
            )

        val threadCount = 50
        val executor = Executors.newFixedThreadPool(threadCount)
        val barrier = CyclicBarrier(threadCount)
        val latch = CountDownLatch(threadCount)
        val successCount = AtomicInteger(0)
        val failCount = AtomicInteger(0)

        students.take(50).forEach { student ->
            executor.submit {
                try {
                    barrier.await()
                    enrollmentService.enroll(student.id, course30.id)
                    successCount.incrementAndGet()
                } catch (e: Exception) {
                    failCount.incrementAndGet()
                } finally {
                    latch.countDown()
                }
            }
        }

        latch.await()
        executor.shutdown()

        val enrolledCount =
            enrollmentRepository.countByCourseIdAndStatus(
                course30.id,
                EnrollmentStatus.ENROLLED,
            )

        assertEquals(30, successCount.get(), "성공한 수강신청은 30건이어야 함")
        assertEquals(20, failCount.get(), "실패한 수강신청은 20건이어야 함")
        assertEquals(30L, enrolledCount, "DB에 등록된 수강신청은 30건이어야 함")

        val updatedCourse = courseRepository.findById(course30.id).get()
        assertEquals(30, updatedCourse.currentEnrollment, "현재 수강인원은 30명이어야 함")
    }

    @Test
    @DisplayName("동일 학생이 서로 다른 2과목 동시 수강신청 시 학점 초과 방지")
    fun concurrentEnrollment_sameStudent_creditLimitEnforced() {
        enrollmentRepository.deleteAll()
        courseRepository.deleteAll()

        val student = students[0]

        val preEnrolledCourses =
            (1..5).map { i ->
                courseRepository.save(
                    Course.create(
                        courseCode = "PRE%03d".format(i),
                        courseName = "기수강과목$i",
                        credits = 3,
                        capacity = 100,
                        professor = professor,
                        courseType = CourseType.MAJOR_REQUIRED,
                        department = "컴퓨터공학과",
                    ),
                )
            }

        preEnrolledCourses.forEach { c ->
            enrollmentService.enroll(student.id, c.id)
        }

        val currentCredits = enrollmentRepository.sumCreditsByStudentId(student.id)
        assertEquals(15, currentCredits, "사전 수강 학점은 15학점이어야 함")

        val extraCourseA =
            courseRepository.save(
                Course.create(
                    courseCode = "EXT001",
                    courseName = "추가과목A",
                    credits = 3,
                    capacity = 100,
                    professor = professor,
                    courseType = CourseType.MAJOR_ELECTIVE,
                    department = "컴퓨터공학과",
                ),
            )
        val extraCourseB =
            courseRepository.save(
                Course.create(
                    courseCode = "EXT002",
                    courseName = "추가과목B",
                    credits = 3,
                    capacity = 100,
                    professor = professor,
                    courseType = CourseType.MAJOR_ELECTIVE,
                    department = "컴퓨터공학과",
                ),
            )

        val threadCount = 2
        val executor = Executors.newFixedThreadPool(threadCount)
        val barrier = CyclicBarrier(threadCount)
        val latch = CountDownLatch(threadCount)
        val successCount = AtomicInteger(0)
        val failCount = AtomicInteger(0)

        val coursesToEnroll = listOf(extraCourseA, extraCourseB)
        coursesToEnroll.forEach { c ->
            executor.submit {
                try {
                    barrier.await()
                    enrollmentService.enroll(student.id, c.id)
                    successCount.incrementAndGet()
                } catch (e: Exception) {
                    failCount.incrementAndGet()
                } finally {
                    latch.countDown()
                }
            }
        }

        latch.await()
        executor.shutdown()

        assertEquals(1, successCount.get(), "성공한 수강신청은 1건이어야 함")
        assertEquals(1, failCount.get(), "실패한 수강신청은 1건이어야 함")

        val finalCredits = enrollmentRepository.sumCreditsByStudentId(student.id)
        assertEquals(18, finalCredits, "최종 학점은 18학점이어야 함")
        assertTrue(finalCredits <= 18, "학점 제한(18)을 초과하면 안 됨")
    }
}
