package judgels.gabriel.sandboxes.postgrelate;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import judgels.gabriel.api.ProcessExecutionResult;
import judgels.gabriel.api.Sandbox;
import judgels.gabriel.api.SandboxException;
import judgels.gabriel.api.SandboxExecutionResult;
import judgels.gabriel.api.SandboxExecutionStatus;
import judgels.gabriel.sandboxes.SandboxExecutor;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostgrelateSandbox implements Sandbox {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostgrelateSandbox.class);

    private final String postgrelatePath;
    private final int boxId;
    private final Set<File> allowedDirs;
    private final Set<String> filenames;

    private File boxDir;

    private File standardInput;
    private File standardOutput;
    private File standardError;

    private int timeLimit;
    private int memoryLimit;
    private int fileSizeLimit;

    public PostgrelateSandbox(String postgrelatePath, int boxId) {
        this.postgrelatePath = postgrelatePath;
        this.boxId = boxId;
        this.allowedDirs = Sets.newHashSet();
        this.filenames = Sets.newHashSet();
        this.fileSizeLimit = 100 * 1024;

        LOGGER.info("Initialization of Postgrelate box {} started.", boxId);
        initPostgrelate();
        LOGGER.info("Initialization of Postgrelate box {} finished.", boxId);
    }

    @Override
    public void addFile(File file) {
        try {
            FileUtils.copyFileToDirectory(file, boxDir);
            filenames.add(file.getName());
            LOGGER.info("File {} added to Postgrelate box {}.", file.getName(), boxId);
        } catch (IOException e) {
            throw new SandboxException(e);
        }
    }

    @Override
    public boolean containsFile(String filename) {
        return filenames.contains(filename);
    }

    @Override
    public File getFile(String filename) {
        return new File(boxDir, filename);
    }

    @Override
    public void addAllowedDirectory(File directory) {
        allowedDirs.add(directory);
    }

    @Override
    public void setTimeLimitInMilliseconds(int timeLimitInMilliseconds) {
        this.timeLimit = timeLimitInMilliseconds;
    }

    @Override
    public void setWallTimeLimitInMilliseconds(int timeLimitInMilliseconds) { }

    @Override
    public void setMemoryLimitInKilobytes(int memoryLimitInKilobytes) {
        this.memoryLimit = memoryLimitInKilobytes;
    }

    @Override
    public void resetRedirections() {
        standardInput = null;
        standardOutput = null;
        standardError = null;
    }

    @Override
    public void redirectStandardInput(String filenameInsideThisSandbox) {
        standardInput = new File(boxDir, filenameInsideThisSandbox);
    }

    @Override
    public void redirectStandardOutput(String filenameInsideThisSandbox) {
        standardOutput = new File(boxDir, filenameInsideThisSandbox);
    }

    @Override
    public void redirectStandardError(String filenameInsideThisSandbox) {
        standardError = new File(boxDir, filenameInsideThisSandbox);
    }

    @Override
    public void removeAllFilesExcept(Set<String> filenamesToRetain) {
        for (String filename : filenames) {
            if (!filenamesToRetain.contains(filename)) {
                try {
                    FileUtils.forceDelete(new File(boxDir, filename));
                } catch (IOException e) {
                    throw new SandboxException(e);
                }
            }
        }

        filenames.removeIf(f -> !filenamesToRetain.contains(f));
    }

    @Override
    public void cleanUp() {
        LOGGER.info("Cleanup of Postgrelate box {} started.", boxId);
        cleanUpPostgrelate();
        LOGGER.info("Cleanup of Postgrelate box {} finished.", boxId);
    }

    @Override
    public SandboxExecutionResult execute(List<String> command) {
        ProcessBuilder pb = getProcessBuilder(command).redirectErrorStream(true);

        try {
            ProcessExecutionResult result = SandboxExecutor.executeProcessBuilder(pb);
            LOGGER.info("exit code ini: " + result.getExitCode());
            return getResult(result.getExitCode());

        } catch (IOException | InterruptedException e) {
            return new SandboxExecutionResult.Builder()
                    .status(SandboxExecutionStatus.INTERNAL_ERROR)
                    .time(-1)
                    .memory(-1)
                    .message(e.getMessage())
                    .build();
        }
    }

    @Override
    public ProcessBuilder getProcessBuilder(List<String> command) {
        ImmutableList.Builder<String> sandboxedCommand = ImmutableList.builder();
        sandboxedCommand.add("/usr/bin/java").add("-jar");
        sandboxedCommand.add(postgrelatePath).add("-b" + boxId);

        // sandboxedCommand.add("--dir=/etc");
        // for (File dir : allowedDirs) {
        //     sandboxedCommand.add("--dir=" + dir.getAbsolutePath() + ":rw");
        // }

        // sandboxedCommand.add("-e");

        if (timeLimit > 0) {
            sandboxedCommand.add("-t" + timeLimit);
            // sandboxedCommand.add("-x0.5");
        }

        if (memoryLimit > 0) {
            sandboxedCommand.add("-m" + memoryLimit);
        }

        // if (fileSizeLimit > 0) {
        //     sandboxedCommand.add("-f" + fileSizeLimit);
        // }

        if (standardInput != null) {
            sandboxedCommand.add("-i" + boxDir + "/" + standardInput.getName());
        }

        if (standardOutput != null) {
            sandboxedCommand.add("-o" + boxDir + "/" + standardOutput.getName());
        }

        // if (standardError != null) {
        //     sandboxedCommand.add("-r" + standardError.getName());
        // }

        sandboxedCommand.add("-M" + new File(boxDir, "_postgrelate.meta").getAbsolutePath());
        sandboxedCommand.add("--run");

        sandboxedCommand.add("-q").add(boxDir + "/" + command.get(0));

        return new ProcessBuilder(sandboxedCommand.build());
    }

    @Override
    public SandboxExecutionResult getResult(int exitCode) {
        if (exitCode != 0 && exitCode != 1) {
            return SandboxExecutionResult.internalError("Postgrelate returned nonzero and non-one exit code: " + exitCode);
        }

        String meta;
        try {
            meta = FileUtils.readFileToString(new File(boxDir, "_postgrelate.meta"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return SandboxExecutionResult.internalError("Postgrelate did not produce readable meta file!");
        }

        Map<String, String> items = Maps.newHashMap();
        for (String line : meta.split("\n")) {
            String[] tokens = line.split(":");
            String key = tokens[0];
            String val = tokens[1];

            items.put(key, val);
        }

        int time = (int) (Double.parseDouble(items.get("time")));
        int wallTime = (int) (Double.parseDouble(items.get("time-wall")));
        int memory = (int) (Double.parseDouble(items.get("cg-mem")));
        String status = items.get("status");

        SandboxExecutionStatus executionStatus;
        if (status == null) {
            executionStatus = SandboxExecutionStatus.ZERO_EXIT_CODE;
        } else if (status.equals("RE")) {
            executionStatus = SandboxExecutionStatus.NONZERO_EXIT_CODE;
        } else if (status.equals("SG")) {
            executionStatus = SandboxExecutionStatus.KILLED_ON_SIGNAL;
        } else if (status.equals("TO")) {
            executionStatus = SandboxExecutionStatus.TIMED_OUT;
        } else {
            executionStatus = SandboxExecutionStatus.INTERNAL_ERROR;
        }

        Optional<Integer> exitSignal = Optional.empty();
        if (items.containsKey("exitsig")) {
            exitSignal = Optional.of(Integer.parseInt(items.get("exitsig")));
        }

        boolean isKilled = items.getOrDefault("killed", "0").equals("1");
        Optional<String> message = Optional.ofNullable(items.get("message"));

        return new SandboxExecutionResult.Builder()
                .time(time)
                .wallTime(wallTime)
                .memory(memory)
                .status(executionStatus)
                .exitSignal(exitSignal)
                .isKilled(isKilled)
                .message(message)
                .build();
    }

    private void initPostgrelate() {
        for (int tries = 0;; tries++) {
            ImmutableList.Builder<String> command = ImmutableList.builder();
            command.add("/usr/bin/java");
            command.add("-jar");
            command.add(postgrelatePath, "-b" + boxId);
            command.add("--init");

            ProcessBuilder pb = new ProcessBuilder(command.build()).redirectErrorStream(true);

            try {
                ProcessExecutionResult result = SandboxExecutor.executeProcessBuilder(pb);
                if (result.getExitCode() == 0) {
                    boxDir = new File(result.getOutputLines().get(0));
                    return;
                }
                if (tries < 1) {
                    String errorMessage = result.getOutputLines().isEmpty() ? "" : result.getOutputLines().get(0);
                    if (errorMessage.startsWith("Box already exists")) {
                        // Clean up the box,
                        cleanUpPostgrelate();

                        // and try initializing it again.
                        continue;
                    }
                }
                throw new SandboxException("Cannot initialize Postgrelate!");
            } catch (IOException | InterruptedException e) {
                throw new SandboxException(e);
            }
        }
    }

    private void cleanUpPostgrelate() {
        ProcessBuilder pb = new ProcessBuilder("/usr/bin/java", "-jar", postgrelatePath, "-b" + boxId, "--cleanup").redirectErrorStream(true);

        try {
            ProcessExecutionResult result = SandboxExecutor.executeProcessBuilder(pb);
            if (result.getExitCode() != 0) {
                throw new SandboxException("Cannot clean up Postgrelate!");
            }
            if (boxDir != null && boxDir.exists()) {
                FileUtils.forceDelete(boxDir);
            }
        } catch (IOException | InterruptedException e) {
            throw new SandboxException(e);
        }
    }
}
