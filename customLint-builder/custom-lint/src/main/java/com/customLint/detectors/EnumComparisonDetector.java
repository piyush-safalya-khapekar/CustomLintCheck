package com.customLint.detectors;

import com.android.annotations.NonNull;
import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Context;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.Location;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;
import com.android.tools.lint.detector.api.TextFormat;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.regex.Pattern;

import lombok.ast.AstVisitor;
import lombok.ast.EnumConstant;
import lombok.ast.EnumDeclaration;
import lombok.ast.ForwardingAstVisitor;
import lombok.ast.Node;

/**
 * Created by piyush.khapekar on 3/24/2017.
 * <p>
 * This class is used to detect comparisons of Enum. If enum's with different enum type are compared,
 * then this detector class will throw an warning.
 */

public class EnumComparisonDetector extends Detector implements Detector.JavaScanner {

    private static final String EQUALS_MATCHER_STRING = ".equals(";
    private static final Class<? extends Detector> DETECTOR_CLASS = EnumComparisonDetector.class;
    private static final EnumSet<Scope> DETECTOR_SCOPE = Scope.JAVA_FILE_SCOPE;
    private static final int END_INDEX = 60;
    private static final Implementation IMPLEMENTATION = new Implementation(
            DETECTOR_CLASS,
            DETECTOR_SCOPE
    );

    private static final String ISSUE_ID = "Enum comparison detected";
    private static final String ISSUE_DESCRIPTION = "Enum should only be compared with the same enum type";
    private static final String ISSUE_EXPLANATION = "Two enum constants should only be compared if " +
            "they belong to the same enum type";
    private static final Category ISSUE_CATEGORY = Category.CORRECTNESS;
    private static final int ISSUE_PRIORITY = 5;
    private static final Severity ISSUE_SEVERITY = Severity.WARNING;
    private static ArrayList<String> enumArrayList = new ArrayList<>();
    public static final Issue ISSUE = Issue.create(
            ISSUE_ID,
            ISSUE_DESCRIPTION,
            ISSUE_EXPLANATION,
            ISSUE_CATEGORY,
            ISSUE_PRIORITY,
            ISSUE_SEVERITY,
            IMPLEMENTATION
    );

    /**
     * Constructs a new {@link EnumComparisonDetector} check
     */
    public EnumComparisonDetector() {
    }

    @Override
    public boolean appliesTo(@NonNull Context context, @NonNull File file) {
        return true;
    }

    @Override
    public EnumSet<Scope> getApplicableFiles() {
        return Scope.JAVA_FILE_SCOPE;
    }

    @Override
    public List<Class<? extends Node>> getApplicableNodeTypes() {
        return Arrays.<Class<? extends Node>>asList(
                EnumDeclaration.class,
                EnumConstant.class
        );
    }

    @Override
    public AstVisitor createJavaVisitor(@NonNull JavaContext context) {
        String source = context.getContents();
        // Check validity of source
        if (source == null) {
            return null;
        }

        if (enumArrayList != null) {
            for (int i = 0; i < enumArrayList.size(); i++) {
                // Check for uses of EQUALS_MATCHER_STRING
                int index = source.indexOf(enumArrayList.get(i).toString() + EQUALS_MATCHER_STRING);
                if (index > 0) {
                    String subString = source.substring(index, index + END_INDEX);
                    String[] subStringArr = subString.split(Pattern.quote(EQUALS_MATCHER_STRING));
                    String[] tempSubStringArr = subStringArr[1].split(Pattern.quote("."));
                    String firstEnumClassName = tempSubStringArr[0].trim();

                    String[] arraylistEnumNameArr = enumArrayList.get(i).toString().split(Pattern.quote("."));
                    String secondEnumClassName = arraylistEnumNameArr[0].trim();
                    // If two enum classes are not same, throws warning
                    if (!firstEnumClassName.equalsIgnoreCase(secondEnumClassName)) {
                        for (int j = index; j >= 0; j = source.indexOf(subString, j + 1)) {
                            Location location = Location.create(context.file, source, j, j + subString.length());
                            context.report(ISSUE, location, ISSUE.getBriefDescription(TextFormat.TEXT));
                        }
                    }
                }
            }
        }
        return new EnumComparisonDetector.EnumChecker(context);
    }


    /**
     * This class is used to traverse through all the enum class created in project and store all the
     * enum constant in array list.
     */
    private static class EnumChecker extends ForwardingAstVisitor {
        private final JavaContext mContext;
        private String enumClassName;
        String enumClassRegexString = "public enum";

        public EnumChecker(JavaContext context) {
            mContext = context;
        }

        @Override
        public boolean visitEnumDeclaration(EnumDeclaration node) {
            String[] enumClassNameArr = node.getParent().toString().split(enumClassRegexString);
            String[] tempEnumClassArr = enumClassNameArr[1].split(Pattern.quote("{"));
            enumClassName = tempEnumClassArr[0].trim();
            return super.visitEnumDeclaration(node);
        }

        public boolean visitEnumConstant(EnumConstant node) {
            enumArrayList.add(enumClassName + "." + node.getChildren().get(0).toString());
            new EnumComparisonDetector().createJavaVisitor(mContext);
            return super.visitEnumConstant(node);
        }
    }
}
