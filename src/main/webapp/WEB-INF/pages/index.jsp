<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Livraria Antiqua</title>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css"
          crossorigin="anonymous">

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/styles.css">
</head>

<body>

<c:set var="activeNav" value="home" scope="request"/>
<jsp:include page="/WEB-INF/pages/included/navbar.jsp"/>

<main class="page-wrap pb-5">

    <section class="hero-banner mt-4">
        <p class="section-kicker text-white-50">Bem-vindo</p>

        <h1 class="page-title text-white">
            Livraria Antiqua
        </h1>

        <p class="page-subtitle">
            Descubra obras raras, clássicos inesquecíveis e novos mundos para explorar.
        </p>

        <div class="hero-actions mt-4">
            <a class="btn btn-brand" href="#acervo">
                <i class="bi bi-book-half me-2"></i>
                Explorar livros
            </a>

            <a class="btn btn-ghost text-white border-white"
               href="${pageContext.request.contextPath}/login">
                <i class="bi bi-box-arrow-in-right"></i>
                Entrar
            </a>
        </div>
    </section>

    <section class="mt-5" id="acervo">

        <div class="d-flex justify-content-between align-items-end mb-4">

            <div>
                <p class="section-kicker mb-1">Destaques</p>
                <h2 class="section-title mb-0">
                    Livros em destaque
                </h2>
            </div>

            <a class="btn btn-outline-dark"
               href="${pageContext.request.contextPath}/livros">
                Ver catálogo completo
            </a>

        </div>

        <c:if test="${empty livros}">
            <div class="empty-state">
                Nenhum livro disponível no momento.
            </div>
        </c:if>

        <div class="grid-cards">

            <c:forEach var="livro" items="${livros}">

                <article class="book-card">

                    <div class="book-card__cover">

                        <c:choose>

                            <c:when test="${not empty livro.imgCapa}">
                                <img src="${pageContext.request.contextPath}/uploads/${livro.imgCapa}"
                                     alt="${livro.nome}">
                            </c:when>

                            <c:otherwise>
                                <div class="text-center px-3">
                                    <i class="bi bi-image fs-1 text-secondary"></i>
                                    <div class="muted mt-2">Sem capa</div>
                                </div>
                            </c:otherwise>

                        </c:choose>

                    </div>

                    <div class="book-card__body">

                        <div>
                            <h3 class="book-card__title">
                                    ${livro.nome}
                            </h3>

                            <div class="book-card__meta">
                                    ${livro.autor}
                            </div>
                        </div>

                        <div class="book-card__footer">

                            <div>
                                <div class="price">
                                    R$ ${livro.preco}
                                </div>

                                <div class="stock">
                                    Estoque: ${livro.quantidade}
                                </div>
                            </div>

                            <a class="card-action"
                               href="${pageContext.request.contextPath}/livros/${livro.id}"
                               title="Ver detalhes">

                                <i class="bi bi-arrow-right"></i>

                            </a>

                        </div>

                    </div>

                </article>

            </c:forEach>

        </div>

        <div class="text-center mt-5">

            <a class="btn btn-brand btn-lg"
               href="${pageContext.request.contextPath}/livros">
                <i class="bi bi-book me-2"></i>
                Ver catálogo completo
            </a>

        </div>

    </section>

</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"
        crossorigin="anonymous"></script>

</body>
</html>