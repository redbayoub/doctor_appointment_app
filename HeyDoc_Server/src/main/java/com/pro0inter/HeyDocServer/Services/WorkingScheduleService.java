package com.pro0inter.HeyDocServer.Services;

import com.pro0inter.HeyDocServer.Domain.WorkingSchedule;
import com.pro0inter.HeyDocServer.Repositories.WorkingScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WorkingScheduleService {
    @Autowired
    private WorkingScheduleRepository workingScheduleRepository;


    public WorkingSchedule get_workingSchedule(Long workingSchedule_id) {
        Optional<WorkingSchedule> optionalWorkingSchedule = workingScheduleRepository.findById(workingSchedule_id);
        if (optionalWorkingSchedule.isPresent())
            return optionalWorkingSchedule.get();
        return null;
    }

    public WorkingSchedule add_workingSchedule(WorkingSchedule workingSchedule) {
        return workingScheduleRepository.save(workingSchedule);
    }

    public WorkingSchedule update_workingSchedule(WorkingSchedule workingSchedule) {
        return workingScheduleRepository.save(workingSchedule);
    }

    public void delete_workingSchedule(Long workingSchedule_id) {
        workingScheduleRepository.deleteById(workingSchedule_id);
    }


    public List<WorkingSchedule> findAllById(List<Long> collect) {
        return workingScheduleRepository.findAllById(collect);
    }

    public List<WorkingSchedule> saveAll(List<WorkingSchedule> collect) {
        return workingScheduleRepository.saveAll(collect);
    }

    public void deleteInBatch(List<WorkingSchedule> collect) {
        workingScheduleRepository.deleteInBatch(collect);
    }
}
