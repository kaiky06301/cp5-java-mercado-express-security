package br.com.fiap.mercado.controller;

import br.com.fiap.mercado.model.Role;
import br.com.fiap.mercado.repository.ProdutoRepository;
import br.com.fiap.mercado.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Area restrita do ADMIN: painel e gestao dos usuarios/perfis. */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ProdutoRepository produtoRepository;
    private final UsuarioRepository usuarioRepository;

    public AdminController(ProdutoRepository produtoRepository, UsuarioRepository usuarioRepository) {
        this.produtoRepository = produtoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public String painel(Model model) {
        var produtos = produtoRepository.findAll();
        model.addAttribute("totalProdutos", produtos.size());
        model.addAttribute("totalEstoque", produtos.stream().mapToInt(p -> p.getEstoque()).sum());
        model.addAttribute("estoqueBaixo", produtos.stream().filter(p -> p.getEstoque() < 10).count());
        model.addAttribute("totalUsuarios", usuarioRepository.count());
        return "admin/painel";
    }

    @GetMapping("/usuarios")
    public String usuarios(Model model) {
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "admin/usuarios";
    }

    /** Alterna o perfil do usuario entre ADMIN e CLIENTE. */
    @PostMapping("/usuarios/{id}/perfil")
    public String alterarPerfil(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        usuarioRepository.findById(id).ifPresent(u -> {
            if (u.getEmail().equalsIgnoreCase(auth.getName())) {
                ra.addFlashAttribute("erro", "Voce nao pode alterar o seu proprio perfil");
                return;
            }
            u.setRole(u.getRole() == Role.ADMIN ? Role.CLIENTE : Role.ADMIN);
            usuarioRepository.save(u);
            ra.addFlashAttribute("sucesso", u.getNome() + " agora e " + u.getRole());
        });
        return "redirect:/admin/usuarios";
    }
}
