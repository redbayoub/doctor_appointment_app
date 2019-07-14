package com.pro0inter.HeyDocServer.Repositories;

import com.pro0inter.HeyDocServer.Domain.DoctorService;
import com.pro0inter.HeyDocServer.Domain.DoctorServicePK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorServiceRepository extends JpaRepository<DoctorService, DoctorServicePK> {

    @Query("select ds from DoctorService ds where ds.doctor.id=?1")
    List<DoctorService> findByDoctorId(Long doctor_id);

    @Query("select  count(*) from DoctorService ds where ds.doctor.id=?1 and ds.service.id=?2")
    Long existByDoctorIdAndDocServiceId(Long doctor_id, Long doc_service_id);
}
