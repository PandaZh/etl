import cc.changic.platform.etl.base.model.db.ODSConfig;
import com.google.common.base.Splitter;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;

/**
 * Created by Panda.Z on 2015/2/10.
 */
public class Timer {

    @Test
    public void insert() {
        try {
//            Class.forName("org.postgresql.Driver");
//            String url = "jdbc:postgresql://192.168.50.195:3433/db_etl_server";
//            Connection connection = DriverManager.getConnection(url, "gp_user", "123456");
//            PreparedStatement statement = connection.prepareStatement("INSERT INTO db_etl_server_panda_0001.t_test VALUES (?,?)");
//            for (int i = 0; i < 100; i++) {
//                statement.setObject(1, i);
//                if (i == 50)
//                    statement.setObject(1, "tttttt");
//                statement.setObject(2, i + "test");
//                statement.addBatch();
//            }
//            statement.executeBatch();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
