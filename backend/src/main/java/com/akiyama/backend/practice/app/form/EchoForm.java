package com.akiyama.backend.practice.app.form;

import java.io.Serializable;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EchoForm implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @NotNull // nullを許容しない
    @Size(min = 1, max = 5) // 1文字以上5文字以下
    private String name;
}
