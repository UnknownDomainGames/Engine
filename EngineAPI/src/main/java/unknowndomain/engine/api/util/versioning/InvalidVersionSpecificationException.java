package unknowndomain.engine.api.util.versioning;

/**
 * Occurs when a version is invalid.
 *
 * @author <a href="mailto:brett@apache.org">Brett Porter</a>
 */
public class InvalidVersionSpecificationException extends Exception
{
    private static final long serialVersionUID = 1L;

    public InvalidVersionSpecificationException( String message )
    {
        super( message );
    }
}
