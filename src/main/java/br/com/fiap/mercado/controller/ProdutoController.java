package br.com.fiap.mercado.controller;

import br.com.fiap.mercado.model.Produto;
import br.com.fiap.mercado.repository.ProdutoRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** CRUD de produtos do Mercado Express. Leitura: ADMIN e CLIENTE. Escrita: somente ADMIN. */
@Controller
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoRepository repository;

    public ProdutoController(ProdutoRepository repository) {
        this.repository = repository;
    }

    // READ - lista com busca por nome
    @GetMapping
    public String listar(@RequestParam(required = false) String busca, Model model) {
        model.addAttribute("produtos", busca == null || busca.isBlank()
                ? repository.findAllByOrderByCategoriaAscNomeAsc()
                : repository.findByNomeContainingIgnoreCaseOrderByNomeAsc(busca));
        model.addAttribute("busca", busca);
        return "produtos/lista";
    }

    // READ - detalhe de um produto
    @GetMapping("/{id}")
    public String detalhe(@PathVariable Long id, Model model, RedirectAttributes ra) {
        return repository.findById(id).map(p -> {
            model.addAttribute("produto", p);
            return "produtos/detalhe";
        }).orElseGet(() -> {
            ra.addFlashAttribute("erro", "Produto nao encontrado");
            return "redirect:/produtos";
        });
    }

    // CREATE - formulario
    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("produto", new Produto());
        return "produtos/form";
    }

    // UPDATE - formulario preenchido
    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes ra) {
        return repository.findById(id).map(p -> {
            model.addAttribute("produto", p);
            return "produtos/form";
        }).orElseGet(() -> {
            ra.addFlashAttribute("erro", "Produto nao encontrado");
            return "redirect:/produtos";
        });
    }

    // CREATE / UPDATE - grava no PostgreSQL
    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("produto") Produto produto, BindingResult result, RedirectAttributes ra) {
        if (result.hasErrors()) {
            return "produtos/form";
        }
        boolean novo = produto.getId() == null;
        repository.save(produto);
        ra.addFlashAttribute("sucesso", novo ? "Produto cadastrado com sucesso" : "Produto atualizado com sucesso");
        return "redirect:/produtos";
    }

    // DELETE
    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes ra) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            ra.addFlashAttribute("sucesso", "Produto excluido com sucesso");
        }
        return "redirect:/produtos";
    }
}
