package com.example.routineapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.routineapp.service.UserService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("form", new RegisterForm());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("form") RegisterForm form,
                           BindingResult result,
                           RedirectAttributes redirectAttributes) {
        System.out.println(form);
        if (result.hasErrors()) {
            System.out.println(result.getAllErrors());
            return "auth/register";
        }
        if (!form.getPassword().equals(form.getPasswordConfirm())) {
            result.rejectValue("passwordConfirm", "error.form", "パスワードが一致しません");
            return "auth/register";
        }
        try {
            userService.register(form.getEmail(), form.getPassword(), form.getName());
        } catch (IllegalArgumentException e) {
            result.rejectValue("email", "error.form", e.getMessage());
            return "auth/register";
        }
        redirectAttributes.addFlashAttribute("message", "登録が完了しました。ログインしてください。");
        return "redirect:/login";
    }

    public static class RegisterForm {
        @NotBlank(message = "メールアドレスを入力してください")
        private String email;

        @NotBlank(message = "パスワードを入力してください")
        @Size(min = 6, message = "パスワードは6文字以上で入力してください")
        private String password;

        private String passwordConfirm;

        @NotBlank(message = "名前を入力してください")
        @Size(max = 100)
        private String name;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getPasswordConfirm() { return passwordConfirm; }
        public void setPasswordConfirm(String passwordConfirm) { this.passwordConfirm = passwordConfirm; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
}
