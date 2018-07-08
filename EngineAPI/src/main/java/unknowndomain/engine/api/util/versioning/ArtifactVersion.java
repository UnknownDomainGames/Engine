package unknowndomain.engine.api.util.versioning;

/**
 * Describes an artifact version in terms of its components, converts it to/from a string and
 * compares two versions.
 *
 * @author <a href="mailto:brett@apache.org">Brett Porter</a>
 */
public interface ArtifactVersion extends Comparable<ArtifactVersion> {
    String getLabel();

    String getVersionString();

    boolean containsVersion(ArtifactVersion source);

    String getRangeString();
}
