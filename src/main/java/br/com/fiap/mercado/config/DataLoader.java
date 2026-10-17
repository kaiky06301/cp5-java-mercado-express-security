package br.com.fiap.mercado.config;

import br.com.fiap.mercado.model.Produto;
import br.com.fiap.mercado.model.Role;
import br.com.fiap.mercado.repository.ProdutoRepository;
import br.com.fiap.mercado.repository.UsuarioRepository;
import br.com.fiap.mercado.service.UsuarioService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/** Cria os usuarios de demonstracao e alguns produtos na primeira execucao. */
@Component
public class DataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final ProdutoRepository produtoRepository;
    private final UsuarioService usuarioService;

    public DataLoader(UsuarioRepository usuarioRepository, ProdutoRepository produtoRepository, UsuarioService usuarioService) {
        this.usuarioRepository = usuarioRepository;
        this.produtoRepository = produtoRepository;
        this.usuarioService = usuarioService;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() == 0) {
            usuarioService.cadastrar("Gerente Mercado", "admin@mercadoexpress.com", "admin123", Role.ADMIN);
            usuarioService.cadastrar("Cliente Demo", "cliente@mercadoexpress.com", "cliente123", Role.CLIENTE);
        }
        if (produtoRepository.count() == 0) {
            salvar("Banana Prata (kg)", "Hortifruti", "Banana prata fresca, vendida por quilo", "6.99", 80);
            salvar("Maca Gala (kg)", "Hortifruti", "Maca gala nacional", "9.90", 60);
            salvar("Morango (bandeja 250g)", "Hortifruti", "Morango selecionado", "8.49", 35);
            salvar("Detergente Neutro 500ml", "Limpeza", "Detergente liquido neutro", "2.79", 120);
            salvar("Sabao em Po 1kg", "Limpeza", "Sabao em po multiacao", "15.90", 45);
            salvar("Kit 3 Meias Esportivas", "Vestuario", "Meias cano medio, tamanho unico", "29.90", 25);
            salvar("Leite Integral 1L", "Mercearia", "Leite UHT integral", "5.49", 90);
        }
    }

    private void salvar(String nome, String categoria, String descricao, String preco, int estoque) {
        produtoRepository.save(Produto.builder().nome(nome).categoria(categoria).descricao(descricao)
                .preco(new BigDecimal(preco)).estoque(estoque).build());
    }
}
