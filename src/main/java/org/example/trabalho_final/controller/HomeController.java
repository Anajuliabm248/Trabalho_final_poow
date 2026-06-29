package org.example.trabalho_final.controller;

import org.example.trabalho_final.model.Livro;
import org.example.trabalho_final.service.CategoriaService;
import org.example.trabalho_final.service.LivroService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final LivroService livroService;
    private final CategoriaService categoriaService;

    public HomeController(
            LivroService livroService,
            CategoriaService categoriaService) {

        this.livroService = livroService;
        this.categoriaService = categoriaService;
    }

    @GetMapping("/")
    public String home(Model model) {

        model.addAttribute(
                "livros",
                livroService.listar().stream()
                        .limit(8)
                        .toList()
        );

        model.addAttribute(
                "categorias",
                categoriaService.listar()
        );

        return "index";
    }
}