package com.axgrid.signal.service;

import com.axgrid.signal.dto.AxSignal;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.GenericTypeResolver;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Файловый бэкенд длф задач
 * @param <T>
 */
@Slf4j
public class AxSignalFileStoreBackend<T extends AxSignal> implements AxSignalStoreBackend<T> {

    final File newTasks;
    final File processTasks;
    final File doneTasks;
    Class<T> persistentClass;

    final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

    public String getFileName(T task) {
        return String.format("%d_%s.json", task.getTime(), task.getUuid());
    }

    public Class<T> getPersistentClass() {
        return this.persistentClass;
    }

    @Override
    public void add(T task) {
        try {
            Path filePath = Path.of(newTasks.getAbsolutePath(), getFileName(task));
            objectMapper.writeValue(filePath.toFile(), task);
        }catch (IOException e) {
            if (log.isErrorEnabled()) log.error("Write file exception {}", Path.of(newTasks.getAbsolutePath(), getFileName(task)), e);
        }
    }

    @Override
    public void process(T task) {
        String fileName = getFileName(task);
        File taskFile = Path.of(newTasks.getAbsolutePath(), fileName).toFile();
        if (!taskFile.exists()) {
            if (log.isErrorEnabled()) log.error("Task file {} not exists", taskFile.getName());
            return;
        }
        try {
            FileUtils.moveFile(taskFile, Path.of(processTasks.getAbsolutePath(), fileName).toFile());
        }catch (IOException e) {
            if (log.isErrorEnabled()) log.error("Move task file {} exception", taskFile.getName(), e);
        }
    }

    @Override
    public void done(T task) {
        String fileName = getFileName(task);
        File taskFile = Path.of(newTasks.getAbsolutePath(), fileName).toFile();
        if (!taskFile.exists())
            taskFile = Path.of(processTasks.getAbsolutePath(), fileName).toFile();
        if (!taskFile.exists()) {
            if (log.isErrorEnabled()) log.error("Task file {} not found", taskFile.getName());
            return;
        }
        try {
            FileUtils.moveFile(taskFile, Path.of(doneTasks.getAbsolutePath(), fileName).toFile());
        }catch (IOException e) {
            if (log.isErrorEnabled()) log.error("Move task file {} exception", taskFile.getName(), e);
        }
    }

    @Override
    public void remove(T task) {
        try {
            Path filePath = Path.of(newTasks.getAbsolutePath(), getFileName(task));
            FileUtils.forceDelete(filePath.toFile());
        }catch (IOException e){
            if (log.isWarnEnabled()) log.warn("Delete file {} Exception", Path.of(newTasks.getAbsolutePath(), getFileName(task)), e);
        }
    }

    @Override
    public void clear() {
        var files = newTasks.listFiles();
        if (files == null) return;
        for(var file : files) {
            try {
                FileUtils.forceDelete(file);
            }catch (IOException e){
                if (log.isErrorEnabled()) log.error("Delete all file Exception", e);
            }
        }
    }

    @Override
    public Collection<T> getAllNewStoredSignals() {
        var files = newTasks.listFiles();
        if (files != null) {
            return Arrays.stream(files).filter(file -> file.getName().endsWith(".json")).map(file -> {
                try {
                    var queueObject =  objectMapper.readValue(file, getPersistentClass());
                    if (log.isTraceEnabled()) log.trace("{} load object {}", this.getClass().getSimpleName(), queueObject);
                    return queueObject;
                }catch (IOException e) {
                    if (log.isWarnEnabled()) log.warn("{} json read Exception", this.getClass().getSimpleName(), e);
                    return null;
                }
            }).filter(Objects::nonNull).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public Collection<T> getAllProcessStoredSignals() {
        var files = processTasks.listFiles();
        if (files != null) {
            return Arrays.stream(files).filter(file -> file.getName().endsWith(".json")).map(file -> {
                try {
                    var queueObject =  objectMapper.readValue(file, getPersistentClass());
                    if (log.isTraceEnabled()) log.trace("{} load object {}", this.getClass().getSimpleName(), queueObject);
                    return queueObject;
                }catch (IOException e) {
                    if (log.isWarnEnabled()) log.warn("{} json read Exception", this.getClass().getSimpleName(), e);
                    return null;
                }
            }).filter(Objects::nonNull).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    public AxSignalFileStoreBackend(String basePath, String channel, Class<T> persistentClass)  {
        this.persistentClass = persistentClass;
        newTasks = Path.of(basePath, channel, "new").toFile();
        processTasks = Path.of(basePath, channel, "process").toFile();
        doneTasks = Path.of(basePath, channel, "done").toFile();
        try {
            FileUtils.forceMkdir(newTasks);
            FileUtils.forceMkdir(processTasks);
            FileUtils.forceMkdir(doneTasks);
        }catch (IOException e) {
            log.error("IOException", e);
        }
    }


}
