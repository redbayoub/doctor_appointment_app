package com.pro0inter.HeyDocServer.Repositories;

import com.pro0inter.HeyDocServer.Domain.Speciality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpecialityRepository extends JpaRepository<Speciality, Long> {
    Optional<Speciality> findByTitleIgnoreCase(String title);

}

