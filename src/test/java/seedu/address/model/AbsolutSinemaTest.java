package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_WEBSITE_BOB;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.getTypicalAbsolutSinema;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.person.Person;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.testutil.PersonBuilder;

public class AbsolutSinemaTest {

    private final AbsolutSinema absolutSinema = new AbsolutSinema();

    @Test
    public void constructor() {
        assertEquals(Collections.emptyList(), absolutSinema.getPersonList());
    }

    @Test
    public void resetData_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> absolutSinema.resetData(null));
    }

    @Test
    public void resetData_withValidReadOnlyAbsolutSinema_replacesData() {
        AbsolutSinema newData = getTypicalAbsolutSinema();
        absolutSinema.resetData(newData);
        assertEquals(newData, absolutSinema);
    }

    @Test
    public void resetData_withDuplicatePersons_throwsDuplicatePersonException() {
        // Two persons with the same identity fields
        Person editedAlice = new PersonBuilder(ALICE).withWebsite(VALID_WEBSITE_BOB).withTags(VALID_TAG_HUSBAND)
                .build();
        List<Person> newPersons = Arrays.asList(ALICE, editedAlice);
        AbsolutSinemaStub newData = new AbsolutSinemaStub(newPersons);

        assertThrows(DuplicatePersonException.class, () -> absolutSinema.resetData(newData));
    }

    @Test
    public void hasPerson_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> absolutSinema.hasPerson(null));
    }

    @Test
    public void hasPerson_personNotInAbsolutSinema_returnsFalse() {
        assertFalse(absolutSinema.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personInAbsolutSinema_returnsTrue() {
        absolutSinema.addPerson(ALICE);
        assertTrue(absolutSinema.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personWithSameIdentityFieldsInAbsolutSinema_returnsTrue() {
        absolutSinema.addPerson(ALICE);
        Person editedAlice = new PersonBuilder(ALICE).withWebsite(VALID_WEBSITE_BOB).withTags(VALID_TAG_HUSBAND)
                .build();
        assertTrue(absolutSinema.hasPerson(editedAlice));
    }

    @Test
    public void getPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> absolutSinema.getPersonList().remove(0));
    }

    @Test
    public void toStringMethod() {
        String expected = AbsolutSinema.class.getCanonicalName() + "{persons=" + absolutSinema.getPersonList() + "}";
        assertEquals(expected, absolutSinema.toString());
    }

    /**
     * A stub ReadOnlyAbsolutSinema whose persons list can violate interface constraints.
     */
    private static class AbsolutSinemaStub implements ReadOnlyAbsolutSinema {
        private final ObservableList<Person> persons = FXCollections.observableArrayList();

        AbsolutSinemaStub(Collection<Person> persons) {
            this.persons.setAll(persons);
        }

        @Override
        public ObservableList<Person> getPersonList() {
            return persons;
        }

        @Override
        public ObservableList<seedu.address.model.event.Event> getEventList() {
            return FXCollections.observableArrayList();
        }
    }

}
