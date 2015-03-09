package cc.changic.platform.etl.file.exec;

import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 命令行执行结果处理类
 * Created by Panda.Z on 2015/3/9.
 */
public class CmdExecResultHandler extends DefaultExecuteResultHandler {

    private Logger logger = LoggerFactory.getLogger(CmdExecResultHandler.class);

    private ExecuteWatchdog watchdog;
    private String cmd;

    public CmdExecResultHandler(final ExecuteWatchdog watchdog, final String cmd) {
        this.watchdog = watchdog;
        this.cmd = cmd;
    }

    @Override
    public void onProcessComplete(final int exitValue) {
        super.onProcessComplete(exitValue);
        logger.info("CommandLine:[{}] execute successfully.", cmd);
    }

    @Override
    public void onProcessFailed(final ExecuteException e) {
        super.onProcessFailed(e);
        if (watchdog != null && watchdog.killedProcess()) {
            logger.error("CommandLine:[{}] execute timed out.", cmd);
        } else {
            logger.error("CommandLine:[{}] execute failed, message={}.", cmd, e.getMessage());
        }
    }
}