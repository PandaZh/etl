package cc.changic.platform.etl.file.execute;

import cc.changic.platform.etl.base.model.db.*;
import cc.changic.platform.etl.base.model.db.proto.*;
import cc.changic.platform.etl.protocol.FileJobProto;
import com.google.common.base.Strings;

import java.text.ParseException;

/**
 * @author Panda.Z
 */
public class FileJobTransformer {

    public static FileJobProto.FileJob toProtoFileJob(ExecutableFileJob executableFileJob) {
        App tmpApp = executableFileJob.getApp();
        AppProto.App app = AppProto.App.newBuilder()
                .setAppId(tmpApp.getAppId())
                .setAppName(tmpApp.getAppName() == null ? "" : tmpApp.getAppName())
                .setDbUser(tmpApp.getDbUser() == null ? "" : tmpApp.getDbUser())
                .setDbPwd(tmpApp.getDbPwd() == null ? "" : tmpApp.getDbPwd())
                .build();

        GameZone tmpGameZone = executableFileJob.getGameZone();
        GameZoneProto.GameZone gameZone = GameZoneProto.GameZone.newBuilder()
                .setId(tmpGameZone.getId())
                .setAppId(tmpGameZone.getAppId())
                .setGameZoneId(tmpGameZone.getGameZoneId())
                .setGameZoneName(tmpGameZone.getGameZoneName())
                .setEtlClientIp(tmpGameZone.getEtlClientIp())
                .setDbIp(tmpGameZone.getDbIp() == null ? "" : tmpGameZone.getDbIp())
                .setDbPort(tmpGameZone.getDbPort() == null ? 0 : tmpGameZone.getDbPort())
                .setTimezone(tmpGameZone.getTimezone())
                .setSite(tmpGameZone.getSite())
                .setMaxRunJob(tmpGameZone.getMaxRunJob())
                .setGameZoneStatus(tmpGameZone.getGameZoneStatus())
                .setFileDeleteInterval(tmpGameZone.getFileDeleteInterval())
                .build();

        ODSConfig tmpODSConfig = executableFileJob.getOdsConfig();
        ODSConfigProto.ODSConfig odsConfig = null;
        if (null != tmpODSConfig) {
            odsConfig = ODSConfigProto.ODSConfig.newBuilder()
                    .setId(tmpODSConfig.getId())
                    .setOdsName(tmpODSConfig.getOdsName())
                    .setOdsIp(tmpODSConfig.getOdsIp())
                    .setOdsPort(tmpODSConfig.getOdsPort())
                    .setOdsSchema(tmpODSConfig.getOdsSchema())
                    .setOdsUser(tmpODSConfig.getOdsUser())
                    .setOdsPwd(tmpODSConfig.getOdsPwd())
                    .build();
        }

        FileTask tmpFileTask = executableFileJob.getFileTask();
        FileTaskProto.FileTask fileTask = FileTaskProto.FileTask.newBuilder()
                .setId(tmpFileTask.getId())
                .setAppId(tmpFileTask.getAppId())
                .setTaskName(tmpFileTask.getTaskName())
                .setTaskType(tmpFileTask.getTaskType())
                .setNextInterval(tmpFileTask.getNextInterval())
                .setSourceDir(tmpFileTask.getSourceDir())
                .setStorageDir(tmpFileTask.getStorageDir())
                .setFileName(tmpFileTask.getFileName())
                .setDeleteInterval(tmpFileTask.getDeleteInterval() == null ? "" : tmpFileTask.getDeleteInterval())
                .setOdsId(tmpFileTask.getOdsId() == null ? -1 : tmpFileTask.getOdsId())
                .setInsertSql(tmpFileTask.getInsertSql() == null ? "" : tmpFileTask.getInsertSql())
                .setDeleteSql(tmpFileTask.getDeleteSql() == null ? "" : tmpFileTask.getDeleteSql())
                .build();

        cc.changic.platform.etl.base.model.db.Job tmpJob = executableFileJob.getJob();
        JobProto.Job.Builder jobBuilder = JobProto.Job.newBuilder();
        jobBuilder.setId(tmpJob.getId())
                .setAppId(tmpJob.getAppId())
                .setGameZoneId(tmpJob.getGameZoneId())
                .setTaskTable(tmpJob.getTaskTable())
                .setTaskId(tmpJob.getTaskId())
                .setStatus(tmpJob.getStatus());
        if (!Strings.isNullOrEmpty(tmpJob.getModifyTime()))
            jobBuilder.setModifyTime(tmpJob.getModifyTime());
        if (!Strings.isNullOrEmpty(tmpJob.getNextTime()))
            jobBuilder.setNextTime(tmpJob.getNextTime());
        if (!Strings.isNullOrEmpty(tmpJob.getLastRecordTime()))
            jobBuilder.setLastRecordTime(tmpJob.getLastRecordTime());
        if (null != tmpJob.getLastRecordId())
            jobBuilder.setLastRecordId(tmpJob.getLastRecordId());
        if (null != tmpJob.getLastRecordOffset())
            jobBuilder.setLastRecordOffset(tmpJob.getLastRecordOffset());
        if (!Strings.isNullOrEmpty(tmpJob.getOptionDesc()))
            jobBuilder.setOptionDesc(tmpJob.getOptionDesc());

        JobProto.Job job = jobBuilder.build();

        ConfigVersion tmpVersion = executableFileJob.getConfigVersion();
        ConfigVersionProto.ConfigVersion version = ConfigVersionProto.ConfigVersion.newBuilder()
                .setId(tmpVersion.getId())
                .setModifyTime(tmpVersion.getModifyTime())
                .setStatus(tmpVersion.getStatus()).build();

        FileJobProto.FileJob.Builder builder = FileJobProto.FileJob.newBuilder();
        builder.setApp(app).setGameZone(gameZone).setFileTask(fileTask).setJob(job).setVersion(version);
        if (null != odsConfig)
            builder.setOdsConfig(odsConfig);
        try {
            if (null != executableFileJob.getFileName())
                builder.setFileName(executableFileJob.getFileName());
        } catch (Exception e) {
        }
        try {
            if (null != executableFileJob.getSourceDir())
                builder.setSourceDir(executableFileJob.getSourceDir());
        } catch (Exception e) {
        }
        try {
            if (null != executableFileJob.getStorageDir())
                builder.setStorageDir(executableFileJob.getStorageDir());
        } catch (Exception e) {
        }
        if (null != executableFileJob.getMd5())
            builder.setMd5(executableFileJob.getMd5());
        builder.setIncrementalOffset(executableFileJob.getIncrementalOffset());
        return builder.build();
    }

