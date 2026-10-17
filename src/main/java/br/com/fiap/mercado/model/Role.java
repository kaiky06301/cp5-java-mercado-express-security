package br.com.fiap.mercado.model;

/** Tipos de usuario do sistema. */
public enum Role {
    ADMIN,    // gerente: CRUD completo de produtos + gestao de usuarios
    CLIENTE   // cliente: consulta o catalogo de produtos
}
