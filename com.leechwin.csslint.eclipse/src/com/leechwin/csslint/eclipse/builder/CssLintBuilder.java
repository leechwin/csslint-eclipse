package com.leechwin.csslint.eclipse.builder;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import com.leechwin.csslint.eclipse.Activator;
import com.leechwin.csslint.eclipse.CssLintLog;
import com.leechwin.csslint.eclipse.engine.CssLint;
import com.leechwin.csslint.eclipse.engine.CssLintResult;
import com.leechwin.csslint.eclipse.engine.Issue;

public class CssLintBuilder extends IncrementalProjectBuilder {

    private class CssLintDeltaVisitor implements IResourceDeltaVisitor {
        private final IProgressMonitor monitor;

        public CssLintDeltaVisitor(IProgressMonitor monitor) {
            this.monitor = monitor;
        }

        public boolean visit(IResourceDelta delta) throws CoreException {
            IResource resource = delta.getResource();
            switch (delta.getKind()) {
            case IResourceDelta.ADDED:
                // handle added resource
                logProgress(monitor, resource);
                checkCss(resource);
                break;
            case IResourceDelta.REMOVED:
                // handle removed resource
                break;
            case IResourceDelta.CHANGED:
                // handle changed resource
                logProgress(monitor, resource);
                checkCss(resource);
                break;
            }
            // return true to continue visiting children.
            return true;
        }
    }

    private class CssLintResourceVisitor implements IResourceVisitor {
        private final IProgressMonitor monitor;

        public CssLintResourceVisitor(IProgressMonitor monitor) {
            this.monitor = monitor;
        }

        public boolean visit(IResource resource) {
            logProgress(monitor, resource);
            checkCss(resource);
            // return true to continue visiting children.
            return true;
        }
    }

    // NB! Must match plugin.xml declaration.
    public static final String BUILDER_ID = Activator.PLUGIN_ID + ".builder.CssLintBuilder";

    // NB! Must match plugin.xml declaration.
    public static final String MARKER_TYPE = Activator.PLUGIN_ID + ".CSSLintProblem";

    private final CssLintProvider lintProvider = new CssLintProvider();
    private final Excluder excluder = new Excluder();

    public CssLintBuilder() {
        lintProvider.init();
        excluder.init();
    }

    private void addMarker(IFile file, Issue issue) {
        try {
            IMarker m = file.createMarker(MARKER_TYPE);
            if (m.exists()) {
                m.setAttribute(IMarker.MESSAGE, issue.getMessage());
                m.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
                m.setAttribute(IMarker.LINE_NUMBER, issue.getLine());
                m.setAttribute(IMarker.SOURCE_ID, "CSSLint");
            }
        } catch (CoreException e) {
            CssLintLog.error(e);
        }
    }

    @Override
    protected IProject[] build(final int kind, @SuppressWarnings("rawtypes") Map args, IProgressMonitor monitor) throws CoreException {
        ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable() {
            public void run(IProgressMonitor monitor) throws CoreException {
                if (kind == FULL_BUILD) {
                    fullBuild(monitor);
                } else {
                    IResourceDelta delta = getDelta(getProject());
                    if (delta == null) {
                        fullBuild(monitor);
                    } else {
                        incrementalBuild(delta, monitor);
                    }
                }
            }
        }, monitor);
        return null;
    }

    private void checkCss(IResource resource) {
        if (!(resource instanceof IFile)) {
            return;
        }

        IFile file = (IFile) resource;
        if (!isCss(file)) {
            return;
        }

        // Clear out any existing problems.
        deleteMarkers(file);

        if (excluded(file)) {
            return;
        }

        BufferedReader reader = null;
        try {
            CssLint lint = lintProvider.getCssLint();
            reader = new BufferedReader(new InputStreamReader(file.getContents(), file.getCharset()));
            CssLintResult result = lint.lint(file.getFullPath().toString(), reader);
            for (Issue issue : result.getIssues()) {
                addMarker(file, issue);
            }
        } catch (IOException e) {
            CssLintLog.error(e);
        } catch (CoreException e) {
            CssLintLog.error(e);
        } finally {
            close(reader);
        }
    }

    /**
     * Is {@code file} explicitly excluded? Check against a list of regexes in the <i>exclude_path_regexes</i> preference.
     */
    private boolean excluded(IFile file) {
        return excluder.isExcluded(file);
    }

    private boolean isCss(IFile file) {
        return file.getName().endsWith(".css");
    }

    private void close(Closeable close) {
        if (close == null) {
            return;
        }
        try {
            close.close();
        } catch (IOException e) {
        }
    }

    private void deleteMarkers(IFile file) {
        try {
            file.deleteMarkers(MARKER_TYPE, false, IResource.DEPTH_ZERO);
        } catch (CoreException e) {
            CssLintLog.error(e);
        }
    }

    private void fullBuild(final IProgressMonitor monitor) throws CoreException {
        try {
            startProgress(monitor);
            getProject().accept(new CssLintResourceVisitor(monitor));
        } catch (CoreException e) {
            CssLintLog.error(e);
        } finally {
            monitor.done();
        }
    }

    private void incrementalBuild(IResourceDelta delta, IProgressMonitor monitor)
            throws CoreException {
        try {
            startProgress(monitor);
            delta.accept(new CssLintDeltaVisitor(monitor));
        } finally {
            monitor.done();
        }
    }

    private void startProgress(IProgressMonitor monitor) {
        monitor.beginTask("CSSLint", IProgressMonitor.UNKNOWN);
    }

    private void logProgress(IProgressMonitor monitor, IResource resource) {
        monitor.subTask("Linting " + resource.getName());
    }

}
