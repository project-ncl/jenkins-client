package com.offbytwo.jenkins.integration;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.*;
import hudson.model.Cause;
import hudson.model.FreeStyleProject;
import hudson.model.ParametersAction;
import hudson.model.StringParameterValue;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BuildWithDetailsTest  {

    private final String JENKINS_WITH_DETAILS_TEST_JOB = "build_with_details";
    private JenkinsServer server;
    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();


    @Before
    public void setUp() throws Exception {
        jenkinsRule.getInstance().getCrumbIssuer().getCrumb();
        jenkinsRule.getInstance().getCrumbIssuer().getCrumbRequestField();
        server = new JenkinsServer(jenkinsRule.getURL().toURI());
    }

    @Test
    public void checkCauses() throws Exception {
        FreeStyleProject pr = jenkinsRule.getInstance().createProject(FreeStyleProject.class, JENKINS_WITH_DETAILS_TEST_JOB);
        for (int i = 0; i < 5; i++)
            pr.scheduleBuild(0, new Cause.UserIdCause(),
                    new ParametersAction(new StringParameterValue("BUILD NUMBER", "" + i)));

        while (pr.isInQueue() || pr.isBuilding()) {
        }

        JobWithDetails job = server.getJobs().get(JENKINS_WITH_DETAILS_TEST_JOB).details();
        BuildWithDetails build = job.getBuilds().get(0).details();
        assertEquals(BuildResult.SUCCESS, build.getResult());

        List<BuildCause> causes = build.getCauses();
        assertNotNull(causes);

    }

}
