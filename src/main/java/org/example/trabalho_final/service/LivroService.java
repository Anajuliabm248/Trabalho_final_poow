package org.example.trabalho_final.service;

import org.example.trabalho_final.model.Livro;
import org.example.trabalho_final.repository.LivroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LivroService {

    private final LivroRepository livroRepository;

    public LivroService(LivroRepository livroRepository) {
        this.livroRepository = livroRepository;
    }

    // Catálogo público (clientes veem todos)
    public List<Livro> listar() {
        return livroRepository.listar();
    }

    // Estoque privado (vendedor vê só os seus)
    public List<Livro> listarPorVendedor(int vendedorId) {
        return livroRepository.listarPorVendedor(vendedorId);
    }

    public Livro buscarPorId(int id) {
        return livroRepository.buscarPorId(id);
    }

    public List<Livro> buscarPorNome(String nome) {
        return livroRepository.buscarPorNome(nome);
    }

    public List<Livro> buscarPorCategoria(int categoriaId) {
        return livroRepository.buscarPorCategoria(categoriaId);
    }

    @Transactional
    public boolean inserir(Livro livro) {
        return livroRepository.inserir(livro);
    }

    @Transactional
    public void atualizar(Livro livro) {
        livroRepository.atualizar(livro);
    }

    // Retorna false se o livro não pertencer ao vendedor
    @Transactional
    public boolean excluir(int livroId, int vendedorId) {
        return livroRepository.excluir(livroId, vendedorId);
    }

}
