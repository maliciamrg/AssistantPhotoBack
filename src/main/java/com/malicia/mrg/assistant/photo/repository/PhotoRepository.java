package com.malicia.mrg.assistant.photo.repository;


import com.malicia.mrg.assistant.photo.entity.Photo;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface PhotoRepository extends JpaRepository<Photo, UUID> {

    // Example: find photo by hash (useful for checking duplicates)
    Optional<Photo> findByHash(String hash);

    @Modifying
    @Transactional
    @Query(value = """
        DELETE FROM photos
        WHERE id IN (
            SELECT id FROM (
                SELECT id FROM photos
                WHERE hash IN (
                    SELECT hash FROM photos
                    GROUP BY hash
                    HAVING COUNT(*) > 1
                )
            ) AS subquery
        )
    """, nativeQuery = true)
    int deletePhotosWithDuplicateHash();

    @Modifying
    @Transactional
    @Query(value = """
    DELETE FROM photo_exif WHERE photo_id IS NULL OR photo_id NOT IN (SELECT id FROM photos);
    DELETE FROM photo_thumbnail WHERE photo_id IS NULL OR photo_id NOT IN (SELECT id FROM photos);
    DELETE FROM photo_metadata WHERE photo_id IS NULL OR photo_id NOT IN (SELECT id FROM photos);
    DELETE FROM photo_filesystem WHERE photo_id IS NULL OR photo_id NOT IN (SELECT id FROM photos);
    DELETE FROM photo_keywords WHERE photo_id IS NULL OR photo_id NOT IN (SELECT id FROM photos);
""", nativeQuery = true)
    void cleanupOrphanedPhotoData();

    @Modifying
    @Transactional
    @Query(value = """
    DELETE FROM photo_exif ;
    DELETE FROM photo_thumbnail ;
    DELETE FROM photo_metadata ;
    DELETE FROM photo_filesystem ;
    DELETE FROM photo_keywords ;
    DELETE FROM photos ;
""", nativeQuery = true)
    void cleanupAllPhotoData();

    @Modifying
    @Transactional
    @Query(value = """
    DELETE FROM photo_exif where photo_id = :id;
    DELETE FROM photo_thumbnail where photo_id = :id;
    DELETE FROM photo_metadata where photo_id = :id;
    DELETE FROM photo_filesystem where photo_id = :id;
    DELETE FROM photo_keywords where photo_id = :id;
    DELETE FROM photos where id = :id;
""", nativeQuery = true)
    void cleanupPhotoData(UUID id);
}
