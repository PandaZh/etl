import cc.changic.platform.etl.file.python.ImportDataUtil;
import org.junit.Test;
import org.python.core.PyInteger;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;

import java.io.*;

import static org.junit.Assert.assertFalse;

/**
 * Created by Panda.Z on 2015/3/7.
 */
public class ExecTest {

    @Test
    public void test() {
//        try {
//            Executor exec = new DefaultExecutor();
//            OutputStream systemOut = new SystemLogOutputStream(1);
//            exec.setStreamHandler(new PumpStreamHandler(systemOut, systemOut));
//            exec.setWorkingDirectory(new File("E:"));
//            CommandLine cl = new CommandLine("cmd /c python indb.py asdf");
//            int exitValue = exec.execute(cl);
//
//            assertFalse(exec.isFailure(exitValue));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Test
    public void pythonTest() {
//        ImportDataUtil util = new ImportDataUtil("E:/indb.py");

//        PySystemState state = new PySystemState();
//        state.argv.append(new PyString("arg1"));
//        PythonInterpreter python = new PythonInterpreter(null, state);
//        python.execfile("E:/indb.py");
//        PyObject code = python.get("code");
//        System.out.println(code.toString());
//        int number1 = 10;
//        int number2 = 32;
//
//        python.set("number1", new PyInteger(number1));
//        python.set("number2", new PyInteger(number2));
//        python.python("number3 = number1+number2");
//        PyObject number3 = python.get("number3");
//        System.out.println("val : " + number3.toString());
    }

    @Test
    public void runtime() {
//        try {
//            Runtime runtime = Runtime.getRuntime();
//            Process process = runtime.exec("cmd /c python E:/indb.py asdf");
//            //取得命令结果的输出流
//            InputStream fis = process.getInputStream();
//            //用一个读输出流类去读
//            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
//            String line = null;
//            //逐行读取输出到控制台
//            while ((line = br.readLine()) != null) {
//                System.out.println(line);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }


}
