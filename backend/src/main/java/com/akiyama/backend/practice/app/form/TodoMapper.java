package com.akiyama.backend.practice.app.form;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.akiyama.backend.practice.domain.entity.Todo;

// TodoFromをTodoにマッピングしている
// @Mapper：このインターフェースはMapStructのMapperという宣言
// ->コンパイル時にTodoMapperImplという実装クラスが自動生成される
// ★1つのMapperは1つの業務（またはEntity）を担当のため
// 　他のMapperが必要なら別途作成する★
@Mapper(componentModel = "spring")
// ->これにすると実装クラスに@Componentが付く⇒つまりDIコンテナに自動で追加される
public interface TodoMapper {
    // ignore = true：createdAtはコピーしないという指定
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "finished", ignore = true)
    Todo map(TodoForm form);
}