package org.example.trabalho_final.controller;

import jakarta.servlet.http.HttpSession;
import org.example.trabalho_final.model.Carrinho;
import org.example.trabalho_final.model.ItemCarrinho;
import org.example.trabalho_final.model.Livro;
import org.example.trabalho_final.model.Usuario;
import org.example.trabalho_final.service.CarrinhoService;
import org.example.trabalho_final.service.LivroService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class CarrinhoController {

    private final CarrinhoService carrinhoService;
    private final LivroService livroService;

    public CarrinhoController(
            CarrinhoService carrinhoService,
            LivroService livroService) {

        this.carrinhoService = carrinhoService;
        this.livroService = livroService;
    }

    @GetMapping("/carrinho")
    public String paginaCarrinho(HttpSession session, Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/login";
        }

        List<ItemCarrinho> itens =
                carrinhoService.listarItens(usuario.getId());

        Carrinho carrinho =
                carrinhoService.buscarCarrinho(usuario.getId());

        List<Livro> livros = itens.stream()
                .map(item -> livroService.buscarPorId(item.getLivroId()))
                .toList();

        model.addAttribute("itens", itens);
        model.addAttribute("carrinho", carrinho);
        model.addAttribute("livros", livros);

        return "carrinho/carrinho";
    }

    @PostMapping("/carrinho/adicionar")
    public String adicionarItem(
            @RequestParam Integer livroId,
            @RequestParam Integer quantidade,
            HttpSession session,
            Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/login";
        }

        boolean sucesso = carrinhoService.adicionarItem(
                usuario.getId(),
                livroId,
                quantidade
        );

        if (!sucesso) {

            model.addAttribute("erro", "Estoque insuficiente.");
            model.addAttribute(
                    "livro",
                    livroService.buscarPorId(livroId)
            );

            return "livro/detalhe";
        }

        return "redirect:/carrinho";
    }

    @PostMapping("/carrinho/remover")
    public String removerItem(
            @RequestParam Integer itemId,
            HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/login";
        }

        carrinhoService.removerItem(itemId, usuario.getId());

        return "redirect:/carrinho";
    }

    @PostMapping("/carrinho/limpar")
    public String limparCarrinho(HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/login";
        }

        carrinhoService.limpar(usuario.getId());

        return "redirect:/carrinho";
    }
}