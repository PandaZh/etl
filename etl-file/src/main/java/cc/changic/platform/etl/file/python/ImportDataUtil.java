package cc.changic.platform.etl.file.python;

import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;

import java.io.File;
import java.io.IOException;

/**
 * Created by Panda.Z on 2015/3/7.
 */
public class ImportDataUtil {

    private final String pythonScript;

    public ImportDataUtil(String pythonScript) {
        this.pythonScript = pythonScript;
    }

    /**
     * 使用Python脚本导入数据
     *
     * @param dataFile 数据源
     * @return <p> 1 执行正常
     * <p> -1 复制模版文件出错，会退出；
     * <p> -2 替换副本模版文件出错，会退出
     * <p> -3 执行2次gpload还是报错，会退出
     * <p> -4 移走模版文件和文件报错，会退出
     * <p> -5 main1报错，会退出
     * <p> -6 main2报错，会退出
     * <p> -7 传入的文件不存在，或者不是文件，报错退出
     * @throws IOException
     */
    public String importDataByPython(File dataFile) throws IOException {
        if (null == pythonScript)
            throw new IOException("Import data error: pythonScript is null");

        File script = new File(pythonScript);
        if (!script.exists())
            throw new IOException("Import data error: pythonScript=" + pythonScript + " not found");

        if (null == dataFile || !dataFile.exists())
            throw new IOException("Import data error: dataFile=" + dataFile.getAbsolutePath() + " not found");

        PySystemState state = new PySystemState();
        state.argv.append(new PyString(dataFile.getAbsolutePath()));
        PythonInterpreter python = new PythonInterpreter(null, state);
        python.execfile(pythonScript);
        PyObject code = python.get("code");
        return code.toString();
    }
}
