package org.example.trabalho_final.controller;

import org.example.trabalho_final.model.Cliente;
import org.example.trabalho_final.model.Vendedor;
import org.example.trabalho_final.service.ClienteService;
import org.example.trabalho_final.service.VendedorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CadastroController {

    private final ClienteService clienteService;
    private final VendedorService vendedorService;

    public CadastroController(
            ClienteService clienteService,
            VendedorService vendedorService) {

        this.clienteService = clienteService;
        this.vendedorService = vendedorService;
    }

    @GetMapping("/cadastro")
    public String cadastro() {
        return "auth/cadastro";
    }

    @PostMapping("/cadastro")
    public String cadastrar(
            @RequestParam String nome,
            @RequestParam String cpf,
            @RequestParam String email,
            @RequestParam String telefone,
            @RequestParam String senha,
            @RequestParam String tipo,
            Model model
    ) {
        boolean sucesso;

        if ("VENDEDOR".equals(tipo)) {

            Vendedor vendedor = new Vendedor(
                    nome,
                    cpf,
                    telefone,
                    email,
                    senha
            );

            sucesso = vendedorService.cadastrar(vendedor);
        } else {
            Cliente cliente = new Cliente();

            cliente.setNome(nome);
            cliente.setCpf(cpf);
            cliente.setEmail(email);
            cliente.setTelefone(telefone);
            cliente.setSenha(senha);

            cliente.setAtivo(true);
            cliente.setTipo("CLIENTE");

            sucesso = clienteService.cadastrar(cliente);
        }
        if (sucesso) {
            return "redirect:/login?msg=cadastrado";
        }
        model.addAttribute(
                "erro",
                "Email ou CPF já cadastrado."
        );
        return "auth/cadastro";
    }
}
