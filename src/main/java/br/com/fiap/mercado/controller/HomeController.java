package br.com.fiap.mercado.controller;

import br.com.fiap.mercado.model.Role;
import br.com.fiap.mercado.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeController {

    private final UsuarioService usuarioService;

    public HomeController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /** Landing page: index.html */
    @GetMapping({"/", "/index"})
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /** Tela de Sign Up (cadastro de novo cliente). */
    @GetMapping("/cadastro")
    public String cadastro() {
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String cadastrar(@RequestParam String nome, @RequestParam String email,
                            @RequestParam String senha, @RequestParam String confirmacao,
                            RedirectAttributes ra) {
        if (nome.isBlank() || email.isBlank() || senha.length() < 6) {
            ra.addFlashAttribute("erro", "Preencha todos os campos (senha com no minimo 6 caracteres)");
            ra.addFlashAttribute("email", email);
            return "redirect:/cadastro";
        }
        if (!senha.equals(confirmacao)) {
            ra.addFlashAttribute("erro", "As senhas nao conferem");
            ra.addFlashAttribute("email", email);
            return "redirect:/cadastro";
        }
        try {
            usuarioService.cadastrar(nome, email, senha, Role.CLIENTE);
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("erro", e.getMessage());
            return "redirect:/cadastro";
        }
        ra.addFlashAttribute("sucesso", "Cadastro realizado! Agora faca o login.");
        return "redirect:/login";
    }

    @GetMapping("/acesso-negado")
    public String acessoNegado() {
        return "acesso-negado";
    }
}
