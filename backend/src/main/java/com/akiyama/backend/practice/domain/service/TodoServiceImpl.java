package com.akiyama.backend.practice.domain.service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.akiyama.backend.practice.domain.entity.Todo;
import com.akiyama.backend.practice.infra.repository.TodoRepository;

import lombok.RequiredArgsConstructor;

// サービスクラスもインターフェースと実装クラスに分けている
// ->大規模開発やTERASOLUNA系の案件ではServiceもインターフェースと実装クラスに分けることがある
// 最近では作らないことが一般的
// 下記を重視する場合の未実施
// レイヤ間の依存を抽象に統一
// テスト容易性
// 開発ルール統一
// 将来の拡張性

@Service
@Transactional
@RequiredArgsConstructor
public class TodoServiceImpl implements TodoService {
    private static final long MAX_UNFINISHED_COUNT = 5;

    // @Injectはオブジェクトを自分で生成せずにDIコンテナから注入するように指示をするもの
    // @Autowiredと同じ役割のもの　違いはJava標準かSpringかというもの
    // しかし現在は「コンストラクタインジェクション」が推奨されているため
    // その記載にしている
    private final TodoRepository todoRepository;

    @Override
    @Transactional(readOnly = true)
    public Collection<Todo> findAll() {
        return todoRepository.findAll();
    }

    @Override
    public Todo create(Todo todo) {
        long unfinishedCount = todoRepository.countByFinished(false);
        if (unfinishedCount >= MAX_UNFINISHED_COUNT) {
            return null;
        }

        String todoId = UUID.randomUUID().toString();
        LocalDate createdAt = LocalDate.now();

        todo.setTodoId(todoId);
        todo.setCreatedAt(createdAt);
        todo.setFinished(false);

        todoRepository.create(todo);

        return todo;
    }

    @Override
    public Todo finish(String todoId) {
        Todo todo = findOne(todoId);
        if (todo.isFinished()) {
            return null;
        }
        todo.setFinished(true);
        todoRepository.update(todo);

        return todo;
    }

    @Override
    public void delete(String todoId) {
        Todo todo = findOne(todoId);
        todoRepository.delete(todo);
    }

    // (10)
    private Todo findOne(String todoId) {
        // (11)
        return todoRepository.findById(todoId).orElseThrow();
    }
}
