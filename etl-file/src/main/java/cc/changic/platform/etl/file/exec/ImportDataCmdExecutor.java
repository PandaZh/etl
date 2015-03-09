package cc.changic.platform.etl.file.exec;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * 调用Python脚本导入数据
 * Created by Panda.Z on 2015/3/9.
 */
public class ImportDataCmdExecutor {

    public final static int IMPORT_SUCCESS = 1;

    private Logger logger = LoggerFactory.getLogger(ImportDataCmdExecutor.class);

    private final String pythonScript;
    private int timeout = -1;

    public ImportDataCmdExecutor(String pythonScript, int timeout) {
        this.pythonScript = pythonScript;
        this.timeout = timeout;
    }

    /**
     * * @return
     * <p> -1 Java调用错误
     * <p> 1 执行正常
     * <p> 2 替换副本模版文件出错，会退出
     * <p> 3 执行2次gpload还是报错，会退出
     * <p> 4 移走模版文件和文件报错，会退出
     * <p> 5 main1报错，会退出
     * <p> 6 main2报错，会退出
     * <p> 7 传入的文件不存在，或者不是文件，报错退出
     * <p> 8 复制模版文件出错，会退出；
     *
     * @param dataFile
     * @return 正常返回值：{"code":1},异常返回值：String(错误描述)
     */
    public String exec(File dataFile) {
        try {
            String cmd = "python " + pythonScript + " " + dataFile.getAbsolutePath();
            logger.info("Import data: cmd={}, dataFile={}", cmd, dataFile.getAbsoluteFile());
            Executor executor = new DefaultExecutor();
            executor.setExitValue(IMPORT_SUCCESS);
            CmdResultOutputStream out = new CmdResultOutputStream(1);
            CmdResultOutputStream err = new CmdResultOutputStream(1);
            executor.setStreamHandler(new PumpStreamHandler(out, err));
            CommandLine cl = CommandLine.parse(cmd);
            int execute = executor.execute(cl);
            if (execute == IMPORT_SUCCESS) {
                return out.getResults().get(out.getResults().size() - 1);
            } else {
                StringBuffer error = new StringBuffer();
//                for (String tmp : out.getResults()) {
//                    error.append(tmp);
//                }
                for (String tmp : err.getResults()) {
                    error.append(tmp);
                }
                logger.error("Import data error:{}", error.toString());
                return error.toString();
            }
        } catch (Exception e) {
            logger.error("Import data java error:{}", e.getMessage(), e);
            return "{\"code\":-1}";
        }

    }
}
