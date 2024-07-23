package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

import static gitlet.Repository.REMOVED_FILE;
import static gitlet.Repository.STAGED_FILE;
import static gitlet.Repository.STAGED_DIR;
import static gitlet.Repository.BLOBS_DIR;
import static gitlet.Utils.*;
import static java.nio.file.Files.move;

/**
 * Handle the stage and remove area logic.
 *
 * @author PainJoker
 */
public class BufferManager {
    public static HashMap<String, String> staged;
    public static HashMap<String, String> removed;

    public static void setBuffer() {
        staged = new HashMap<>();
        removed = new HashMap<>();
        writeObject(STAGED_FILE, staged);
        writeObject(REMOVED_FILE, removed);
    }

    /** Clear the Staging files of stage area. */
    public static void clearStage() {
        staged = new HashMap<>();
        writeObject(STAGED_FILE, staged);
    }

    /** Unstage the blob. */
    public static void unstage(Blob blob) {
        File stagedBlob = new File(STAGED_DIR, blob.getFileName());
        if (!stagedBlob.delete()) {
            throw new RuntimeException("Failed to delete staged file: " + stagedBlob.getAbsolutePath());
        }
        removeStage(blob);
    }

    /** Check stage area whether is empty. */
    public static boolean stagedAreaIsEmpty() {
        staged = readObject(STAGED_FILE, HashMap.class);
        return staged.isEmpty();
    }

    /**
     * Get tracking information of stage area.
     * @return stage mapping between file name and corresponding SHA-1
     */
    public static HashMap<String, String> getStaged() {
        staged = readObject(STAGED_FILE, HashMap.class);
        return staged;
    }

    /**
     * Check whether staged File through its file name.
     * @return true if it is staged.
     */
    public static boolean stagedContainsFile(Blob blob) {
        staged = readObject(STAGED_FILE, HashMap.class);
        return staged.containsKey(blob.getFileName());
    }

    /**
     * Staging an already-staged file overwrites the previous entry
     * in the staging area with the new contents.
     * @param blob The content of file need to be added.
     */
    public static void stageWithOverride(Blob blob) {
        staged = readObject(STAGED_FILE, HashMap.class);
        String fileName = blob.getFileName();
        String fileUid = blob.getUid();
        if (stagedContainsFile(blob)) {
            unstage(blob);
        }
        stage(blob);
        addStage(blob);
    }

    /** Write blob into STAGE_DIR. */
    private static void stage(Blob blob) {
        File stagedFile = join(STAGED_DIR, blob.getFileName());
        try {
            stagedFile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        writeObject(stagedFile, blob);
    }

    /** Lose track of blob */
    private static void removeStage(Blob blob) {
        staged = readObject(STAGED_FILE, HashMap.class);
        staged.remove(blob.getFileName());
        writeObject(STAGED_FILE, staged);
    }

    /** add track of blob. */
    private static void addStage(Blob blob) {
        staged = readObject(STAGED_FILE, HashMap.class);
        staged.put(blob.getFileName(), blob.getUid());
        writeObject(STAGED_FILE, staged);
    }

    public static void storeTrackedFiles() {
        for (String file : Objects.requireNonNull(plainFilenamesIn(STAGED_DIR))) {
            File blobFile = join(STAGED_DIR, file);
            Blob blob = readObject(blobFile, Blob.class);
            try {
                move(blobFile.toPath(), new File(BLOBS_DIR, blob.getUid()).toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        clearStage();
    }
}