    public static ExecutableFileJob toExecutableFileJob(FileJobProto.FileJob protoFileJob) {
        AppProto.App protoApp = protoFileJob.getApp();
        App app = new App();
        app.setAppId(protoApp.getAppId());
        app.setAppName(protoApp.getAppName());
        app.setDbUser(protoApp.getDbUser());
        app.setDbPwd(protoApp.getDbPwd());

        GameZoneProto.GameZone protoGameZone = protoFileJob.getGameZone();
        GameZone gameZone = new GameZone();
        gameZone.setId(protoGameZone.getId());
        gameZone.setAppId(protoGameZone.getAppId());
        gameZone.setGameZoneId(protoGameZone.getGameZoneId());
        gameZone.setGameZoneName(protoGameZone.getGameZoneName());
        gameZone.setEtlClientIp(protoGameZone.getEtlClientIp());
        gameZone.setDbIp(protoGameZone.getDbIp());
        gameZone.setDbPort((short) protoGameZone.getDbPort());
        gameZone.setTimezone((short) protoGameZone.getTimezone());
        gameZone.setSite(protoGameZone.getSite());
        gameZone.setMaxRunJob((short) protoGameZone.getMaxRunJob());
        gameZone.setGameZoneStatus((short) protoGameZone.getGameZoneStatus());
        gameZone.setFileDeleteInterval((short) protoGameZone.getFileDeleteInterval());

        ODSConfigProto.ODSConfig protoDOSConfig = protoFileJob.getOdsConfig();
        ODSConfig odsConfig = null;
        if (null != protoDOSConfig && protoDOSConfig.isInitialized()) {
            odsConfig = new ODSConfig();
            odsConfig.setId(protoDOSConfig.getId());
            odsConfig.setOdsName(protoDOSConfig.getOdsName());
            odsConfig.setOdsIp(protoDOSConfig.getOdsIp());
            odsConfig.setOdsPort((short) protoDOSConfig.getOdsPort());
            odsConfig.setOdsSchema(protoDOSConfig.getOdsSchema());
            odsConfig.setOdsUser(protoDOSConfig.getOdsUser());
            odsConfig.setOdsPwd(protoDOSConfig.getOdsPwd());
        }

        FileTaskProto.FileTask protoFileTask = protoFileJob.getFileTask();
        FileTask fileTask = new FileTask();
        fileTask.setId(protoFileTask.getId());
        fileTask.setAppId(protoFileTask.getAppId());
        fileTask.setTaskName(protoFileTask.getTaskName());
        fileTask.setTaskType((short) protoFileTask.getTaskType());
        fileTask.setNextInterval((short) protoFileTask.getNextInterval());
        fileTask.setSourceDir(protoFileTask.getSourceDir());
        fileTask.setStorageDir(protoFileTask.getStorageDir());
        fileTask.setFileName(protoFileTask.getFileName());
        fileTask.setDeleteInterval(protoFileTask.getDeleteInterval());
        fileTask.setOdsId(protoFileTask.getOdsId());
        fileTask.setInsertSql(protoFileTask.getInsertSql());
        fileTask.setDeleteSql(protoFileTask.getDeleteSql());

        JobProto.Job protoJob = protoFileJob.getJob();
        Job job = new Job();
        job.setId(protoJob.getId());
        job.setAppId(protoJob.getAppId());
        job.setGameZoneId(protoJob.getGameZoneId());
        job.setTaskTable(protoJob.getTaskTable());
        job.setTaskId(protoJob.getTaskId());
        job.setStatus((short) protoJob.getStatus());
        job.setModifyTime(Strings.isNullOrEmpty(protoJob.getModifyTime()) ? null : protoJob.getModifyTime());
        job.setNextTime(Strings.isNullOrEmpty(protoJob.getNextTime()) ? null : protoJob.getNextTime());
        job.setLastRecordTime(Strings.isNullOrEmpty(protoJob.getLastRecordTime()) ? null : protoJob.getLastRecordTime());
        job.setLastRecordId(protoJob.getLastRecordId());
        job.setLastRecordOffset(protoJob.getLastRecordOffset());
        job.setOptionDesc(protoJob.getOptionDesc());

        ConfigVersionProto.ConfigVersion protoVersion = protoFileJob.getVersion();
        ConfigVersion version = new ConfigVersion();
        version.setId(protoVersion.getId());
        version.setModifyTime(protoVersion.getModifyTime());
        version.setStatus(protoVersion.getStatus());

        ExecutableFileJob executableFileJob = new ExecutableFileJob(app, gameZone, fileTask, job, odsConfig, version);
        executableFileJob.setFileName(protoFileJob.getFileName());
        executableFileJob.setSourceDir(protoFileJob.getSourceDir());
        executableFileJob.setStorageDir(protoFileJob.getStorageDir());
        executableFileJob.setMd5(protoFileJob.getMd5());
        executableFileJob.setIncrementalOffset(protoFileJob.getIncrementalOffset());
        return executableFileJob;
    }
}
