package com.shinu.todo_app.service;

import org.springframework.stereotype.Service;

import com.shinu.todo_app.repository.TodoRepository;

@Service
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;

    public TodoServiceImpl(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

}
