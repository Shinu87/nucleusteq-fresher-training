package com.shinu.todo_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shinu.todo_app.entity.Todo;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {

}
