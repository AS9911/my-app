package com.akiyama.backend.practice.domain.entity;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;


public class Todo implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Getter
    @Setter
    private String todoId;

    @Getter
    @Setter
    private String todoTitle;
    
    @Getter
    @Setter
    private boolean finished;
    
    @Getter
    @Setter
    private LocalDate createdAt;
    
}