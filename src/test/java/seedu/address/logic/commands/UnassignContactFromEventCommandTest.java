package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.event.Event;
import seedu.address.model.event.EventDate;
import seedu.address.model.event.EventName;
import seedu.address.model.event.EventTime;
import seedu.address.model.person.Person;
import seedu.address.testutil.TypicalPersons;

/**
 * Tests for {@link UnassignContactFromEventCommand}.
 */
public class UnassignContactFromEventCommandTest {

    private Model model;
    private Event eventOne;

    @BeforeEach
    public void setUp() {
        model = new ModelManager();

        // create two events
        eventOne = new Event(new EventName("John's Party"), new EventDate("01-01-2024"),
                new EventTime("12:00"));
        Event eventTwo = new Event(new EventName("Jane's Party"), new EventDate("02-02-2024"),
                new EventTime("18:00"));

        // add two persons to the model
        Person person1 = TypicalPersons.ALICE;
        Person person2 = TypicalPersons.BENSON;
        model.addPerson(person1);
        model.addPerson(person2);

        // assign both persons to eventOne initially
        eventOne.getParticipants().add(person1.getId());
        eventOne.getParticipants().add(person2.getId());

        model.addEvent(eventOne);
        model.addEvent(eventTwo);
    }

    @Test
    public void execute_unassignContactsFromEvent_success() throws Exception {
        Set<Index> contactIndices = new LinkedHashSet<>(List.of(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON));
        UnassignContactFromEventCommand unassignCommand =
                new UnassignContactFromEventCommand(Index.fromOneBased(1), contactIndices);

        CommandResult commandResult = unassignCommand.execute(model);

        assertEquals(String.format(UnassignContactFromEventCommand.MESSAGE_UNASSIGN_FROM_EVENT_SUCCESS,
                eventOne.getName().toString(), "Alice Pauline, Benson Meier"), commandResult.getFeedbackToUser());
    }

    @Test
    public void execute_unassignNotAssignedContact_throwsCommandException() {
        Set<Index> contactIndices = new LinkedHashSet<>(List.of(Index.fromOneBased(3)));
        UnassignContactFromEventCommand unassignCommand =
                new UnassignContactFromEventCommand(Index.fromOneBased(1), contactIndices);

        assertThrows(CommandException.class,
                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX
                        + " " + Messages.MESSAGE_TRY_PERSON_LIST_MODE, () -> unassignCommand.execute(model));
    }
}
