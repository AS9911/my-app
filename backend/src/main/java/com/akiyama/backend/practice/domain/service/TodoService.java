package com.akiyama.backend.practice.domain.service;

import java.util.Collection;

import com.akiyama.backend.practice.domain.entity.Todo;

public interface TodoService {
    Collection<Todo> findAll();
    
    Todo create(Todo todo);

    Todo finish(String todoId);

    void delete(String todoId);
}