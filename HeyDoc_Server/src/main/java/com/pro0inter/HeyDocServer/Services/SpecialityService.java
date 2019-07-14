package com.pro0inter.HeyDocServer.Services;

import com.pro0inter.HeyDocServer.Domain.Speciality;
import com.pro0inter.HeyDocServer.Repositories.SpecialityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SpecialityService {
    @Autowired
    private SpecialityRepository specialityRepository;


    public Speciality get_speciality(Long speciality_id) {
        Optional<Speciality> optionalSpeciality = specialityRepository.findById(speciality_id);
        if (optionalSpeciality.isPresent())
            return optionalSpeciality.get();
        return null;
    }

    public Speciality get_speciality(String title) {
        Optional<Speciality> optionalSpeciality = specialityRepository.findByTitleIgnoreCase(title);
        if (optionalSpeciality.isPresent())
            return optionalSpeciality.get();
        return null;
    }

    public List<Speciality> findAllById(List<Long> ids) {
        return specialityRepository.findAllById(ids);
    }

    public Speciality add_speciality(Speciality speciality) {
        return specialityRepository.save(speciality);
    }

    public Speciality update_speciality(Speciality speciality) {
        return specialityRepository.save(speciality);
    }

    public void delete_speciality(Long speciality_id) {
        specialityRepository.deleteById(speciality_id);
    }


    public List<Speciality> getAll() {
        return specialityRepository.findAll();
    }
}
