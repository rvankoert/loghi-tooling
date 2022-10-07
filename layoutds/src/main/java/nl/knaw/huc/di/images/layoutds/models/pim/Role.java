package nl.knaw.huc.di.images.layoutds.models.pim;

// Order roles on hierarchy admin is the highest
public enum Role {
    ADMIN, // administrators of instance of the application
    OCR_MINION, // role for all the minions that perform ocr actions
    SIAMESENETWORK_MINION, // role form minions that use siamese networks
    JOBRUNNER_MINION, // Minion for executing and delegating long running jobs
    PI, // primary investigator, administrator of a data set
    RESEARCHER, // other researchers not the primary investigator
    ASSISTANT,
    AUTHENTICATED // logged in users without a specific role
}
