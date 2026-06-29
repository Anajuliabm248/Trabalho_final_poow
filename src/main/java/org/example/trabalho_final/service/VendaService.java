package org.example.trabalho_final.service;

import org.example.trabalho_final.model.*;
import org.example.trabalho_final.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VendaService {

    private final VendaRepository vendaRepository;
    private final ItemVendaRepository itemVendaRepository;
    private final PagamentoRepository pagamentoRepository;
    private final LivroRepository livroRepository;
    private final CarrinhoService carrinhoService;

    public VendaService(VendaRepository vendaRepository, ItemVendaRepository itemVendaRepository, PagamentoRepository pagamentoRepository, LivroRepository livroRepository, CarrinhoService carrinhoService) {
        this.vendaRepository = vendaRepository;
        this.itemVendaRepository = itemVendaRepository;
        this.pagamentoRepository = pagamentoRepository;
        this.livroRepository = livroRepository;
        this.carrinhoService = carrinhoService;
    }

    @Transactional
    public Venda finalizarCompra(int clienteId, String formaPagamento) {
        List<ItemCarrinho> itensCarrinho = carrinhoService.listarItens(clienteId);

        if (itensCarrinho.isEmpty()) return null;

        // Valida estoque antes de criar qualquer registro
        for (ItemCarrinho item : itensCarrinho) {
            Livro livro = livroRepository.buscarPorId(item.getLivroId());
            if (livro == null || livro.getQuantidade() < item.getQuantidade()) return null;
        }

        double total = 0;
        for (ItemCarrinho item : itensCarrinho) {
            Livro livro = livroRepository.buscarPorId(item.getLivroId());
            total += livro.getPreco() * item.getQuantidade();
        }

        Venda venda = new Venda();
        venda.setClienteId(clienteId);
        venda.setValorTotal(total);
        int vendaId = vendaRepository.inserir(venda);
        venda.setId(vendaId);

        for (ItemCarrinho item : itensCarrinho) {
            Livro livro = livroRepository.buscarPorId(item.getLivroId());

            ItemVenda itemVenda = new ItemVenda();
            itemVenda.setVendaId(vendaId);
            itemVenda.setLivroId(item.getLivroId());
            itemVenda.setQuantidade(item.getQuantidade());
            itemVenda.setPrecoUni(livro.getPreco());
            itemVenda.setSubtotal(livro.getPreco() * item.getQuantidade());
            itemVendaRepository.inserir(itemVenda);

            livroRepository.atualizarEstoque(livro.getId(), livro.getQuantidade() - item.getQuantidade());
        }

        Pagamento pagamento = new Pagamento();
        pagamento.setVendaId(vendaId);
        pagamento.setFormaPagamento(formaPagamento);
        pagamento.setValor(total);
        pagamentoRepository.inserir(pagamento);

        pagamentoRepository.atualizarStatus(vendaId, "APROVADO");
        vendaRepository.atualizarStatus(vendaId, "CONCLUIDA");

        carrinhoService.limpar(clienteId);

        return venda;
    }

    @Transactional
    public void cancelarVenda(int vendaId) {
        List<ItemVenda> itens = itemVendaRepository.listarPorVenda(vendaId);
        for (ItemVenda item : itens) {
            Livro livro = livroRepository.buscarPorId(item.getLivroId());
            livroRepository.atualizarEstoque(livro.getId(), livro.getQuantidade() + item.getQuantidade());
        }
        vendaRepository.atualizarStatus(vendaId, "CANCELADA");
        pagamentoRepository.atualizarStatus(vendaId, "CANCELADO");
    }

    public Venda buscarPorId(int id) {
        return vendaRepository.buscarPorId(id);
    }

    public List<Venda> listarPorCliente(int clienteId) {
        return vendaRepository.listarPorCliente(clienteId);
    }

    // Vendas que contêm livros deste vendedor
    public List<Venda> listarPorVendedor(int vendedorId) {
        return vendaRepository.listarPorVendedor(vendedorId);
    }

    public List<Venda> listarTodas() {
        return vendaRepository.listarTodas();
    }

    public List<ItemVenda> listarItensDaVenda(int vendaId) {
        return itemVendaRepository.listarPorVenda(vendaId);
    }
}
