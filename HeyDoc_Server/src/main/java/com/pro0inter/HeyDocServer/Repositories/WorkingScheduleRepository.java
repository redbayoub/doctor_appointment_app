package com.pro0inter.HeyDocServer.Repositories;

import com.pro0inter.HeyDocServer.Domain.WorkingSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkingScheduleRepository extends JpaRepository<WorkingSchedule, Long> {

}