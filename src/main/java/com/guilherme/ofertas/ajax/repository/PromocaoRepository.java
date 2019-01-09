package com.guilherme.ofertas.ajax.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.guilherme.ofertas.ajax.domain.Promocao;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface PromocaoRepository extends JpaRepository<Promocao, Long> {

    @Transactional(readOnly = false)
    @Modifying
    @Query("update Promocao p set p.likes = p.likes + 1 where p.id= :id")
    void updateNumLikes(@Param("id") Long id);

    @Query("select p.likes from Promocao p where p.id= :id")
    int findLikesById(@Param("id") Long id);

    @Query("select distinct p.site from Promocao p where p.site like :term%")
    List<String> findSitesByTerm(@Param("term") String term);

    @Query("select p from Promocao p where p.site like :site")
    Page<Promocao> findPromocaoBySite(@Param("site") String site, Pageable pageable);
    
    @Query("select distinct p.site from Promocao p")
    List<String> findAllSites();

    @Query("select p from Promocao p where p.titulo like :search% or p.site like :search% or p.categoria.titulo like :search%")
    Page<Promocao> findPromocaoByTituloOrSiteOrCategoria(@Param("search") String search, Pageable pageable);

    @Query("select p from Promocao p where p.preco = :preco")
    Page<Promocao> findPromocaoByPreco(@Param("preco") BigDecimal preco, Pageable pageable);
}
