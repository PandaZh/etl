package cc.changic.platform.etl.file.exec;

import com.google.common.collect.Lists;
import org.apache.commons.exec.LogOutputStream;

import java.util.List;

/**
 * 可获取返回结果集的输出流
 * Created by Panda.Z on 2015/3/9.
 */
public class CmdResultOutputStream extends LogOutputStream {

    private List<String> results = Lists.newArrayList();

    public CmdResultOutputStream(final int level) {
        super(level);
    }

    @Override
    protected void processLine(final String line, final int level) {
        results.add(line);
    }

    public List<String> getResults() {
        return results;
    }
}