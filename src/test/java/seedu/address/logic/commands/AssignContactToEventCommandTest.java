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
 * Tests for {@link AssignContactToEventCommand}.
 */
public class AssignContactToEventCommandTest {

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

        model.addEvent(eventOne);
        model.addEvent(eventTwo);

        // add two persons to the model
        Person person1 = TypicalPersons.ALICE;
        Person person2 = TypicalPersons.BENSON;
        model.addPerson(person1);
        model.addPerson(person2);
    }

    @Test
    public void execute_assignContactsToEvent_success() throws Exception {
        Set<Index> contactIndices = new LinkedHashSet<>(List.of(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON));
        AssignContactToEventCommand assignCommand =
                new AssignContactToEventCommand(Index.fromOneBased(1), contactIndices);

        CommandResult commandResult = assignCommand.execute(model);

        assertEquals(String.format(AssignContactToEventCommand.MESSAGE_ASSIGN_TO_EVENT_SUCCESS,
                eventOne.getName().toString(), "Alice Pauline, Benson Meier"), commandResult.getFeedbackToUser());
    }

    @Test
    public void execute_assignToNonexistentEvent_throwsCommandException() {
        Set<Index> contactIndices = new LinkedHashSet<>(List.of(INDEX_FIRST_PERSON));
        AssignContactToEventCommand assignCommand =
                new AssignContactToEventCommand(Index.fromOneBased(3), contactIndices);

        assertThrows(CommandException.class, Messages.MESSAGE_INVALID_EVENT_DISPLAYED_INDEX, () ->
                assignCommand.execute(model));
    }

    @Test
    public void execute_assignAlreadyAssignedContact_throwsCommandException() throws Exception {
        Set<Index> contactIndices = new LinkedHashSet<>(List.of(INDEX_FIRST_PERSON));
        AssignContactToEventCommand assignCommand =
                new AssignContactToEventCommand(Index.fromOneBased(1), contactIndices);
        // first assign should succeed
        assignCommand.execute(model);

        // second assign of same person should fail
        assertThrows(CommandException.class,
                "Alice Pauline has already been assigned to this party.", () -> assignCommand.execute(model));
    }
}
