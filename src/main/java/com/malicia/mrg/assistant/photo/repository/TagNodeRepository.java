package com.malicia.mrg.assistant.photo.repository;

import com.malicia.mrg.assistant.photo.entity.TagNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagNodeRepository extends JpaRepository<TagNode, Long> {

    Optional<TagNode> findByNameIgnoreCase(String name);

    @Query("SELECT c.name FROM TagNode t JOIN t.children c WHERE LOWER(t.name) = LOWER(:name)")
    List<String> findDirectChildrenNamesByParentName(@Param("name") String name);

    @Modifying
    @Transactional
    @Query("UPDATE TagNode t SET t.name = :name WHERE t.id = :id")
    int updateTagNameById(@Param("id") Long id, @Param("name") String name);

    @Query("SELECT t.id FROM TagNode t")
    List<Long> findAllIds();
}
