package com.rpg.api.controller;

import com.rpg.api.exception.ConflitoException;
import com.rpg.api.exception.RecursoNaoEncontradoException;
import com.rpg.api.model.Item;
import com.rpg.api.repository.ItemRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/itens")
public class ItemController {

    private final ItemRepository repository;

    public ItemController(ItemRepository repository) {
        this.repository = repository;
    }

    // GET /api/itens  ou  GET /api/itens?nome=espada
    @GetMapping
    public ResponseEntity<List<Item>> listar(@RequestParam(required = false) String nome) {
        List<Item> itens = (nome == null || nome.isBlank())
                ? repository.findAll()
                : repository.findByNomeContainingIgnoreCase(nome);
        return ResponseEntity.ok(itens);
    }

    // GET /api/itens/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Item> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(buscarOuFalhar(id));
    }

    // POST /api/itens
    @PostMapping
    public ResponseEntity<Item> criar(@Valid @RequestBody Item item) {
        verificarNomeDuplicado(item.getNome(), null);
        item.setId(null); // garante que será um INSERT, mesmo se o cliente enviar id
        Item salvo = repository.save(item);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(salvo.getId()).toUri();
        return ResponseEntity.created(location).body(salvo);
    }

    // PUT /api/itens/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Item> atualizar(@PathVariable Long id, @Valid @RequestBody Item dados) {
        Item existente = buscarOuFalhar(id);
        verificarNomeDuplicado(dados.getNome(), id);

        existente.setNome(dados.getNome());
        existente.setRaridade(dados.getRaridade());
        existente.setPreco(dados.getPreco());
        existente.setNivelMinimo(dados.getNivelMinimo());

        return ResponseEntity.ok(repository.save(existente));
    }

    // DELETE /api/itens/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        Item existente = buscarOuFalhar(id);
        repository.delete(existente);
        return ResponseEntity.noContent().build();
    }

    // Busca o item ou lança 404
    private Item buscarOuFalhar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Item com id " + id + " não encontrado"));
    }

    // Lança 409 se já existir outro item com o mesmo nome
    private void verificarNomeDuplicado(String nome, Long idAtual) {
        repository.findByNomeIgnoreCase(nome).ifPresent(outro -> {
            if (!outro.getId().equals(idAtual)) {
                throw new ConflitoException("Já existe um item com o nome '" + nome + "'");
            }
        });
    }
}
