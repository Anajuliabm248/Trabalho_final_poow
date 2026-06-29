package org.example.trabalho_final.controller;

import jakarta.servlet.http.HttpSession;
import org.example.trabalho_final.model.Cliente;
import org.example.trabalho_final.model.Endereco;
import org.example.trabalho_final.model.Usuario;
import org.example.trabalho_final.repository.EnderecoRepository;
import org.example.trabalho_final.service.ClienteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ClienteController {

    private final ClienteService clienteService;
    private final EnderecoRepository enderecoRepository;

    public ClienteController(
            ClienteService clienteService,
            EnderecoRepository enderecoRepository) {

        this.clienteService = clienteService;
        this.enderecoRepository = enderecoRepository;
    }

    @GetMapping("/cliente")
    public String perfil(HttpSession session, Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/login";
        }

        Cliente cliente = clienteService.buscarPorId(usuario.getId());

        model.addAttribute("cliente", cliente);

        return "cliente/perfil";
    }

    @GetMapping("/cliente/endereco")
    public String endereco(HttpSession session, Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/login";
        }

        Endereco endereco = enderecoRepository.buscarPorClienteId(usuario.getId());

        model.addAttribute("endereco", endereco);

        return "cliente/endereco";
    }

    @GetMapping("/cliente/historico")
    public String historico() {
        return "redirect:/venda/historico";
    }

    @PostMapping("/cliente")
    public String atualizarPerfil(
            @RequestParam String nome,
            @RequestParam String telefone,
            @RequestParam String email,
            HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/login";
        }

        Cliente cliente = clienteService.buscarPorId(usuario.getId());

        cliente.setNome(nome);
        cliente.setTelefone(telefone);
        cliente.setEmail(email);

        clienteService.atualizar(cliente);

        usuario.setNome(nome);
        session.setAttribute("usuario", usuario);

        return "redirect:/cliente?msg=salvo";
    }

    @PostMapping("/cliente/endereco")
    public String salvarEndereco(
            Endereco endereco,
            HttpSession session,
            Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/login";
        }

        endereco.setClienteId(usuario.getId());

        Endereco existente = enderecoRepository.buscarPorClienteId(usuario.getId());

        if (existente == null) {

            endereco.setPais("Brasil");

            enderecoRepository.inserir(endereco);

        } else {

            endereco.setId(existente.getId());
            endereco.setClienteId(usuario.getId());
            endereco.setPais(existente.getPais());

            enderecoRepository.atualizar(endereco);
        }

        return "redirect:/cliente/endereco?msg=salvo";
    }
}