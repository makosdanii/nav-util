package com.navutil.server.data.repositories;

import com.navutil.server.data.entities.Marker;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarkerRepository extends CrudRepository<Marker, Integer> {
}
