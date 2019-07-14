package com.pro0inter.HeyDocServer.Repositories;

import com.pro0inter.HeyDocServer.Domain.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin,Long> {

    @Query("select ad from Admin ad where ad.user.id=?1")
    Optional<Admin> findByUserId(Long user_id);

    @Query("select  count(*) from Admin ad where ad.user.id=?1")
    Long existByUserId(Long user_id);

}
