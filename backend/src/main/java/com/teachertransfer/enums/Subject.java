package com.teachertransfer.enums;

import lombok.Getter;

/**
 * Subject enumeration for Bihar government schools
 */
@Getter
public enum Subject {
    HINDI(1, "Hindi"),
    ENGLISH(2, "English"),
    MATHEMATICS(3, "Mathematics"),
    SCIENCE(4, "Science"),
    SOCIAL_SCIENCE(5, "Social Science"),
    SANSKRIT(6, "Sanskrit"),
    URDU(7, "Urdu"),
    PHYSICAL_EDUCATION(8, "Physical Education & Yoga"),
    ART_CRAFT(9, "Art & Craft"),
    MUSIC(10, "Music"),
    COMPUTER(11, "Computer"),
    PRIMARY_TEACHER(12, "Primary Teacher"),
    PHYSICS(13, "Physics"),
    CHEMISTRY(14, "Chemistry"),
    BIOLOGY(15, "Biology"),
    BOTANY(16, "Botany"),
    ZOOLOGY(17, "Zoology"),
    HISTORY(18, "History"),
    POLITICAL_SCIENCE(19, "Political Science / Civics"),
    GEOGRAPHY(20, "Geography"),
    ECONOMICS(21, "Economics"),
    SOCIOLOGY(22, "Sociology"),
    PHILOSOPHY(23, "Philosophy"),
    PSYCHOLOGY(24, "Psychology"),
    HOME_SCIENCE(25, "Home Science"),
    ACCOUNTANCY(26, "Accountancy"),
    BUSINESS_STUDIES(27, "Business Studies"),
    ENTREPRENEURSHIP(28, "Entrepreneurship"),
    COMPUTER_SCIENCE(29, "Computer Science"),
    INFORMATICS_PRACTICES(30, "Informatics Practices"),
    MULTIMEDIA_WEB_TECH(31, "Multimedia & Web Technology"),
    DATA_SCIENCE(32, "Data Science"),
    PALI(33, "Pali"),
    PRAKRIT(34, "Prakrit"),
    PERSIAN(35, "Persian / Farsi"),
    ARABIC(36, "Arabic"),
    MAITHILI(37, "Maithili"),
    BHOJPURI(38, "Bhojpuri"),
    MAGAHI(39, "Magahi"),
    BENGALI(40, "Bangla / Bengali"),
    ENVIRONMENTAL_STUDIES(41, "Environmental Studies (EVS)"),
    AGRICULTURE(42, "Agriculture"),
    HORTICULTURE(43, "Horticulture & Floriculture"),
    FINE_ARTS(44, "Fine Arts / Painting"),
    DANCE(45, "Dance"),
    FASHION_STUDIES(46, "Fashion Studies"),
    FOOD_PRODUCTION(47, "Food Production"),
    BEAUTY_WELLNESS(48, "Beauty and Wellness"),
    HEALTHCARE(49, "Healthcare"),
    TOURISM(50, "Tourism"),
    MASS_MEDIA(51, "Mass Media Studies"),
    LIBRARY_SCIENCE(52, "Library and Information Science"),
    MARKETING(53, "Marketing"),
    TAXATION(54, "Taxation"),
    BANKING_INSURANCE(55, "Banking & Insurance"),
    RETAIL(56, "Retail"),
    AUTOMOTIVE(57, "Automotive"),
    ELECTRICAL_ELECTRONICS(58, "Electrical & Electronics Technology"),
    STENOGRAPHY(59, "Stenography / Shorthand (Hindi/English)"),
    OTHER(99, "Other");

    private final int code;
    private final String displayName;

    Subject(int code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public static Subject fromCode(int code) {
        for (Subject subject : values()) {
            if (subject.code == code) {
                return subject;
            }
        }
        return OTHER;
    }
}
