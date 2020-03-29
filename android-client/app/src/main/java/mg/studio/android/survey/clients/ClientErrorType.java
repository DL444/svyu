package mg.studio.android.survey.clients;

/**
 * Represents a type of error occurred in client.
 */
public enum ClientErrorType {
    /**
     * Represents IO error, including disk write fail or network error.
     */
    IO,
    /**
     * Represents the requested resource was not found.
     */
    NotFound,
    /**
     * Represents that the specific type of client does not support the requested operation.
     */
    NotSupported,
    /**
     * Represents that the client was unable to correctly serialize or deserialize survey model.
     */
    Serialization,
    /**
     * Represents that current version of the client does not recognize all features used in the model.
     */
    Versioning,
    /**
     * Represents that there is currently no cached models to enable working offline.
     */
    CacheMiss,
    /**
     * Represents an unknown error.
     */
    Unknown
}
