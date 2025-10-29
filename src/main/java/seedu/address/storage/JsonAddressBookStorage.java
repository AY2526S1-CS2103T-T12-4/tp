package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataCorruptionException;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.FileUtil;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.ReadOnlyAddressBook;

/**
 * A class to access AddressBook data stored as a json file on the hard disk.
 */
public class JsonAddressBookStorage implements AddressBookStorage {

    private static final Logger logger = LogsCenter.getLogger(JsonAddressBookStorage.class);

    private final Path filePath;

    public JsonAddressBookStorage(Path filePath) {
        this.filePath = filePath;
    }

    public Path getAddressBookFilePath() {
        return filePath;
    }

    @Override
    public Optional<ReadOnlyAddressBook> readAddressBook() throws DataLoadingException {
        return readAddressBook(filePath);
    }

    /**
     * Similar to {@link #readAddressBook()}.
     *
     * @param filePath location of the data. Cannot be null.
     * @throws DataLoadingException if loading the data from storage failed.
     */
    public Optional<ReadOnlyAddressBook> readAddressBook(Path filePath) throws DataLoadingException {
        requireNonNull(filePath);

        logger.info("Reading AddressBook data from " + filePath);

        // Check data integrity before loading
        try {
            if (!DataIntegrityChecker.verifyIntegrity(filePath)) {
                throw new DataLoadingException(new DataCorruptionException(
                    "Data file has been modified externally. "
                    + "Please restore from backup or delete the file to reset the application. "
                    + "Manual editing of data files is not supported and may cause data corruption."));
            }
        } catch (IOException e) {
            logger.warning("Unable to verify data integrity: " + e.getMessage());
        }

        Optional<JsonSerializableAddressBook> jsonAddressBook = JsonUtil.readJsonFile(
                filePath, JsonSerializableAddressBook.class);
        if (jsonAddressBook.isEmpty()) {
            logger.info("AddressBook file not found at " + filePath + "; returning empty Optional.");
            return Optional.empty();
        }

        try {
            ReadOnlyAddressBook readOnly = jsonAddressBook.get().toModelType();
            logger.info("Successfully loaded AddressBook from " + filePath);
            return Optional.of(readOnly);
        } catch (IllegalValueException ive) {
            logger.info("Illegal values found in " + filePath + ": " + ive.getMessage());
            throw new DataLoadingException(ive);
        }
    }

    @Override
    public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
        saveAddressBook(addressBook, filePath);
    }

    /**
     * Similar to {@link #saveAddressBook(ReadOnlyAddressBook)}.
     *
     * @param filePath location of the data. Cannot be null.
     */
    public void saveAddressBook(ReadOnlyAddressBook addressBook, Path filePath) throws IOException {
        requireNonNull(addressBook);
        requireNonNull(filePath);

        logger.info("Saving AddressBook to " + filePath);
        FileUtil.createIfMissing(filePath);
        JsonUtil.saveJsonFile(new JsonSerializableAddressBook(addressBook), filePath);
        logger.info("Saved AddressBook to " + filePath);

        // Generate new checksum after saving
        DataIntegrityChecker.generateChecksum(filePath);
    }

}
