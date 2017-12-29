package com.customLint.registry;

import com.android.tools.lint.client.api.IssueRegistry;
import com.android.tools.lint.detector.api.Issue;
import com.customLint.detectors.EnumComparisonDetector;

import java.util.Arrays;
import java.util.List;

/**
 * Created by piyush.khapekar on 3/21/2017.
 */

public class CustomIssueRegistry extends IssueRegistry {

    private List<Issue> mIssues = Arrays.asList(
            EnumComparisonDetector.ISSUE
    );

    public CustomIssueRegistry() {
    }


    /**
     * Returns the list of issues that can be found by all known detectors.
     *
     * @return the list of issues to be checked (including those that may be
     *         disabled!)
     */
    @Override
    public List<Issue> getIssues() {
        return mIssues;
    }
}
