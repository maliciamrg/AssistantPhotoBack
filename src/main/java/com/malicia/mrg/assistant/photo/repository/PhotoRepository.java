package com.malicia.mrg.assistant.photo.repository;


import com.malicia.mrg.assistant.photo.entity.Photo;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
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
    @Query("DELETE FROM PhotoExifData exif WHERE exif.photo IS NULL")
    void deleteOrphanedExif();

    @Modifying
    @Query("DELETE FROM PhotoThumbnail thumb WHERE thumb.photo.id IS NULL")
    void deleteOrphanedThumbnails();

    @Modifying
    @Query("DELETE FROM PhotoMetadata meta WHERE meta.photo.id IS NULL")
    void deleteOrphanedMetadata();

    @Modifying
    @Query("DELETE FROM PhotoFileSystem fs WHERE fs.photo.id IS NULL")
    void deleteOrphanedFilesystem();

    @Transactional
    default void cleanupOrphanedPhotoData() {
        deleteOrphanedExif();
        deleteOrphanedThumbnails();
        deleteOrphanedMetadata();
        deleteOrphanedFilesystem();
    }

    @Modifying
    @Query("DELETE FROM PhotoExifData")
    void deleteAllExif();

    @Modifying
    @Query("DELETE FROM PhotoThumbnail ")
    void deleteAllThumbnails();

    @Modifying
    @Query("DELETE FROM PhotoMetadata")
    void deleteAllMetadata();

    @Modifying
    @Query("DELETE FROM PhotoFileSystem ")
    void deleteAllFilesystem();

    @Modifying
    @Query("DELETE FROM Photo")
    void deleteAllPhotos();

    @Transactional
    default void cleanupAllPhotoData() {
        deleteAllExif();
        deleteAllThumbnails();
        deleteAllMetadata();
        deleteAllFilesystem();
        deleteAllPhotos();
    }

    @Modifying
    @Query("DELETE FROM PhotoExifData WHERE photo.id = :id")
    void deleteExif(@Param("id") UUID id);

    @Modifying
    @Query("DELETE FROM PhotoThumbnail WHERE photo.id = :id")
    void deleteThumbnail(@Param("id") UUID id);

    @Modifying
    @Query("DELETE FROM PhotoMetadata WHERE photo.id = :id")
    void deleteMetadata(@Param("id") UUID id);

    @Modifying
    @Query("DELETE FROM PhotoFileSystem WHERE photo.id = :id")
    void deleteFilesystem(@Param("id") UUID id);

    @Modifying
    @Query("DELETE FROM Photo p WHERE p.id = :id")
    void deletePhoto(@Param("id") UUID id);

    @Transactional
    default void cleanupPhotoData(UUID id) {
        deleteExif(id);
        deleteThumbnail(id);
        deleteMetadata(id);
        deleteFilesystem(id);
        deletePhoto(id);
    }

    @Query("SELECT pf.photo.id FROM PhotoFileSystem pf WHERE pf.path LIKE %:pathPattern%")
    List<UUID> findPhotoIdsByPathPattern(@Param("pathPattern") String pathPattern);
}
