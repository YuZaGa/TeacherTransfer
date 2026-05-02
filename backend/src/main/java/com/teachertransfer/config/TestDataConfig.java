package com.teachertransfer.config;

import com.teachertransfer.entity.Teacher;
import com.teachertransfer.entity.District;
import com.teachertransfer.entity.Block;
import com.teachertransfer.enums.SchoolType;
import com.teachertransfer.enums.Subject;
import com.teachertransfer.enums.SubscriptionStatus;
import com.teachertransfer.enums.TeacherStatus;
import com.teachertransfer.repository.BlockRepository;
import com.teachertransfer.repository.DistrictRepository;
import com.teachertransfer.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
@Profile("dev")
public class TestDataConfig {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initTestData(
            TeacherRepository teacherRepository,
            DistrictRepository districtRepository,
            BlockRepository blockRepository) {
        return args -> {
            if (teacherRepository.findByPhone("9988776655").isPresent()) {
                return;
            }

            List<District> districts = districtRepository.findAll();
            District d1 = districts.get(0);
            List<Block> blocks = blockRepository.findByDistrictId(d1.getId());

            Block b1 = blocks.get(0);
            Block b2 = blocks.get(1);

            String passwordHash = passwordEncoder.encode("password123");

            // User 1: Ramesh Singh (Maths, High)
            Teacher t1 = createTeacher(
                    "Ramesh Singh", "9988776655", "ramesh@example.com", 
                    passwordHash, Subject.MATHEMATICS, SchoolType.HIGH,
                    d1.getId().intValue(), b1.getId().intValue(), d1.getId().intValue(), b2.getId().intValue()
            );
            teacherRepository.save(t1);

            // User 2: Sulekha Kumari (Science, High)
            Teacher t2 = createTeacher(
                    "Sulekha Kumari", "8877665544", "sulekha@example.com", 
                    passwordHash, Subject.SCIENCE, SchoolType.HIGH,
                    d1.getId().intValue(), b2.getId().intValue(), d1.getId().intValue(), b1.getId().intValue()
            );
            teacherRepository.save(t2);

            System.out.println("Test Data Seeded Successfully!");
            System.out.println("User 1 Login: 9988776655 / password123 (Matches with User 2)");
            System.out.println("User 2 Login: 8877665544 / password123 (Matches with User 1)");
        };
    }

    private Teacher createTeacher(
            String name, String phone, String email, String passwordHash,
            Subject subject, SchoolType schoolType,
            Integer currentDistrict, Integer currentBlock,
            Integer preferredDistrict, Integer preferredBlock) {
        
        Teacher t = new Teacher();
        t.setName(name);
        t.setPhone(phone);
        t.setEmail(email);
        t.setPasswordHash(passwordHash);
        t.setSubject(subject.getCode());
        t.setSchoolType(schoolType.getCode());
        t.setCurrentDistrictId(currentDistrict);
        t.setCurrentBlockId(currentBlock);
        t.setPreferredDistrictId(preferredDistrict);
        t.setPreferredBlockId(preferredBlock);
        t.setStatus(TeacherStatus.ACTIVE.getCode());
        t.setOnboardingCompleted(true);
        t.setSubscriptionStatus(SubscriptionStatus.PAID_ACTIVE.getCode());
        t.setRadiusKm(50);
        t.setCreatedAt(LocalDateTime.now());
        t.setPhoneVerified(true);
        t.setReferralCode("REF" + phone.substring(6));
        
        // Mock coordinates if not derived normally
        t.setCurrentLat(25.5941);
        t.setCurrentLng(85.1376);
        t.setPreferredLat(25.5941);
        t.setPreferredLng(85.1376);
        
        return t;
    }
}
