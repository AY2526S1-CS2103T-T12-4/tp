package seedu.address.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;

/**
 * A utility class to check data integrity of JSON files by maintaining checksums.
 */
public class DataIntegrityChecker {

    private static final Logger logger = LogsCenter.getLogger(DataIntegrityChecker.class);
    private static final String CHECKSUM_FILE_SUFFIX = ".checksum";

    /**
     * Generates and saves a checksum for the given file.
     *
     * @param filePath The path to the file to generate checksum for
     * @throws IOException if there's an error reading the file or writing the checksum
     */
    public static void generateChecksum(Path filePath) throws IOException {
        if (!Files.exists(filePath)) {
            return; // No file to checksum
        }

        try {
            byte[] fileContent = Files.readAllBytes(filePath);
            String checksum = calculateChecksum(fileContent);
            Path checksumPath = getChecksumPath(filePath);
            // Add newline to comply with repository EOF requirements
            Files.writeString(checksumPath, checksum + "\n");
            logger.info("Generated checksum for " + filePath);
        } catch (NoSuchAlgorithmException e) {
            logger.warning("Unable to generate checksum: " + e.getMessage());
        }
    }

    /**
     * Verifies if the file has been modified by comparing checksums.
     *
     * @param filePath The path to the file to verify
     * @return true if the file is intact, false if it has been modified
     * @throws IOException if there's an error reading the file or checksum
     */
    public static boolean verifyIntegrity(Path filePath) throws IOException {
        if (!Files.exists(filePath)) {
            return true; // No file means no corruption
        }

        Path checksumPath = getChecksumPath(filePath);
        if (!Files.exists(checksumPath)) {
            logger.info("No checksum file found for " + filePath + ", assuming first run");
            return true; // No checksum means first run, allow it
        }

        try {
            byte[] fileContent = Files.readAllBytes(filePath);
            String currentChecksum = calculateChecksum(fileContent);
            String savedChecksum = Files.readString(checksumPath).trim();

            boolean isIntact = currentChecksum.equals(savedChecksum);
            if (!isIntact) {
                logger.warning("Data integrity check failed for " + filePath);
                logger.warning("Expected checksum: " + savedChecksum);
                logger.warning("Actual checksum: " + currentChecksum);
            }

            return isIntact;
        } catch (NoSuchAlgorithmException e) {
            logger.warning("Unable to verify checksum: " + e.getMessage());
            return true; // If we can't verify, assume it's okay
        }
    }

    /**
     * Calculates SHA-256 checksum of the given byte array.
     *
     * @param content The content to calculate checksum for
     * @return The hexadecimal checksum string
     * @throws NoSuchAlgorithmException if SHA-256 is not available
     */
    private static String calculateChecksum(byte[] content) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(content);
        return HexFormat.of().formatHex(hash);
    }

    /**
     * Gets the path for the checksum file corresponding to the given file.
     *
     * @param filePath The original file path
     * @return The checksum file path
     */
    private static Path getChecksumPath(Path filePath) {
        return filePath.resolveSibling(filePath.getFileName() + CHECKSUM_FILE_SUFFIX);
    }
}
