import cc.changic.platform.etl.base.model.db.ODSConfig;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.apache.commons.exec.*;
import org.junit.Test;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

import static org.junit.Assert.assertFalse;

/**
 * Created by Panda.Z on 2015/3/7.
 */
public class ExecTest {

    @Test
    public void longTest(){
        Long a = 1000l;
        Long b = 10000l;
        System.out.println(a - b);
    }


    @Test
    public void test() {
        try {
//            Executor exec = new DefaultExecutor();
//            OutputStream systemOut = new SystemLogOutputStream(1);
//
//            exec.setStreamHandler(new PumpStreamHandler(systemOut, systemOut));
//            exec.setWorkingDirectory(new File("E:"));
//            CommandLine cl = CommandLine.parse("cmd /c python E:/indb.py asdf");
////            CommandLine cl = CommandLine.parse("cmd /c dir");
//            exec.execute(cl);
//            Map<String, Integer> json = new Gson().fromJson("{\"code\":1}", new TypeToken<Map<String, Integer>>() {
//            }.getType());
//            System.out.println(json.get("code"));
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        try {
            Runtime runtime = Runtime.getRuntime();
//            Process process = runtime.exec("cmd /c dir");
            Process process = runtime.exec("cmd /c python E:/indb.py asdf");
            //取得命令结果的输出流
            InputStream fis = process.getInputStream();
            InputStream errorStream = process.getErrorStream();
            //用一个读输出流类去读
            BufferedReader br = new BufferedReader(new InputStreamReader(fis,"GBK"));
            String line = null;
            //逐行读取输出到控制台
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }

            BufferedReader error = new BufferedReader(new InputStreamReader(errorStream,"GBK"));
            String lineerror = null;
            //逐行读取输出到控制台
            while ((lineerror = error.readLine()) != null) {
                System.err.println(lineerror);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class SystemLogOutputStream extends LogOutputStream {

        private SystemLogOutputStream(final int level) {
            super(level);
        }



        @Override
        protected void processLine(final String line, final int level) {
            System.out.println(line);
        }
    }


    @Test
    public void jdbcTest(){
        try {
            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://192.168.33.64:5432/db_game_data" ;
           Connection connection = DriverManager.getConnection(url, "g_user", "123456");
            System.out.println(connection);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
