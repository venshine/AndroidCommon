package com.wx.android.common.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Shell operation
 *
 * @author fengwx
 */
public class ShellUtils {

    public static final String COMMAND_SU = "su";
    public static final String COMMAND_SH = "sh";
    public static final String COMMAND_EXIT = "exit\n";
    public static final String COMMAND_LINE_END = "\n";

    /**
     * Check root permission
     *
     * @return
     */
    public static boolean checkRootPermission() {
        return execCommand("echo root", true) == 0;
    }

    /**
     * Exec shell command
     *
     * @param command
     * @param isRoot
     * @return
     */
    public static int execCommand(String command, boolean isRoot) {
        return execCommand(new String[]{command}, isRoot);
    }

    /**
     * Exec shell command
     *
     * @param commands
     * @param isRoot
     * @return
     */
    public static int execCommand(List<String> commands, boolean isRoot) {
        return execCommand(commands == null ? null : commands.toArray(new String[]{}), isRoot);
    }

    /**
     * Exec shell command
     *
     * @param commands
     * @param isRoot
     * @return
     */
    public static int execCommand(String[] commands, boolean isRoot) {
        int result = -1;
        if (commands == null || commands.length == 0) {
            return result;
        }

        Process process = null;
        BufferedReader succBR = null;
        BufferedReader errorBR = null;

        DataOutputStream dos = null;
        try {
            process = Runtime.getRuntime().exec(isRoot ? COMMAND_SU : COMMAND_SH);
            dos = new DataOutputStream(process.getOutputStream());
            for (String command : commands) {
                if (command == null) {
                    continue;
                }
                dos.write(command.getBytes());
                dos.writeBytes(COMMAND_LINE_END);
                dos.flush();
            }
            dos.writeBytes(COMMAND_EXIT);
            dos.flush();

            result = process.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(dos);
            IOUtils.close(succBR);
            IOUtils.close(errorBR);
            if (process != null) {
                process.destroy();
            }
        }
        return result;
    }

}
