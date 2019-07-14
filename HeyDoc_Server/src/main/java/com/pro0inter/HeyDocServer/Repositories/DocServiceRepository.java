package com.pro0inter.HeyDocServer.Repositories;

import com.pro0inter.HeyDocServer.Domain.DocService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocServiceRepository extends JpaRepository<DocService, Long> {
    Optional<DocService> findByTitleIgnoreCase(String title);


}
