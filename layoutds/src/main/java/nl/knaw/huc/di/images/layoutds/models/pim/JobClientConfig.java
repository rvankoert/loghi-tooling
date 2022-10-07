package nl.knaw.huc.di.images.layoutds.models.pim;

import java.util.List;


public class JobClientConfig {
    List<JobType> jobtypes;
    int numCPUCores;
    int numGPUs;

    public JobClientConfig() {

    }

    public JobClientConfig(List<JobType> jobTypes, int numCPUCores, int numGPUs) {
        this.jobtypes = jobTypes;
        this.numCPUCores = numCPUCores;
        this.numGPUs = numGPUs;
    }

    public List<JobType> getJobtypes() {
        return jobtypes;
    }

    public int getNumCPUCores() {
        return numCPUCores;
    }

    public int getNumGPUs() {
        return numGPUs;
    }
}
