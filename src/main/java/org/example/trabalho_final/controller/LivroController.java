package org.example.trabalho_final.controller;

import jakarta.servlet.http.HttpSession;
import org.example.trabalho_final.model.Livro;
import org.example.trabalho_final.model.Usuario;
import org.example.trabalho_final.service.CategoriaService;
import org.example.trabalho_final.service.LivroService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/livros")
public class LivroController {

    private static final Set<String> MIME_IMAGENS_PERMITIDOS = Set.of(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/webp"
    );
    private final LivroService livroService;
    private final CategoriaService categoriaService;

    public LivroController(
            LivroService livroService,
            CategoriaService categoriaService) {

        this.livroService = livroService;
        this.categoriaService = categoriaService;
    }

    /**
     * Catálogo público
     */
    @GetMapping
    public String catalogo(
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) Integer categoriaId,
            Model model) {

        List<Livro> livros;

        if (busca != null && !busca.isBlank()) {

            livros = livroService.buscarPorNome(busca);

        } else if (categoriaId != null) {

            livros = livroService.buscarPorCategoria(categoriaId);

        } else {

            livros = livroService.listar();
        }

        model.addAttribute("livros", livros);
        model.addAttribute("categorias", categoriaService.listar());

        return "livro/catalogo";
    }

    /**
     * Página de detalhes
     */
    @GetMapping("/{id}")
    public String detalhe(@PathVariable Integer id, Model model) {
        Livro livro = livroService.buscarPorId(id);

        if (livro == null) {
            return "redirect:/livros";
        }

        model.addAttribute("livro", livro);
        model.addAttribute(
                "categoria",
                categoriaService.buscarPorId(livro.getCategoriaId())
        );

        return "livro/detalhe";
    }

    /**
     * Formulário de cadastro
     */
    @GetMapping("/novo")
    public String novo(
            HttpSession session,
            Model model) {

        Usuario vendedor = getVendedor(session);

        if (vendedor == null) {
            return "redirect:/login";
        }

        model.addAttribute("livro", new Livro());
        model.addAttribute("categorias", categoriaService.listar());

        return "livro/form";
    }

    /**
     * Formulário de edição
     */
    @GetMapping("/{id}/editar")
    public String editar(
            @PathVariable Integer id,
            HttpSession session,
            Model model) {

        Usuario vendedor = getVendedor(session);

        if (vendedor == null) {
            return "redirect:/login";
        }

        Livro livro = livroService.buscarPorId(id);

        if (livro == null ||
                livro.getVendedorId() != vendedor.getId()) {

            return "redirect:/vendedor/estoque?msg=negado";
        }

        model.addAttribute("livro", livro);
        model.addAttribute("categorias", categoriaService.listar());

        return "livro/form";
    }

    /**
     * Recupera o vendedor logado
     */
    private Usuario getVendedor(HttpSession session) {

        Usuario usuario =
                (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            return null;
        }

        if (!"VENDEDOR".equals(usuario.getTipo())) {
            return null;
        }

        return usuario;
    }

    @PostMapping
    public String salvar(
            Livro livro,
            @RequestParam("arquivo") MultipartFile arquivo,
            HttpSession session,
            Model model) throws IOException {

        Usuario vendedor = getVendedor(session);

        if (vendedor == null) {
            return "redirect:/login";
        }

        livro.setVendedorId(vendedor.getId());

        String uploadPath = System.getProperty(
                "uploads.dir",
                System.getProperty("user.home") + "/livraria-uploads"
        );

        File pasta = new File(uploadPath);

        if (!pasta.exists()) {
            pasta.mkdirs();
        }

        String nomeArquivo = null;

        if (!arquivo.isEmpty()) {

            String contentType = arquivo.getContentType();

            if (contentType == null ||
                    !MIME_IMAGENS_PERMITIDOS.contains(contentType.toLowerCase())) {

                model.addAttribute(
                        "erro",
                        "Formato de imagem inválido."
                );

                model.addAttribute(
                        "categorias",
                        categoriaService.listar()
                );

                return "livro/form";
            }

            String nomeSeguro =
                    new File(arquivo.getOriginalFilename()).getName();

            nomeArquivo =
                    System.currentTimeMillis() + "_" + nomeSeguro;

            arquivo.transferTo(
                    new File(uploadPath, nomeArquivo)
            );
        }

        if (livro.getId() > 0) {

            Livro existente =
                    livroService.buscarPorId(livro.getId());

            if (existente == null ||
                    existente.getVendedorId() != vendedor.getId()) {

                return "redirect:/vendedor/estoque?msg=negado";
            }

            if (nomeArquivo == null) {
                livro.setImgCapa(existente.getImgCapa());
            } else {
                livro.setImgCapa(nomeArquivo);
            }

            livroService.atualizar(livro);

            return "redirect:/vendedor/estoque?msg=editado";
        }

        livro.setImgCapa(nomeArquivo);

        livroService.inserir(livro);

        return "redirect:/vendedor/estoque?msg=salvo";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(
            @PathVariable Integer id,
            HttpSession session) {

        Usuario vendedor = getVendedor(session);

        if (vendedor == null) {
            return "redirect:/login";
        }

        boolean excluido = livroService.excluir(id, vendedor.getId());

        if (excluido) {
            return "redirect:/vendedor/estoque?msg=excluido";
        }

        return "redirect:/vendedor/estoque?msg=negado";
    }

}