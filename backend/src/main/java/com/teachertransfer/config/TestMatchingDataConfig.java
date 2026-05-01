package com.teachertransfer.config;

import com.teachertransfer.entity.Teacher;
import com.teachertransfer.entity.TeacherGeoIndex;
import com.teachertransfer.enums.SchoolType;
import com.teachertransfer.enums.Subject;
import com.teachertransfer.enums.SubscriptionStatus;
import com.teachertransfer.enums.TeacherStatus;
import com.teachertransfer.repository.TeacherGeoIndexRepository;
import com.teachertransfer.repository.TeacherRepository;
import com.teachertransfer.util.GeohashUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
@Profile("dev")
public class TestMatchingDataConfig {

    record TeacherSeed(
            String name, String phone, String email,
            Subject subject, SchoolType schoolType,
            double currentLat, double currentLng,
            double preferredLat, double preferredLng,
            int radiusKm,
            String scenario
    ) {}

    @Bean
    CommandLineRunner initMatchingTestData(
            TeacherRepository teacherRepository,
            TeacherGeoIndexRepository geoIndexRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            List<String> testPhones = List.of(
                    "9999990001", "9999990002", "9999990003",
                    "9999990004", "9999990005", "9999990006",
                    "9999990007"
            );

            boolean anyMissing = false;
            for (String phone : testPhones) {
                if (teacherRepository.findByPhone(phone).isEmpty()) {
                    anyMissing = true;
                    break;
                }
            }
            if (!anyMissing) {
                ensureGeoIndex(teacherRepository, geoIndexRepository, "9988776655", Subject.MATHEMATICS, SchoolType.HIGH);
                ensureGeoIndex(teacherRepository, geoIndexRepository, "8877665544", Subject.SCIENCE, SchoolType.HIGH);
                return;
            }

            ensureGeoIndex(teacherRepository, geoIndexRepository, "9988776655", Subject.MATHEMATICS, SchoolType.HIGH);
            ensureGeoIndex(teacherRepository, geoIndexRepository, "8877665544", Subject.SCIENCE, SchoolType.HIGH);

            String ph = passwordEncoder.encode("password123");

            // Ramesh's fixed coords from TestDataConfig: current=25.5941,85.1376; preferred=25.5941,85.1376; radius=50
            // For discovery match: candidate current must be within 50km of 25.5941,85.1376
            //   AND candidate preferred geohash must be in search area around 25.5941,85.1376
            // For mutual match: candidate preferred must be within candidate radius of Ramesh's current (25.5941,85.1376)
            // For discovery-only: candidate radius small enough to fail mutual check

            List<TeacherSeed> seeds = List.of(
                    // TC1: Discovery + Mutual (MATHS, HIGH, preferred same as Ramesh's → guaranteed match)
                    new TeacherSeed("Amit (Match)", "9999990001", "amit@test.com",
                            Subject.MATHEMATICS, SchoolType.HIGH,
                            25.5780, 85.0719,  // Current: Phulwari Sharif (~6km from Patna center)
                            25.5941, 85.1376,  // Preferred: same as Ramesh's
                            50, "DISCOVERY+MUTUAL"),
                    // TC2: Same as TC1
                    new TeacherSeed("Sunita (Match)", "9999990002", "sunita@test.com",
                            Subject.MATHEMATICS, SchoolType.HIGH,
                            25.5780, 85.0719,
                            25.5941, 85.1376,
                            50, "DISCOVERY+MUTUAL"),
                    // TC3: Wrong subject (SCIENCE ≠ MATHS)
                    new TeacherSeed("Vijay (No-Subject)", "9999990003", "vijay@test.com",
                            Subject.SCIENCE, SchoolType.HIGH,
                            25.5780, 85.0719,
                            25.5941, 85.1376,
                            50, "NO_MATCH_SUBJECT"),
                    // TC4: Wrong school type (MIDDLE ≠ HIGH)
                    new TeacherSeed("Priya (No-Type)", "9999990004", "priya@test.com",
                            Subject.MATHEMATICS, SchoolType.MIDDLE,
                            25.5780, 85.0719,
                            25.5941, 85.1376,
                            50, "NO_MATCH_TYPE"),
                    // TC5: Too far (Gaya ~90km)
                    new TeacherSeed("Rahul (No-Distance)", "9999990005", "rahul@test.com",
                            Subject.MATHEMATICS, SchoolType.HIGH,
                            24.6961, 84.9912,  // Current: Bodhgaya
                            25.5941, 85.1376,
                            50, "NO_MATCH_DISTANCE"),
                    // TC6: Discovery but NOT mutual (preferred 1.3km from Ramesh's, radius=1km)
                    // preferred=25.587,85.125 is ~1.3km from Ramesh's 25.5941,85.1376
                    // → still within geohash neighbor grid → passes pre-filter
                    // → reverse distance 1.3km > radius 1km → NOT mutual
                    new TeacherSeed("Pooja (Disc-Only)", "9999990006", "pooja@test.com",
                            Subject.MATHEMATICS, SchoolType.HIGH,
                            25.5780, 85.0719,  // Current: near Ramesh's preferred
                            25.5870, 85.1250,  // Preferred: 1.3km from Ramesh's preferred
                            1,                 // Tiny radius: fails mutual check
                            "DISCOVERY_ONLY"),
                    // TC7: Match (same as TC1/TC2)
                    new TeacherSeed("Anjali (Match)", "9999990007", "anjali@test.com",
                            Subject.MATHEMATICS, SchoolType.HIGH,
                            25.5458, 85.1614,  // Current: Sampatchak
                            25.5941, 85.1376,
                            50, "DISCOVERY+MUTUAL")
            );

            for (TeacherSeed s : seeds) {
                Teacher t = new Teacher();
                t.setName(s.name);
                t.setPhone(s.phone);
                t.setEmail(s.email);
                t.setPasswordHash(ph);
                t.setSubject(s.subject.getCode());
                t.setSchoolType(s.schoolType.getCode());
                t.setCurrentDistrictId(26);
                t.setCurrentBlockId(2623);
                t.setPreferredDistrictId(26);
                t.setPreferredBlockId(2621);
                t.setCurrentLat(s.currentLat);
                t.setCurrentLng(s.currentLng);
                t.setPreferredLat(s.preferredLat);
                t.setPreferredLng(s.preferredLng);
                t.setRadiusKm(s.radiusKm);
                t.setStatus(TeacherStatus.ACTIVE.getCode());
                t.setSubscriptionStatus(SubscriptionStatus.PAID_ACTIVE.getCode());
                t.setPhoneVerified(true);
                t.setCreatedAt(LocalDateTime.now());
                t.setProfileUpdatedAt(LocalDateTime.now());
                t.setLastInteractionAt(LocalDateTime.now());
                t.setReferralCode("REF" + s.phone.substring(6));
                t = teacherRepository.save(t);

                TeacherGeoIndex gi = new TeacherGeoIndex();
                gi.setTeacherId(t.getId());
                gi.setGeohash(GeohashUtil.encode(s.preferredLat, s.preferredLng));
                gi.setSubject(s.subject.getCode());
                gi.setSchoolType(s.schoolType.getCode());
                gi.setCurrentLat(s.currentLat);
                gi.setCurrentLng(s.currentLng);
                gi.setPreferredLat(s.preferredLat);
                gi.setPreferredLng(s.preferredLng);
                gi.setRadiusKm(s.radiusKm);
                gi.setIsPremium(true);
                geoIndexRepository.save(gi);

                System.out.println("Created: " + s.name + " (" + s.scenario + ")");
            }

            System.out.println("\n=== Matching Test Accounts (all login: phone / password123) ===");
            System.out.println("Ramesh (base):\t9988776655");
            System.out.println("Amit:\t\t9999990001  → DISCOVERY+MUTUAL match");
            System.out.println("Sunita:\t\t9999990002  → DISCOVERY+MUTUAL match");
            System.out.println("Vijay:\t\t9999990003  → NO match (SCIENCE)");
            System.out.println("Priya:\t\t9999990004  → NO match (MIDDLE)");
            System.out.println("Rahul:\t\t9999990005  → NO match (Gaya ~90km)");
            System.out.println("Pooja:\t\t9999990006  → DISCOVERY only, NOT mutual");
            System.out.println("Anjali:\t\t9999990007  → DISCOVERY+MUTUAL match");
            System.out.println("============================================");
        };
    }

    private void ensureGeoIndex(
            TeacherRepository teacherRepository,
            TeacherGeoIndexRepository geoIndexRepository,
            String phone, Subject subject, SchoolType schoolType) {
        var opt = teacherRepository.findByPhone(phone);
        if (opt.isEmpty()) return;
        Teacher t = opt.get();
        if (!geoIndexRepository.findByTeacherId(t.getId()).isEmpty()) return;
        if (t.getPreferredLat() == null) return;

        TeacherGeoIndex gi = new TeacherGeoIndex();
        gi.setTeacherId(t.getId());
        gi.setGeohash(GeohashUtil.encode(t.getPreferredLat(), t.getPreferredLng()));
        gi.setSubject(t.getSubject() != null ? t.getSubject() : subject.getCode());
        gi.setSchoolType(t.getSchoolType() != null ? t.getSchoolType() : schoolType.getCode());
        gi.setCurrentLat(t.getCurrentLat());
        gi.setCurrentLng(t.getCurrentLng());
        gi.setPreferredLat(t.getPreferredLat());
        gi.setPreferredLng(t.getPreferredLng());
        gi.setRadiusKm(t.getRadiusKm() != null ? t.getRadiusKm() : 50);
        gi.setIsPremium(true);
        geoIndexRepository.save(gi);
        System.out.println("Added GeoIndex for existing teacher: " + t.getPhone());
    }
}
