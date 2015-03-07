import cc.changic.platform.etl.base.model.db.ODSConfig;
import cc.changic.platform.etl.base.util.TimeUtil;
import com.google.common.base.Splitter;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by Panda.Z on 2015/2/10.
 */
public class Timer {

    @Test
    public void insert() {
        try {
            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://192.168.50.192:5432/db_game_data";
            Connection connection = DriverManager.getConnection(url, "g_user", "123456");
            PreparedStatement statement = connection.prepareStatement("insert into g_test.t_l_log_reg_count(recorddate,gamezoneid,regs) values(CAST(? AS timestamp without time zone),?,?);");
//            for (int i = 0; i < 100; i++) {
            statement.setObject(1, "2015-03-06T00:01:25+0800");
            statement.setObject(2, "3");
            statement.setObject(3, 3);
            statement.executeUpdate();
//                statement.addBatch();
//            }
//            statement.executeBatch();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
