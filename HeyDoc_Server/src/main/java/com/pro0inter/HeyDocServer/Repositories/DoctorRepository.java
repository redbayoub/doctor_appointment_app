package com.pro0inter.HeyDocServer.Repositories;

import com.pro0inter.HeyDocServer.Domain.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor,Long> {

    @Query("select doc from Doctor doc where doc.user.id=?1")
    Optional<Doctor> findByUserId(Long user_id);

    @Query("select  count(*) from Doctor doc where doc.user.id=?1")
    Long existByUserId(Long user_id);
}

