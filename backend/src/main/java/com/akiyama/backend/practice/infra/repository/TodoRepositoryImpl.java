package com.akiyama.backend.practice.infra.repository;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.akiyama.backend.practice.domain.entity.Todo;


// Repositoryを「インターフェース＋実装クラス」に分けるのは設計上のメリットがあるから
// ⇒JavaやSpringのルールとして必須ではない
// ポイント：サービスクラスはドの実装か知らず、DIしたリポジトリのみ知っているという点
// メリット１：実装の差し替えがしやすい
// ⇒サービスから呼ばれるインターフェースクラスは変わらずその実装のみを差し替えられるため
// =>呼び出すインターフェース自体は変更しないため、サービス側の修正も不要
// メリット２：テストしやすい
// メリット３：依存性逆転原則(DIP)
// =>上位モジュールは下位モジュールに依存してはならない。両者は抽象（インターフェース）に依存すべきである。
// =>リポジトリクラスの場合、上位モジュールはサービスクラスになる
// =>要点としては、下位のモジュールの変更により上位モジュールへ影響することを抑制する
// =>問題点はサービスが具体的な実装を知ってしまい依存となる点
// =>だからサービスクラスはリポジトリクラスのインターフェースを呼ぶだけなら
// =>「Repositoryの実装変更が、インターフェースの契約を変えない限りServiceに影響しない」
// DIPは実装の変更から上位モジュールを守るための原則であるが、インターフェースの変更自体をすれば
// 影響は当然、サービスクラスまで及ぶ
// implements TodoRepository：odoRepository インターフェースで定義された契約を実装するというもの
@Repository
public class TodoRepositoryImpl implements TodoRepository {
    // Todoオブジェクトをメモリ上に保存するための共有データ領域を作っている
    // Map<String, Todo>：キー(String) → 値(Todo)を保持するコレクション
    // static；クラス全体で1つだけ共有される変数
    // ->2回newして別々の変数を作っても内容が共有される
    // new ConcurrentHashMap<>()：スレッドセーフなHashMap
    // ->内部的に排他制御を実施するため、複数スレッドから同時に実行しても安全
    private static final Map<String, Todo> TODO_MAP =
        new ConcurrentHashMap<>();

    static {
        Todo todo1 = new Todo();
        todo1.setTodoId("1");
        todo1.setTodoTitle("Send a e-mail");
        Todo todo2 = new Todo();
        todo2.setTodoId("2");
        todo2.setTodoTitle("Have a lunch");
        Todo todo3 = new Todo();
        todo3.setTodoId("3");
        todo3.setTodoTitle("Read a book");
        todo3.setFinished(true);
        TODO_MAP.put(todo1.getTodoId(), todo1);
        TODO_MAP.put(todo2.getTodoId(), todo2);
        TODO_MAP.put(todo3.getTodoId(), todo3);
    }
    
    // @Override 
    // このメソッドは親クラスやインターフェースのメソッドを
    // オーバーライド（上書き）していますという意味
    @Override 
    public Optional<Todo> findById(String todoId) {
        // Optional.ofNullable(...)
        // nullをOptional.empty()として安全に扱えるようにする処理
        // メリット：この値は存在しない可能性がありますよ」ということを、型で表現できる点
        // =>これによりコンパイルレベルで値がない事を踏まえた実装をサービスクラスで実装することを意識付けられる
        // Todo todo = todoOpt.orElse(new Todo());
        // =>存在しない場合デフォルト値を返す
        // Todo todo = todoOpt.orElseThrow();
        // =>存在しなければ例外（引数：() -> new TestException にすることで独自例外も可能）
        // Optional.empty()：「値が存在しないことを表す Optional オブジェクト」
        // nullとの違いは値がないことが想定されているため、上記のようなメソッドを使って
        // 値のない場合を明示的に取り扱っている
        return Optional.ofNullable(TODO_MAP.get(todoId));
    }

    @Override
    public Collection<Todo> findAll() {
        return TODO_MAP.values();
    }

    @Override
    public void create(Todo todo) {
        TODO_MAP.put(todo.getTodoId(), todo);
    }

    @Override
    public boolean update(Todo todo) {
        TODO_MAP.put(todo.getTodoId(), todo);
        return true;
    }

    @Override
    public void delete(Todo todo) {
        TODO_MAP.remove(todo.getTodoId());
    }

    @Override
    public long countByFinished(boolean finished) {
        long count = 0;
        for (Todo todo : TODO_MAP.values()) {
            if (finished == todo.isFinished()) {
                count++;
            }
        }
        return count;
    }
}
