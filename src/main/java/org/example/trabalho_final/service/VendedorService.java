package org.example.trabalho_final.service;

import org.example.trabalho_final.model.Vendedor;
import org.example.trabalho_final.repository.VendedorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VendedorService {

    private final VendedorRepository repository;
    private final AuthService authService;
    private final VendedorRepository vendedorRepository;

    public VendedorService(VendedorRepository repository, AuthService authService, VendedorRepository vendedorRepository) {
        this.repository = repository;
        this.authService = authService;
        this.vendedorRepository = vendedorRepository;
    }

    @Transactional
    public boolean inserir(Vendedor vendedor) {
        return repository.inserir(vendedor);
    }

    @Transactional
    public boolean cadastrar(Vendedor vendedor) {
        if (authService.emailJaCadastrado(vendedor.getEmail())) {
            return false;
        }
        if (authService.cpfJaCadastrado(vendedor.getCpf())) {
            return false;
        }
        return vendedorRepository.inserir(vendedor);
    }

    @Transactional
    public void atualizar(Vendedor vendedor) {
        vendedorRepository.atualizar(vendedor);
    }
}