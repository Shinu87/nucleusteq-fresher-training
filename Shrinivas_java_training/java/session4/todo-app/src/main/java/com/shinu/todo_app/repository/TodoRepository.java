package com.shinu.todo_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shinu.todo_app.entity.Todo;

// This interface is used to interact with the database
// Spring Data JPA provides built-in methods like save, findAll, findById, deleteById

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {

    // No need to write implementation
    // Spring automatically provides implementation at runtime
}