package com.vansh.studentmanagement.controller;

import com.vansh.studentmanagement.model.Student;
import com.vansh.studentmanagement.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST endpoints for student records.
 *
 * <table>
 *   <tr><th>Method</th><th>Endpoint</th><th>Success</th><th>Description</th></tr>
 *   <tr><td>POST</td><td>/api/students</td><td>201</td><td>Add a new student</td></tr>
 *   <tr><td>GET</td><td>/api/students</td><td>200</td><td>Retrieve all students</td></tr>
 *   <tr><td>GET</td><td>/api/students/{id}</td><td>200</td><td>Retrieve student by ID</td></tr>
 *   <tr><td>PUT</td><td>/api/students/{id}</td><td>200</td><td>Update student details</td></tr>
 *   <tr><td>DELETE</td><td>/api/students/{id}</td><td>204</td><td>Delete a student</td></tr>
 *   <tr><td>GET</td><td>/api/students/course/{course}</td><td>200</td><td>Retrieve students by course</td></tr>
 * </table>
 */
@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*")
public class StudentController {

    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    /** POST /api/students — 201 Created, or 400 on validation errors, 409 on duplicate email. */
    @PostMapping
    public ResponseEntity<Student> createStudent(@Valid @RequestBody Student student) {
        Student created = service.createStudent(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /** GET /api/students — 200 OK with the full list (empty array if none). */
    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        return ResponseEntity.ok(service.getAllStudents());
    }

    /** GET /api/students/{id} — 200 OK, or 404 if the id does not exist. */
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getStudentById(id));
    }

    /** PUT /api/students/{id} — 200 OK, or 404 / 400 / 409 as applicable. */
    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(
            @PathVariable Long id, @Valid @RequestBody Student student) {
        return ResponseEntity.ok(service.updateStudent(id, student));
    }

    /** DELETE /api/students/{id} — 204 No Content, or 404 if the id does not exist. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        service.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    /** GET /api/students/course/{course} — 200 OK (case-insensitive, empty array if none match). */
    @GetMapping("/course/{course}")
    public ResponseEntity<List<Student>> getStudentsByCourse(@PathVariable String course) {
        return ResponseEntity.ok(service.getStudentsByCourse(course));
    }
}
