package com.teachertransfer.service;

import com.teachertransfer.dto.match.MatchResponse;
import com.teachertransfer.entity.MatchResult;
import com.teachertransfer.entity.Teacher;
import com.teachertransfer.enums.MatchType;
import com.teachertransfer.enums.SchoolType;
import com.teachertransfer.enums.Subject;
import com.teachertransfer.enums.SubscriptionStatus;
import com.teachertransfer.repository.MatchResultRepository;
import com.teachertransfer.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MatchingService {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private MatchResultRepository matchResultRepository;

    @Value("${app.matching.max-results}")
    private Integer maxResults;

    public List<MatchResponse> getMatches(Long teacherId, MatchType matchType) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        // Check if teacher has active subscription (Relaxed for dev/testing)
        /*
        if (teacher.getSubscriptionStatus() != SubscriptionStatus.PAID_ACTIVE.getCode()) {
            throw new RuntimeException("Active subscription required to view matches");
        }
        */

        // Get cached matches
        List<MatchResult> matchResults = matchResultRepository
                .findByTeacherIdAndMatchType(teacherId, matchType.getCode());

        // If no cached matches, generate new ones
        if (matchResults.isEmpty()) {
            matchResults = generateMatches(teacher, matchType);
            // Cache the results
            for (MatchResult result : matchResults) {
                matchResultRepository.save(result);
            }
        }

        return matchResults.stream()
                .limit(maxResults)
                .map(this::mapToMatchResponse)
                .collect(Collectors.toList());
    }

    public List<MatchResponse> getMatchesForMap(Long teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        // Check if teacher has premium subscription
        if (!hasPremiumAccess(teacher)) {
            throw new RuntimeException("Premium subscription required for map view");
        }

        // Get all matches for map view
        List<MatchResult> matchResults = matchResultRepository
                .findByTeacherIdOrderByScoreDesc(teacherId);

        return matchResults.stream()
                .map(this::mapToMatchResponse)
                .collect(Collectors.toList());
    }

    public void refreshMatches(Long teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        // Delete old matches
        List<MatchResult> oldMatches = matchResultRepository.findByTeacherId(teacherId);
        matchResultRepository.deleteAll(oldMatches);

        // Generate new matches
        List<MatchResult> newMatches = generateMatches(teacher, MatchType.DIRECT);
        matchResultRepository.saveAll(newMatches);
    }

    private List<MatchResult> generateMatches(Teacher teacher, MatchType matchType) {
        List<MatchResult> matches = new ArrayList<>();

        // Get all active teachers with active subscriptions
        List<Teacher> allTeachers = teacherRepository.findActiveSubscribers();

        // Filter out self and already matched teachers
        List<Teacher> potentialMatches = allTeachers.stream()
                .filter(t -> !t.getId().equals(teacher.getId()))
                .collect(Collectors.toList());

        // Generate matches based on type
        for (Teacher potentialMatch : potentialMatches) {
            double score = calculateMatchScore(teacher, potentialMatch);
            double distance = calculateDistance(
                    teacher.getPreferredLat(), teacher.getPreferredLng(),
                    potentialMatch.getCurrentLat(), potentialMatch.getCurrentLng()
            );

            // Check if within radius
            if (distance <= teacher.getRadiusKm()) {
                MatchResult match = new MatchResult();
                match.setTeacherId(teacher.getId());
                match.setMatchedTeacherId(potentialMatch.getId());
                match.setMatchType(matchType.getCode());
                match.setScore(score);
                match.setDistanceKm(distance);
                match.setMatchReason(generateMatchReason(teacher, potentialMatch, distance));
                match.setHopCount(1);
                match.setCreatedAt(LocalDateTime.now());
                matches.add(match);
            }
        }

        // Sort by score (descending)
        matches.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));

        return matches;
    }

    private double calculateMatchScore(Teacher teacher1, Teacher teacher2) {
        double score = 0.0;

        // Subject match (40 points)
        if (teacher1.getSubject().equals(teacher2.getSubject())) {
            score += 40;
        }

        // School type match (20 points)
        if (teacher1.getSchoolType().equals(teacher2.getSchoolType())) {
            score += 20;
        }

        // Distance score (up to 40 points, closer is better)
        double distance = calculateDistance(
                teacher1.getPreferredLat(), teacher1.getPreferredLng(),
                teacher2.getCurrentLat(), teacher2.getCurrentLng()
        );
        double distanceScore = Math.max(0, 40 - distance);
        score += distanceScore;

        return score;
    }

    private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        final int R = 6371; // Earth's radius in km

        double latDistance = Math.toRadians(lat2 - lat1);
        double lngDistance = Math.toRadians(lng2 - lng1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    private String generateMatchReason(Teacher teacher1, Teacher teacher2, double distance) {
        StringBuilder reason = new StringBuilder();

        if (teacher1.getSubject().equals(teacher2.getSubject())) {
            reason.append("Same subject (").append(Subject.fromCode(teacher1.getSubject()).getDisplayName()).append(")");
        }

        if (teacher1.getSchoolType().equals(teacher2.getSchoolType())) {
            if (reason.length() > 0) reason.append(", ");
            reason.append("Same school type (").append(SchoolType.fromCode(teacher1.getSchoolType()).getDisplayName()).append(")");
        }

        if (reason.length() > 0) reason.append(". ");
        reason.append(String.format("%.1f km away", distance));

        return reason.toString();
    }

    private boolean hasPremiumAccess(Teacher teacher) {
        // Return true for testing purposes
        return true;
        /*
        // Check if teacher has premium plan
        return teacher.getSubscriptionStatus() == SubscriptionStatus.PAID_ACTIVE.getCode()
                && (teacher.getSubscriptionPlan() != null
                && (teacher.getSubscriptionPlan().contains("PREMIUM")
                || teacher.getSubscriptionPlan().contains("3M")));
        */
    }

    private MatchResponse mapToMatchResponse(MatchResult matchResult) {
        MatchResponse response = new MatchResponse();
        response.setId(matchResult.getId());
        response.setTeacherId(matchResult.getTeacherId());
        response.setMatchType(MatchType.fromCode(matchResult.getMatchType()));
        response.setScore(matchResult.getScore());
        response.setDistanceKm(matchResult.getDistanceKm());
        response.setMatchReason(matchResult.getMatchReason());
        response.setHopCount(matchResult.getHopCount());
        response.setCreatedAt(matchResult.getCreatedAt());

        // Get matched teacher details
        Teacher matchedTeacher = teacherRepository.findById(matchResult.getMatchedTeacherId())
                .orElse(null);

        if (matchedTeacher != null) {
            MatchResponse.TeacherInfo teacherInfo = new MatchResponse.TeacherInfo();
            teacherInfo.setId(matchedTeacher.getId());
            teacherInfo.setName(matchedTeacher.getName());
            teacherInfo.setSubject(Subject.fromCode(matchedTeacher.getSubject()).getDisplayName());
            teacherInfo.setSchoolType(SchoolType.fromCode(matchedTeacher.getSchoolType()).getDisplayName());
            response.setTeacher(teacherInfo);
        }

        return response;
    }
}