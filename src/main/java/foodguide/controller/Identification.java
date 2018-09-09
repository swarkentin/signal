package foodguide.controller;

import foodguide.model.Gender;

import javax.validation.constraints.NotBlank;

/**
 * A POJO that parses the name-gender-age identification string used by the
 * controller to identify a person.
 */
public class Identification {
    public final String name;
    public final Gender gender;
    public final int age;

    public Identification(final String name, final Gender gender, final int age) {
        this.name = name;
        this.gender = gender;
        this.age = age;
    }

    /**
     * Identification is parsed from the pattern:
     * <p>
     * <code>
     * $NAME-$GENDER-$AGE
     * </code>
     * <p>
     * For example, <code>Steve:M:29</code> or <code>April%20ONneal:F:32</code>
     *
     * @param idString The raw id string
     * @return The parsed object
     */
    public static Identification parse(@NotBlank String idString) throws BadIdentificationException {
        final String[] parts = idString.split(":");
        if (parts.length == 3) {
            // Parse the name
            final String name = parts[0];

            // Parse the gender
            final Gender gender;
            if ("M".equalsIgnoreCase(parts[1])) {
                gender = Gender.Male;
            } else if ("F".equalsIgnoreCase(parts[1])) {
                gender = Gender.Female;
            } else {
                throw new BadIdentificationException(String.format("No gender matches %s. Expected one of 'M' or 'F'.", parts[1]));
            }

            // Parse the age
            final int age = parseAge(parts[2]);
            return new Identification(name, gender, age);
        }

        throw new BadIdentificationException("Expected <name>:<Gender>:<age> such as \"Steve:M:25\"");
    }

    private static int parseAge(final String ageString) throws BadIdentificationException {
        try {
            final int age = Integer.parseUnsignedInt(ageString);
            if (age < 2) {
                throw new BadIdentificationException(String.format("Age must be greater than or equal to 0, but was '%s'.", ageString));
            }
            return age;
        } catch (NumberFormatException ex) {
            throw new BadIdentificationException(String.format("Cannot read age - %s", ex.getMessage()));
        }
    }
}
