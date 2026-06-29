package org.example.trabalho_final.service;

import org.example.trabalho_final.model.Carrinho;
import org.example.trabalho_final.model.Cliente;
import org.example.trabalho_final.repository.CarrinhoRepository;
import org.example.trabalho_final.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final CarrinhoRepository carrinhoRepository;
    private final AuthService authService;

    public ClienteService(ClienteRepository clienteRepository, AuthService authService, CarrinhoRepository carrinhoRepository) {
        this.clienteRepository = clienteRepository;
        this.authService = authService;
        this.carrinhoRepository = carrinhoRepository;
    }

    @Transactional
    public boolean inserir(Cliente cliente) {
        return clienteRepository.inserir(cliente);
    }

    @Transactional
    public boolean cadastrar(Cliente cliente) {
        if (authService.emailJaCadastrado(cliente.getEmail())) {
            return false;
        }
        if (authService.cpfJaCadastrado(cliente.getCpf())) {
            return false;
        }

        boolean inserido = clienteRepository.inserir(cliente);

        if (inserido) {
            Carrinho carrinho = new Carrinho();
            carrinho.setClienteId(cliente.getId());
            carrinhoRepository.inserir(carrinho);
        }

        return inserido;
    }

    public Cliente buscarPorId(int id) {
        return clienteRepository.buscarPorId(id);
    }

    @Transactional
    public void atualizar(Cliente cliente) {
        clienteRepository.atualizar(cliente);
    }
}