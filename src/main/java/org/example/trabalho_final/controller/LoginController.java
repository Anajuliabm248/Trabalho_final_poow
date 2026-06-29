package org.example.trabalho_final.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.example.trabalho_final.model.Usuario;
import org.example.trabalho_final.service.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    private final AuthService authService;

    public LoginController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String login(HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario != null) {

            if ("VENDEDOR".equals(usuario.getTipo())) {
                return "redirect:/vendedor/dashboard";
            }

            return "redirect:/livros";
        }

        return "auth/login";
    }

    @PostMapping("/login")
    public String autenticar(
            @RequestParam String email,
            @RequestParam String senha,
            HttpServletRequest request,
            Model model) {

        Usuario usuario = authService.login(email, senha);

        if (usuario == null) {

            model.addAttribute(
                    "erro",
                    "Email ou senha inválidos."
            );

            return "auth/login";
        }

        HttpSession antiga = request.getSession(false);

        if (antiga != null) {
            antiga.invalidate();
        }

        HttpSession nova = request.getSession(true);

        nova.setAttribute("usuario", usuario);

        if ("VENDEDOR".equals(usuario.getTipo())) {
            return "redirect:/vendedor/dashboard";
        }

        return "redirect:/livros";
    }
}