package dev.genesshoan.cinema_rest_api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name = "rooms", indexes = {
    @Index(name = "idx_room_name", columnList = "name", unique = true)
})
public class Room {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Column(nullable = false, length = 255)
  String name;

  @Column(nullable = false)
  Integer rows;

  @Column(name = "seats_per_row", nullable = false)
  Integer seatsPerRow;

  public Room() {
  }

  public Room(Long id, String name, Integer rows, Integer seatsPerRow) {
    this.id = id;
    this.name = name;
    this.rows = rows;
    this.seatsPerRow = seatsPerRow;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getRows() {
    return rows;
  }

  public void setRows(Integer rows) {
    this.rows = rows;
  }

  public Integer getSeatsPerRow() {
    return seatsPerRow;
  }

  public void setSeatsPerRow(Integer seatsPerRow) {
    this.seatsPerRow = seatsPerRow;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Room other = (Room) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    return true;
  }
}
