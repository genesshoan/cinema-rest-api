package dev.genesshoan.cinema_rest_api.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import dev.genesshoan.cinema_rest_api.entity.Movie;

public interface MovieRepository extends JpaRepository<Movie, Long> {

  public boolean existsByTitleAndReleaseDate(String title, LocalDate releaseDate);

  @Query("""
        SELECT m
        FROM Movie m
        WHERE (:title IS NULL OR LOWER(m.title) LIKE LOWER(CONCAT('%', :title, '%')))
          AND (:genre IS NULL OR LOWER(m.genre) LIKE LOWER(CONCAT('%', :genre, '%')))
      """)
  public List<Movie> search(
      @Param("title") String title,
      @Param("genre") String genre);

}
