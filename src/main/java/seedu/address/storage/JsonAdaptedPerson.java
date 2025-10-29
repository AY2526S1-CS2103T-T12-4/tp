package seedu.address.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Budget;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonId;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Website;
import seedu.address.model.tag.Tag;

/**
 * Jackson-friendly version of {@link Person}.
 */
class JsonAdaptedPerson {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Person's %s field is missing!";

    private final String id;
    private final String name;
    private final String phone;
    private final String email;
    private final String website;
    private final String budget;
    private final List<JsonAdaptedTag> tags = new ArrayList<>();

    /**
     * Constructs a {@code JsonAdaptedPerson} with the given person details.
     */
    @JsonCreator
    public JsonAdaptedPerson(@JsonProperty("id") String id, @JsonProperty("name") String name,
            @JsonProperty("phone") String phone, @JsonProperty("email") String email,
            @JsonProperty("website") String website, @JsonProperty("tags") List<JsonAdaptedTag> tags,
            @JsonProperty("budget") String budget) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.website = website;
        if (tags != null) {
            this.tags.addAll(tags);
        }
        this.budget = budget;
    }

    /**
     * Converts a given {@code Person} into this class for Jackson use.
     */
    public JsonAdaptedPerson(Person source) {
        id = source.getId().toString();
        name = source.getName().fullName;
        phone = source.getPhone().value;
        email = source.getEmail().value;
        website = source.getWebsite().value;
        tags.addAll(source.getTags().stream()
                .map(JsonAdaptedTag::new)
                .collect(Collectors.toList()));
        budget = source.getBudget().value;
    }

    /**
     * Converts this Jackson-friendly adapted person object into the model's {@code Person} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted person.
     */
    public Person toModelType() throws IllegalValueException {
        final List<Tag> personTags = new ArrayList<>();
        for (JsonAdaptedTag tag : tags) {
            personTags.add(tag.toModelType());
        }

        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }
        if (!Name.isValidName(name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        final Name modelName = new Name(name);

        if (phone == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName()));
        }
        if (!Phone.isValidPhone(phone)) {
            throw new IllegalValueException(Phone.MESSAGE_CONSTRAINTS);
        }
        final Phone modelPhone = new Phone(phone);

        if (email == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName()));
        }
        if (!Email.isValidEmail(email)) {
            throw new IllegalValueException(Email.MESSAGE_CONSTRAINTS);
        }
        final Email modelEmail = new Email(email);

        final Website modelWebsite = (website == null || website.isEmpty()) ? new Website("") : new Website(website);

        final Budget modelBudget;
        if (budget == null) {
            modelBudget = new Budget("0");
        } else {
            if (!Budget.isValidBudget(budget)) {
                throw new IllegalValueException(Budget.MESSAGE_CONSTRAINTS);
            }
            modelBudget = new Budget(budget);
        }

        final Set<Tag> modelTags = new HashSet<>(personTags);

        // If id was not present in JSON (older files), create a new Person with a generated id.
        if (id == null || id.isEmpty()) {
            return new Person(modelName, modelPhone, modelEmail, modelWebsite, modelTags, modelBudget);
        } else {
            PersonId modelId;
            try {
                modelId = new PersonId(id);
            } catch (IllegalArgumentException e) {
                // If the ID in JSON is invalid, generate a new one instead of crashing
                System.err.println("Warning: Invalid PersonId '" + id + "' found in JSON. Generating new ID.");
                modelId = new PersonId();
            }
            return new Person(modelId, modelName, modelPhone, modelEmail, modelWebsite, modelTags, modelBudget);
        }
    }

}
