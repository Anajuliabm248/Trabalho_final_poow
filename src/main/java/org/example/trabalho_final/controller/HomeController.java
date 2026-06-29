package org.example.trabalho_final.controller;

import org.example.trabalho_final.service.LivroService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class HomeController {

    private final LivroService livroService;

    public HomeController(LivroService livroService) {
        this.livroService = livroService;
    }

    @GetMapping("/")
    public String home(Model model) {

        model.addAttribute(
                "livros",
                livroService.listar().stream()
                        .limit(8)
                        .toList()
        );

        return "index";
    }
}