package com.akiyama.backend.practice.app.controller;

import java.util.Collection;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import com.akiyama.backend.practice.app.form.TodoForm;
import com.akiyama.backend.practice.app.form.TodoMapper;
import com.akiyama.backend.practice.domain.entity.Todo;
import com.akiyama.backend.practice.domain.service.TodoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;



@Controller
@RequestMapping("todo")
@RequiredArgsConstructor
public class TodoController {
    
    // @ModelAttributeアノテーションをつけることで、
    // このメソッドの返り値のformオブジェクトが、
    // todoFormという名前でModelに追加
    // これは、TodoControllerの各処理で、
    // model.addAttribute("todoForm", form)を実装するのと同義
    @ModelAttribute
    public TodoForm setUoForm() {
        TodoForm form = new TodoForm();
        return form;
    }

    private final TodoService todoService;
    private final TodoMapper beanMapper;
    
    @GetMapping("list")
    public String showAllTodo(Model model) {
        Collection<Todo> todos = todoService.findAll();
        model.addAttribute("todos", todos);
        return "list";
    }
    
    @PostMapping("create")
    public String createTodo(
        @Valid TodoForm todoForm,
        BindingResult bindingResult,
        Model model,
        RedirectAttributes attributes
    ) {
        if (bindingResult.hasErrors()) {
            return showAllTodo(model);
        }
        // 実務的な設計ではコントローラでは、Form⇒DTO(Entity)に変換でOK
        // DTOを用いた場合、ServiceクラスでDTOからBusiness Entityへ変換した後は、
        // そのEntityを使って業務処理を行います。
        // ★使い分け★
        // ・form⇒business entity
        // CRUD中心のシステム
        // Entityが単純なデータ保持オブジェクト
        // 業務ロジックが少ない
        // 小〜中規模システム
        // ・DTOを挟む場合
        // 向いているケース
        // 大規模システム
        // DDD
        // 業務ルールが多い
        // APIと画面で入力形式が異なる
        // ->つまりインフラ層に渡すまで業務ロジックによる値の変更が多くある場合、
        // 一旦DTOに入力データを保持し、それをもとにServiceが業務ロジックを適用して、
        // 業務ルール上正しいBusiness Entityを生成（または更新）する。
        // メリットはEntityが「常に業務ルールを満たした正しい状態」になること
        Todo todo = beanMapper.map(todoForm);

        try {
            todoService.create(todo);
        } catch (Exception e) {
            model.addAttribute(e);
            return showAllTodo(model);
        }
        // リダイレクト先の画面に、一度だけ値を渡すための処理
        // Spring MVCでは、リダイレクトすると通常のModelの値は引き継がれません。
        // ->リダイレクトはクライアント側からリクエスト外の新たなリクエストなるため
        // ->つまりModelクラスのmodel.addAttributeではリダイレクト先に値を渡せない
        // FlashAttributeは、
        // //「登録しました」「削除しました」などの一度だけ表示したいメッセージを
        // リダイレクト先に渡す用途で利用される
        attributes.addFlashAttribute("message", "登録しました");
        return "redirect:todo/list";
    }
    
    public String Finish() {
        return "redirect:todo/list";
    }

    public String Delete(Model model) {
        return "redirect:todo/list";

    }
    
}
