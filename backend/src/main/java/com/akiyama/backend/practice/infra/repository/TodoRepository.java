package com.akiyama.backend.practice.infra.repository;

import java.util.Collection;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.akiyama.backend.practice.domain.entity.Todo;

@Repository
// このインタフェースのクラスがサービスから呼ばれる
// インターフェースのメソッドは必ず「public abstract」と決まっており
// 内部的にもそのように扱われるため省略している。
// public abstractに対する実装は必ず公開しないといけないため、実装側はpublicをつける
// =>何もつけない場合、privateメソッドとして判定されてコンパイルエラーになる
public interface TodoRepository {
    // Optional型はJava 8から導入されたnullを安全に扱うための仕組み
    // コーディングミスによるNullPointerException等の防止に寄与
    // 戻り値をOptional型にすべきか否かは、
    // 採用するO/R Mapperの仕様を確認
    // O/R Mapperに依存しない場合とMyBatis3を利用する場合は、Optional型にしても良い。
    // Spring Data JPAを利用する場合は、Repositoryのメソッドシグネチャが決まっており必ずOptional型となる。
    Optional<Todo> findById(String todoId);
    // Collection はJavaのコレクションの親インターフェース（List<Todo>　Set<Todo>と共通の親）
    // 実務ではListで扱うことが多い
    Collection<Todo> findAll();

    void create(Todo todo);

    boolean update(Todo todo);

    void delete(Todo todo);
    // long countByFinished(boolean)の引数としてtrueを渡すと
    // 「完了済みの件数」、falseを渡すと「未完了の件数」が取得できる仕様としている。
    long countByFinished(boolean finished);
}
