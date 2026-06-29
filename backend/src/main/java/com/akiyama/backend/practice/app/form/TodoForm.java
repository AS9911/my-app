package com.akiyama.backend.practice.app.form;

import java.io.Serializable;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

public class TodoForm implements Serializable{
    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    @NotNull // (1)
    @Size(min = 1, max = 30)
    private String todoTitle;    
}
