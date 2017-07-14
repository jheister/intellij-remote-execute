package jheister.idearemoteexecute;

import com.intellij.debugger.impl.GenericDebuggerRunner;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.DebuggingRunnerData;
import com.intellij.execution.configurations.RemoteConnection;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.RunContentDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RemoteDebugProgramRunner extends GenericDebuggerRunner {
    @NotNull
    @Override
    public String getRunnerId() {
        return DebuggingRunnerData.DEBUGGER_RUNNER_ID;
    }

    @Override
    public boolean canRun(@NotNull String id, @NotNull RunProfile runProfile) {
        return DefaultDebugExecutor.EXECUTOR_ID.equals(id) && runProfile instanceof RemoteExecutionConfig;
    }

    @Nullable
    @Override
    protected RunContentDescriptor doExecute(@NotNull RunProfileState state, @NotNull ExecutionEnvironment environment) throws ExecutionException {
        return super.doExecute(state, environment);
    }

    @Nullable
    @Override
    protected RunContentDescriptor createContentDescriptor(@NotNull RunProfileState state, @NotNull ExecutionEnvironment environment) throws ExecutionException {
        RemoteExecutionConfig.RemoteExecutionRunProfileState remoteState = (RemoteExecutionConfig.RemoteExecutionRunProfileState) state;

        remoteState.setAdditionalJvmArgs("-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005");

        RemoteConnection con = new RemoteConnection(true, "localhost", "5005", false);

        return this.attachVirtualMachine(state, environment, con, true);

        //todo: setup SSH tunnel for debug
        //todo: shutdown debugger when app stops
    }
}
