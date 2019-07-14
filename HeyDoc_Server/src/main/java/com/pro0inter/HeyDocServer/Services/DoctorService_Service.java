package com.pro0inter.HeyDocServer.Services;

import com.pro0inter.HeyDocServer.Domain.DoctorService;
import com.pro0inter.HeyDocServer.Domain.DoctorServicePK;
import com.pro0inter.HeyDocServer.Repositories.DoctorServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DoctorService_Service {
    @Autowired
    private DoctorServiceRepository doctorServiceRepository;


    public DoctorService get_doctorService(DoctorServicePK doctorService_id) {
        Optional<DoctorService> optionalDoctorService = doctorServiceRepository.findById(doctorService_id);
        if (optionalDoctorService.isPresent())
            return optionalDoctorService.get();
        return null;
    }

    public DoctorService add_doctorService(DoctorService doctorService) {
        return doctorServiceRepository.save(doctorService);
    }

    public DoctorService update_doctorService(DoctorService doctorService) {
        return doctorServiceRepository.save(doctorService);
    }

    public void delete_doctorService(DoctorServicePK doctorService_id) {
        doctorServiceRepository.deleteById(doctorService_id);
    }

    public boolean existByDocIdAndDocServiceId(Long doctor_id, Long doctor_service_id) {
        return (doctorServiceRepository.existByDoctorIdAndDocServiceId(doctor_id, doctor_service_id) > 0);
    }


    public List<DoctorService> saveAll(List<DoctorService> services) {
        return doctorServiceRepository.saveAll(services);
    }

    public List<DoctorService> get_by_doctor_id(Long doctor_id) {
        return doctorServiceRepository.findByDoctorId(doctor_id);
    }

    public void deleteAllByDoctorId(Long id) {
        doctorServiceRepository.deleteInBatch(doctorServiceRepository.findByDoctorId(id));
        doctorServiceRepository.flush();
    }
}
