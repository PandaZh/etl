package cc.changic.platform.etl.base.service;

import cc.changic.platform.etl.base.model.ExecutableJob;

/**
 * 作业相关服务接口
 * Created by Panda.Z on 2015/3/3.
 */
public interface JobService {

    public void onJobSuccess(ExecutableJob executableJob);

    public void onJobFailed(ExecutableJob executableJob);
}
