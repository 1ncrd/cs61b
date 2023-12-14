package gitlet;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/** Represents a gitlet commit object.
 *
 *  does at a high level.
 *
 *  @author incrd
 */
public class Commit implements Serializable {
    /** The message of this Commit. */
    private String message;
    /** Commit time. */
    private String timestamp;

    private Map<String, String> fileToID = new HashMap<>();

    private List<String> parents = new ArrayList<>();

    private String id;

    public Commit(String message, Date date, HashMap<String, String> fileToID) {
        this.message = message;
        this.timestamp = dateToTimestamp(date);
        this.fileToID = fileToID;
    }

    public Commit(String message, Date date) {
        this.message = message;
        this.timestamp = dateToTimestamp(date);
    }

    public Commit(String message) {
        this.message = message;
        this.timestamp = dateToTimestamp(new Date());
    }

    private static String dateToTimestamp(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        return dateFormat.format(date);
    }

    @Override
    public String toString() {
        return "commit " + this.getId() + "\n"
                + "Date: " + this.getTimestamp() + "\n"
                + this.getMessage();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Commit commit = (Commit) o;
        return Objects.equals(id, commit.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public String generateID() {
        return Utils.sha1(this.message, this.timestamp, this.fileToID.toString(), this.parents.toString());
    }

    public void genAndSetID() {
        setId(generateID());
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, String> getFileToID() {
        return fileToID;
    }

    public void setFileToID(Map<String, String> fileToID) {
        this.fileToID = fileToID;
    }

    public List<String> getParents() {
        return parents;
    }

    public void setParents(List<String> parents) {
        this.parents = parents;
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
