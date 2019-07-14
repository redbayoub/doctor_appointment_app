package com.pro0inter.HeyDocServer.Services;

import com.pro0inter.HeyDocServer.Domain.Admin;
import com.pro0inter.HeyDocServer.Domain.Doctor;
import com.pro0inter.HeyDocServer.Domain.Patient;
import com.pro0inter.HeyDocServer.Domain.User;
import com.pro0inter.HeyDocServer.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PatientService patientService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private AdminService adminService;


    public User get_user(Long user_id) {
        Optional<User> optionalUser = userRepository.findById(user_id);
        if (optionalUser.isPresent())
            return optionalUser.get();
        return null;
    }

    public User get_user(String uid) {
        Optional<User> optionalUser = userRepository.findByUid(uid);
        if (optionalUser.isPresent())
            return optionalUser.get();
        return null;
    }

    public User add_user(User user) {
        return userRepository.save(user);
    }

    public User update_user(User user) {
        return userRepository.save(user);
    }

    public void delete_user(Long user_id) {
        Patient patient = patientService.findByUserId(user_id);
        Doctor doctor = doctorService.findByUserId(user_id);
        Admin admin = adminService.findByUserId(user_id);

        if (patient != null)
            patientService.delete_patient(patient.getId());
        if (doctor != null)
            doctorService.delete_doctor(doctor.getId());
        if (admin != null)
            adminService.delete_admin(admin.getId());
        userRepository.deleteById(user_id);
    }


    public List<User> get_all_users() {
        return userRepository.findAll();
    }
}
