package com.polleria.polleria.repository;

import com.polleria.polleria.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    // buscar por nombre
    Optional<Category> findByNombre(String nombre);

    // buscar categorias activas
    List<Category> findByEstadoTrue();

    // buscar categorias por estado
    List<Category> findByEstado(Boolean estado);

    // contar productos por categoria
    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.products WHERE c.id = :id")
    Optional<Category> findByIdWithProducts(Integer id);

    // buscar categorias activas ordenadas por nombre
    @Query("SELECT c FROM Category c WHERE c.estado = true ORDER BY c.nombre ASC")
    List<Category> findActiveCategoriesOrderByName();
}