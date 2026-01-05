package dev.genesshoan.cinema_rest_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.genesshoan.cinema_rest_api.entity.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {
  public boolean existsByName(String name);
}
