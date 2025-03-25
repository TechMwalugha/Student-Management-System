package com.example.backend.repositories;

import com.example.backend.entities.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Date;
import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    // Fetch all students
    List<Student> findAll();

    // Search by studentId
    Page<Student> findByStudentId(Long studentId, Pageable pageable);

    // Filter by class
    Page<Student> findByStudentClass(String studentClass, Pageable pageable);

    // Filter by date of birth range
    Page<Student> findByDobBetween(Date startDate, Date endDate, Pageable pageable);
}
