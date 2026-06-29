package org.example.trabalho_final.service;

import org.example.trabalho_final.model.Categoria;
import org.example.trabalho_final.repository.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public List<Categoria> listar() {
        return categoriaRepository.listar();
    }

    public Categoria buscarPorId(int id) {
        return categoriaRepository.buscarPorId(id);
    }

    @Transactional
    public boolean inserir(Categoria categoria) {
        return categoriaRepository.inserir(categoria);
    }
}