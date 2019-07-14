package com.pro0inter.HeyDocServer.Repositories;

import com.pro0inter.HeyDocServer.Domain.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient,Long> {

    @Query("select pa from Patient pa where pa.user.id=?1")
    Optional<Patient> findByUserId(Long user_id);

    @Query("select  count(*) from Patient pa where pa.user.id=?1")
    Long existByUserId(Long user_id);

}
