package br.com.fiap.mercado.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "TDS_Sec_MVC_TB_Mercado")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Informe o nome do produto")
    @Size(max = 100)
    private String nome;

    @NotBlank(message = "Informe a categoria")
    private String categoria;

    @Size(max = 255)
    private String descricao;

    @NotNull(message = "Informe o preco")
    @DecimalMin(value = "0.01", message = "Preco deve ser maior que zero")
    @Column(precision = 10, scale = 2)
    private BigDecimal preco;

    @NotNull(message = "Informe o estoque")
    @Min(value = 0, message = "Estoque nao pode ser negativo")
    private Integer estoque;
}
