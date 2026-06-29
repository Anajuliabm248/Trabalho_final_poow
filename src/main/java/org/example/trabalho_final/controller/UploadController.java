package org.example.trabalho_final.controller;

import jakarta.servlet.ServletContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class UploadController {

    private final ServletContext servletContext;

    public UploadController(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @GetMapping("/uploads/{nomeArquivo:.+}")
    public ResponseEntity<byte[]> exibirImagem(
            @PathVariable String nomeArquivo) throws IOException {

        String baseDir = System.getProperty(
                "uploads.dir",
                System.getProperty("user.home") + "/livraria-uploads"
        );

        // Segurança contra Path Traversal
        nomeArquivo = new File(nomeArquivo).getName();

        Path arquivo = Paths.get(baseDir, nomeArquivo);

        if (!Files.exists(arquivo)) {
            return ResponseEntity.notFound().build();
        }

        String contentType = servletContext.getMimeType(nomeArquivo);

        if (contentType == null) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .contentLength(Files.size(arquivo))
                .body(Files.readAllBytes(arquivo));
    }
}