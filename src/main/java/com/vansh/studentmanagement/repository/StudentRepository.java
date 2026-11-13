package com.vansh.studentmanagement.repository;

import com.vansh.studentmanagement.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    /** Case-insensitive course lookup for GET /api/students/course/{course}. */
    List<Student> findByCourseIgnoreCase(String course);

    Optional<Student> findByEmail(String email);

    boolean existsByEmail(String email);
}
