package com.shinu.todo_app.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.shinu.todo_app.dto.TodoDTO;
import com.shinu.todo_app.entity.Todo;
import com.shinu.todo_app.entity.TodoStatus;
import com.shinu.todo_app.exception.TodoNotFoundException;
import com.shinu.todo_app.mapper.TodoMapper;
import com.shinu.todo_app.repository.TodoRepository;

// This class contains business logic for Todo application
// Service layer connects Controller with Repository

@Service
public class TodoServiceImpl implements TodoService {

    // repository object to interact with database
    private final TodoRepository todoRepository;

    // constructor injection (Spring will inject repository automatically)
    public TodoServiceImpl(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    // create new todo and save into database
    @Override
    public TodoDTO createTodo(TodoDTO dto) {

        // convert DTO to Entity
        Todo todo = TodoMapper.mapToEntity(dto);

        // if status is not given set default as PENDING
        todo.setStatus(
                todo.getStatus() == null ? TodoStatus.PENDING : todo.getStatus());

        // set current time as creation time
        todo.setCreatedAt(LocalDateTime.now());

        // save entity in database
        Todo saved = todoRepository.save(todo);

        // convert entity back to DTO and return
        return TodoMapper.mapToDTO(saved);
    }

    // get all todos from database
    @Override
    public List<TodoDTO> getAllTodos() {

        List<Todo> todos = todoRepository.findAll();

        // convert list of entities to list of DTOs
        return todos.stream()
                .map(TodoMapper::mapToDTO)
                .collect(Collectors.toList());
    }

    // get todo by id
    @Override
    public TodoDTO getTodoById(Long id) {

        // find todo or throw exception if not found
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException("Todo not found with id: " + id));

        return TodoMapper.mapToDTO(todo);
    }

    // update existing todo
    @Override
    public TodoDTO updateTodo(Long id, TodoDTO dto) {

        // find existing todo or throw exception
        Todo existingTodo = todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException("Todo not found with id: " + id));

        // update title if provided
        if (dto.getTitle() != null) {
            existingTodo.setTitle(dto.getTitle());
        }

        // update description if provided
        if (dto.getDescription() != null) {
            existingTodo.setDescription(dto.getDescription());
        }

        // update status with validation rules
        if (dto.getStatus() != null) {

            TodoStatus currentStatus = existingTodo.getStatus();
            TodoStatus newStatus = dto.getStatus();

            // allowed transitions
            if (currentStatus == TodoStatus.PENDING && newStatus == TodoStatus.COMPLETED) {
                existingTodo.setStatus(newStatus);

            } else if (currentStatus == TodoStatus.COMPLETED && newStatus == TodoStatus.PENDING) {
                existingTodo.setStatus(newStatus);

            } else if (currentStatus != newStatus) {
                // invalid status transition
                throw new RuntimeException("Invalid status transition from "
                        + currentStatus + " to " + newStatus);
            }
        }

        // save updated todo
        Todo updated = todoRepository.save(existingTodo);

        return TodoMapper.mapToDTO(updated);
    }

    // delete todo by id
    @Override
    public void deleteTodo(Long id) {

        // check if todo exists before deleting
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException("Todo not found with id: " + id));

        // delete from database
        todoRepository.delete(todo);
    }
}