package org.example.trabalho_final.service;

import org.example.trabalho_final.model.Carrinho;
import org.example.trabalho_final.model.ItemCarrinho;
import org.example.trabalho_final.model.Livro;
import org.example.trabalho_final.repository.CarrinhoRepository;
import org.example.trabalho_final.repository.ItemCarrinhoRepository;
import org.example.trabalho_final.repository.LivroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CarrinhoService {

    private final CarrinhoRepository carrinhoRepository;
    private final ItemCarrinhoRepository itemCarrinhoRepository;
    private final LivroRepository livroRepository;

    public CarrinhoService(
            CarrinhoRepository carrinhoRepository,
            ItemCarrinhoRepository itemCarrinhoRepository,
            LivroRepository livroRepository) {

        this.carrinhoRepository = carrinhoRepository;
        this.itemCarrinhoRepository = itemCarrinhoRepository;
        this.livroRepository = livroRepository;
    }

    public Carrinho buscarCarrinho(int clienteId) {
        return carrinhoRepository.buscarPorClienteId(clienteId);
    }

    public List<ItemCarrinho> listarItens(int clienteId) {

        Carrinho carrinho = carrinhoRepository.buscarPorClienteId(clienteId);

        if (carrinho == null) {
            return List.of();
        }

        return itemCarrinhoRepository.listarPorCarrinho(carrinho.getId());
    }

    @Transactional
    public boolean adicionarItem(int clienteId, int livroId, int quantidade) {

        Livro livro = livroRepository.buscarPorId(livroId);

        if (livro == null || livro.getQuantidade() < quantidade) {
            return false;
        }

        Carrinho carrinho = carrinhoRepository.buscarPorClienteId(clienteId);

        ItemCarrinho existente =
                itemCarrinhoRepository.buscarPorCarrinhoELivro(
                        carrinho.getId(),
                        livroId
                );

        if (existente != null) {

            int novaQuantidade = existente.getQuantidade() + quantidade;

            existente.setQuantidade(novaQuantidade);
            existente.setSubtotal(novaQuantidade * livro.getPreco());

            itemCarrinhoRepository.atualizar(existente);

        } else {

            ItemCarrinho item = new ItemCarrinho();

            item.setCarrinhoId(carrinho.getId());
            item.setLivroId(livroId);
            item.setQuantidade(quantidade);
            item.setSubtotal(quantidade * livro.getPreco());

            itemCarrinhoRepository.inserir(item);
        }

        recalcularTotal(carrinho.getId());

        return true;
    }

    @Transactional
    public void removerItem(int itemId, int clienteId) {

        itemCarrinhoRepository.excluir(itemId);

        Carrinho carrinho =
                carrinhoRepository.buscarPorClienteId(clienteId);

        recalcularTotal(carrinho.getId());
    }

    @Transactional
    public void limpar(int clienteId) {

        Carrinho carrinho =
                carrinhoRepository.buscarPorClienteId(clienteId);

        itemCarrinhoRepository.excluirPorCarrinho(carrinho.getId());

        carrinhoRepository.atualizarTotal(carrinho.getId(), 0.0);
    }

    private void recalcularTotal(int carrinhoId) {

        List<ItemCarrinho> itens =
                itemCarrinhoRepository.listarPorCarrinho(carrinhoId);

        double total = itens.stream()
                .mapToDouble(ItemCarrinho::getSubtotal)
                .sum();

        carrinhoRepository.atualizarTotal(carrinhoId, total);
    }
}