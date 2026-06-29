package com.akiyama.backend.practice.app.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.akiyama.backend.practice.app.form.EchoForm;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
// クラスルートパス
@RequestMapping("echo")
public class EchoController {
    
    // Controllerのリクエスト処理の前に自動実行されるメソッド
    // ※@GetMapping や @PostMapping より前に実行する
    // フォーム表示時に空のFormオブジェクトを作る
    // ★Thymeleafで<form th:object="${echoForm}">といった感じで使う場合は必要★
    // ->画面表示(GET)の時点でechoFormというオブジェクトがModelに存在していなければならないため
    @ModelAttribute 
    public EchoForm setUpEchoForm() {
        EchoForm form = new EchoForm();
        return form;
    }

    @GetMapping()
    // Model model の引数は、
    // ControllerからView(Thymeleaf)へデータを渡すための入れ物
    // SpringMVCは以下の順に動作する
    // Controller
    // ↓
    // Model
    // ↓
    // View(Thymeleaf)
    // 引数にModelを書くのは、、「Viewへデータを渡したい場合のお作法」
    public String index(Model Model) {
        // src/main/resources/templates配下のhtmlファイルが呼ばれる
        return "index";
    }

    @PostMapping("hello")
    public String hello(
        @Validated EchoForm form,
        BindingResult result,
        Model model
    ) {
        if (result.hasErrors()) {
            return "index";
        }
        // ControllerからThymeleafへデータを渡す処理
        // model.addAttribute() は、r
        // eturn で返すThymeleafテンプレート（HTML）で利用するためのデータを渡している
        model.addAttribute("name", form.getName());
        return "hello";
    }
}
