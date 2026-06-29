package org.example.trabalho_final.controller;

import jakarta.servlet.http.HttpSession;
import org.example.trabalho_final.model.Usuario;
import org.example.trabalho_final.model.Venda;
import org.example.trabalho_final.service.LivroService;
import org.example.trabalho_final.service.VendaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/vendedor")
public class VendedorController {

    private final LivroService livroService;
    private final VendaService vendaService;

    public VendedorController(
            LivroService livroService,
            VendaService vendaService) {

        this.livroService = livroService;
        this.vendaService = vendaService;
    }

    @GetMapping("/dashboard")
    public String dashboard(
            HttpSession session,
            Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/login";
        }

        if (!"VENDEDOR".equals(usuario.getTipo())) {
            return "redirect:/livros";
        }

        int vendedorId = usuario.getId();

        model.addAttribute(
                "totalVendas",
                vendaService.listarPorVendedor(vendedorId).size()
        );

        model.addAttribute(
                "totalLivros",
                livroService.listarPorVendedor(vendedorId).size()
        );

        return "vendedor/dashboard";
    }

    @GetMapping("/estoque")
    public String estoque(
            HttpSession session,
            Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/login";
        }

        if (!"VENDEDOR".equals(usuario.getTipo())) {
            return "redirect:/livros";
        }

        model.addAttribute(
                "livros",
                livroService.listarPorVendedor(usuario.getId())
        );

        return "vendedor/estoque";
    }

    @GetMapping("/relatorio")
    public String relatorio(
            HttpSession session,
            Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/login";
        }

        if (!"VENDEDOR".equals(usuario.getTipo())) {
            return "redirect:/livros";
        }

        List<Venda> vendas =
                vendaService.listarPorVendedor(usuario.getId());

        double totalGeral = vendas.stream()
                .mapToDouble(Venda::getValorTotal)
                .sum();

        model.addAttribute("vendas", vendas);
        model.addAttribute("totalGeral", totalGeral);

        return "vendedor/relatorio";
    }
}