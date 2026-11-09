package com.rpg.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Entity
@Table(name = "itens")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres")
    @Column(nullable = false, unique = true, length = 100)
    private String nome;

    @NotNull(message = "A raridade é obrigatória (COMUM, INCOMUM, RARO, EPICO ou LENDARIO)")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Raridade raridade;

    @NotNull(message = "O preço é obrigatório")
    @PositiveOrZero(message = "O preço não pode ser negativo")
    @Column(nullable = false)
    private BigDecimal preco;

    @Min(value = 1, message = "O nível mínimo deve ser no mínimo 1")
    @Max(value = 100, message = "O nível mínimo deve ser no máximo 100")
    private Integer nivelMinimo;

    public Item() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Raridade getRaridade() { return raridade; }
    public void setRaridade(Raridade raridade) { this.raridade = raridade; }

    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }

    public Integer getNivelMinimo() { return nivelMinimo; }
    public void setNivelMinimo(Integer nivelMinimo) { this.nivelMinimo = nivelMinimo; }
}
