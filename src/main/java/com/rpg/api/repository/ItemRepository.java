package com.rpg.api.repository;

import com.rpg.api.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    // Busca itens cujo nome contém o termo, ignorando maiúsculas/minúsculas
    List<Item> findByNomeContainingIgnoreCase(String nome);

    // Usado para checar nome duplicado
    Optional<Item> findByNomeIgnoreCase(String nome);
}
