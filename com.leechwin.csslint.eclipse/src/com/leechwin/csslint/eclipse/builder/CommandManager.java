package com.leechwin.csslint.eclipse.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;

public class CommandManager {

    private final String builderId = CssLintBuilder.BUILDER_ID;

    /**
     * Add the lint build commands to a project. If it already has them, no change will be made.
     */
    public void addTo(IProject project) throws CoreException {
        List<ICommand> cmds = getCommands(project);
        if (!cmds.contains(builderId)) {
            cmds.add(newCommand(project, builderId));
            setCommands(project, cmds);
        }
    }

    /** Return a (mutable) list of commands (builders) in a project. */
    private List<ICommand> getCommands(IProject project) throws CoreException {
        IProjectDescription desc = project.getDescription();
        return new ArrayList<ICommand>(Arrays.asList(desc.getBuildSpec()));
    }

    /** Make a new command (builder). */
    private ICommand newCommand(IProject project, String builderName) throws CoreException {
        ICommand cmd = project.getDescription().newCommand();
        cmd.setBuilderName(builderName);
        return cmd;
    }

    /** Remove the lint commands from a project. */
    public void removeFrom(IProject project) throws CoreException {
        List<ICommand> cmds = getCommands(project);
        if (cmds.contains(builderId)) {
            cmds.remove(builderId);
            setCommands(project, cmds);
        }
    }

    /** Set the list of commands (builders) for a project. */
    private void setCommands(IProject project, List<ICommand> commands) throws CoreException {
        IProjectDescription desc = project.getDescription();
        desc.setBuildSpec(commands.toArray(new ICommand[commands.size()]));
        project.setDescription(desc, null);
    }

}
