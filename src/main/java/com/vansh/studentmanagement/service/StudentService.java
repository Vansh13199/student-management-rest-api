package com.vansh.studentmanagement.service;

import com.vansh.studentmanagement.exception.DuplicateEmailException;
import com.vansh.studentmanagement.exception.StudentNotFoundException;
import com.vansh.studentmanagement.model.Student;
import com.vansh.studentmanagement.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business logic for student records. All "not found" cases throw
 * {@link StudentNotFoundException}, which the global exception handler
 * maps to HTTP 404.
 */
@Service
@Transactional
public class StudentService {

    private final StudentRepository repository;

    public StudentService(StudentRepository repository) {
        this.repository = repository;
    }

    public Student createStudent(Student student) {
        if (repository.existsByEmail(student.getEmail())) {
            throw new DuplicateEmailException(
                    "A student with email '" + student.getEmail() + "' already exists");
        }
        return repository.save(student);
    }

    @Transactional(readOnly = true)
    public List<Student> getAllStudents() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Student getStudentById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(
                        "Student not found with id: " + id));
    }

    public Student updateStudent(Long id, Student updated) {
        Student existing = getStudentById(id);

        if (!existing.getEmail().equals(updated.getEmail())
                && repository.existsByEmail(updated.getEmail())) {
            throw new DuplicateEmailException(
                    "A student with email '" + updated.getEmail() + "' already exists");
        }

        existing.setName(updated.getName());
        existing.setEmail(updated.getEmail());
        existing.setCourse(updated.getCourse());
        existing.setAge(updated.getAge());
        return repository.save(existing);
    }

    public void deleteStudent(Long id) {
        Student existing = getStudentById(id);
        repository.delete(existing);
    }

    @Transactional(readOnly = true)
    public List<Student> getStudentsByCourse(String course) {
        return repository.findByCourseIgnoreCase(course);
    }
}
