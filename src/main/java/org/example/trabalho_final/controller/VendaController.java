package org.example.trabalho_final.controller;

import jakarta.servlet.http.HttpSession;
import org.example.trabalho_final.model.*;
import org.example.trabalho_final.repository.EnderecoRepository;
import org.example.trabalho_final.service.CarrinhoService;
import org.example.trabalho_final.service.LivroService;
import org.example.trabalho_final.service.VendaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/venda")
public class VendaController {

    private final VendaService vendaService;
    private final CarrinhoService carrinhoService;
    private final LivroService livroService;
    private final EnderecoRepository enderecoRepository;

    public VendaController(
            VendaService vendaService,
            CarrinhoService carrinhoService,
            LivroService livroService,
            EnderecoRepository enderecoRepository) {

        this.vendaService = vendaService;
        this.carrinhoService = carrinhoService;
        this.livroService = livroService;
        this.enderecoRepository = enderecoRepository;
    }

    @GetMapping("/checkout")
    public String checkout(HttpSession session, Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/login";
        }

        List<ItemCarrinho> itens =
                carrinhoService.listarItens(usuario.getId());

        if (itens.isEmpty()) {
            return "redirect:/carrinho";
        }

        Carrinho carrinho =
                carrinhoService.buscarCarrinho(usuario.getId());

        Endereco endereco =
                enderecoRepository.buscarPorClienteId(usuario.getId());

        if (endereco == null) {
            return "redirect:/cliente/endereco?msg=necessario";
        }

        List<Livro> livros = itens.stream()
                .map(i -> livroService.buscarPorId(i.getLivroId()))
                .toList();

        model.addAttribute("endereco", endereco);
        model.addAttribute("itens", itens);
        model.addAttribute("carrinho", carrinho);
        model.addAttribute("livros", livros);

        return "venda/checkout";
    }

    @GetMapping("/confirmacao/{id}")
    public String confirmacao(
            @PathVariable int id,
            HttpSession session,
            Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/login";
        }

        Venda venda = vendaService.buscarPorId(id);

        if (venda == null || venda.getClienteId() != usuario.getId()) {
            return "redirect:/livros";
        }

        List<ItemVenda> itens =
                vendaService.listarItensDaVenda(id);

        List<Livro> livros = itens.stream()
                .map(i -> livroService.buscarPorId(i.getLivroId()))
                .toList();

        model.addAttribute("venda", venda);
        model.addAttribute("itens", itens);
        model.addAttribute("livros", livros);

        return "venda/confirmacao";
    }

    @GetMapping("/historico")
    public String historico(
            HttpSession session,
            Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/login";
        }

        model.addAttribute(
                "vendas",
                vendaService.listarPorCliente(usuario.getId())
        );

        return "cliente/historico";
    }

    @PostMapping("/finalizar")
    public String finalizarCompra(
            @RequestParam String formaPagamento,
            HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/login";
        }

        Venda venda =
                vendaService.finalizarCompra(
                        usuario.getId(),
                        formaPagamento
                );

        if (venda == null) {
            return "redirect:/carrinho?msg=erro";
        }

        return "redirect:/venda/confirmacao/" + venda.getId();
    }

    @PostMapping("/{id}/cancelar")
    public String cancelarVenda(
            @PathVariable int id,
            HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/login";
        }

        Venda venda = vendaService.buscarPorId(id);

        if (venda != null &&
                venda.getClienteId() == usuario.getId()) {

            vendaService.cancelarVenda(id);
        }

        return "redirect:/venda/historico?msg=cancelado";
    }
}