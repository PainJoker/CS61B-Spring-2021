package gitlet;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;


import static gitlet.Repository.COMMITS_DIR;
import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  does at a high level.
 *
 *  @author PainJoker
 */
public class Commit implements Serializable {
    public static final String EMPTY_PARENT = "";
    /**
     * Date format of commit.
     * source: Java SimpleDateFormat - Java Date Format on Digital Ocean
     */
    private static final String pattern = "E MMM dd hh:mm:ss yyyy Z";

    /** Data formatter. */
    private static final SimpleDateFormat formatter = new SimpleDateFormat(pattern);

    /** The message of this Commit. */
    private final String message;

    /**
     * The time stamp of a commit
     * initially with 00:00:00 UTC, Thursday, 1 January 1970 (the "Unix" epoch)
     */
    private final String date;

    /** the SHA-1 of one commit. */
    private final String uid;

    /** the SHA-1 of parent commit. */
    private final String parentUid;

    /** store the mapping between staged file and its corresponding SHA-1. */
    private final TreeMap<String, String> files;

    /** merged parent, may be null. */
    private final String secondParentUid;

   public Commit() {
       date = formatter.format(new Date(0));
       this.parentUid = EMPTY_PARENT;
       this.secondParentUid = EMPTY_PARENT;
       files = new TreeMap<>();
       this.message = "initial commit";
       uid = sha1(this.date, this.message, this.parentUid, serialize(this.files));
   }

   public Commit(String message, String parentUid, String secondParentUid,
                 TreeMap<String, String> staged, TreeSet<String> removed) {
       this.message = message;
       this.parentUid = parentUid;
       this.secondParentUid = secondParentUid;

       Commit parent = getCommit(parentUid);
       files = parent.getFiles();
       for (String file : removed) {
           files.remove(file);
       }
       for (String file : staged.keySet()) {
           if (files.containsKey(file)) {
               files.replace(file, staged.get(file));
           } else {
               files.put(file, staged.get(file));
           }
       }
       date = formatter.format(new Date());
       uid = sha1(this.date, this.message, this.parentUid, serialize(this.files));
   }

    /**
     * Retrieve Commit object from its uid.
     * @param commitUid String
     * @return Commit object
     */
    public static Commit getCommit(String commitUid) {
        for (String committedUid : Objects.requireNonNull(plainFilenamesIn(COMMITS_DIR))) {
            if (committedUid.contains(commitUid)) {
                File commitPath = join(COMMITS_DIR, committedUid);
                return readObject(commitPath, Commit.class);
            }
        }
        // commitUid is not exist.
        System.out.println("No commit with that id exists.");
        System.exit(0);
        return null;
    }

    public boolean isInitialCommit() {
        return parentUid.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("===\n");
        sb.append("commit " );
        sb.append(uid);
        sb.append("\n");
        if (isMerged()) {
            sb.append("Merge: ");
            sb.append(parentUid, 0, 7);
            sb.append(" ");
            sb.append(secondParentUid, 0, 7);
            sb.append("\n");
        }
        sb.append("Date: ");
        sb.append(date);
        sb.append("\n");
        sb.append(message);
        sb.append("\n");
        return sb.toString();
    }

   /** Move commit to the COMMIT_DIR. */
   public void store() {
       writeObject(new File(COMMITS_DIR, uid), this);
   }

   public String getMessage() {
       return message;
   }

   public String getDate() {
       return date;
   }

   public String getUid() {
       return uid;
   }

   public String getParentUid() {
       return parentUid;
   }

   public String getSecondParentUid() {
       return secondParentUid;
   }

   public TreeMap<String, String> getFiles() {
       return files;
   }

   private boolean isMerged() {
       return !EMPTY_PARENT.equals(secondParentUid);
   }
}
