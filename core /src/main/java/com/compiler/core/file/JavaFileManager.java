package com.compiler.core.file;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Unsafe;

import javax.tools.*;
import java.io.*;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.*;


public class JavaFileManager implements JavaFileManager {

    private final static Unsafe unsafe;
    private static final long OVERRIDE_OFFSET;

    static {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            unsafe = (Unsafe) theUnsafe.get(null);
            Field f = AccessibleObject.class.getDeclaredField("override");
            OVERRIDE_OFFSET = unsafe.objectFieldOffset(f);
        } catch (Exception ex) {
            throw new AssertionError(ex);
        }
    }

    private final StandardJavaFileManager fileManager;

    // synchronizing due to ConcurrentModificationException
    private final Map<String, ByteArrayOutputStream> buffers = Collections.synchronizedMap(new LinkedHashMap<>());

    MyJavaFileManager(StandardJavaFileManager fileManager) {
        this.fileManager = fileManager;
    }

    public Iterable<Set<javax.tools.JavaFileManager.Location>> listLocationsForModules(final javax.tools.JavaFileManager.Location location) {
        return invokeNamedMethodIfAvailable(location, "listLocationsForModules");
    }

    public String inferModuleName(final javax.tools.JavaFileManager.Location location) {
        return invokeNamedMethodIfAvailable(location, "inferModuleName");
    }

    public ClassLoader getClassLoader(javax.tools.JavaFileManager.Location location) {
        return fileManager.getClassLoader(location);
    }

    public Iterable<JavaFileObject> list(javax.tools.JavaFileManager.Location location, String packageName, Set<JavaFileObject.Kind> kinds, boolean recurse) throws IOException {
        return fileManager.list(location, packageName, kinds, recurse);
    }

    public String inferBinaryName(javax.tools.JavaFileManager.Location location, JavaFileObject file) {
        return fileManager.inferBinaryName(location, file);
    }

    public boolean isSameFile(FileObject a, FileObject b) {
        return fileManager.isSameFile(a, b);
    }

    public boolean handleOption(String current, Iterator<String> remaining) {
        return fileManager.handleOption(current, remaining);
    }

    public boolean hasLocation(javax.tools.JavaFileManager.Location location) {
        return fileManager.hasLocation(location);
    }

    public JavaFileObject getJavaFileForInput(javax.tools.JavaFileManager.Location location, String className, JavaFileObject.Kind kind) throws IOException {

        if (location == StandardLocation.CLASS_OUTPUT) {
            boolean success = false;
            final byte[] bytes;
            synchronized (buffers) {
                success = buffers.containsKey(className) && kind == JavaFileObject.Kind.CLASS;
                bytes = buffers.get(className).toByteArray();
            }
            if (success) {

                return new SimpleJavaFileObject(URI.create(className), kind) {
                    @NotNull
                    public InputStream openInputStream() {
                        return new ByteArrayInputStream(bytes);
                    }
                };
            }
        }
        return fileManager.getJavaFileForInput(location, className, kind);
    }

    @NotNull
    public JavaFileObject getJavaFileForOutput(javax.tools.JavaFileManager.Location location, final String className, JavaFileObject.Kind kind, FileObject sibling) {
        return new SimpleJavaFileObject(URI.create(className), kind) {
            @NotNull
            public OutputStream openOutputStream() {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                buffers.put(className, baos);
                return baos;
            }
        };
    }

    public FileObject getFileForInput(javax.tools.JavaFileManager.Location location, String packageName, String relativeName) throws IOException {
        return fileManager.getFileForInput(location, packageName, relativeName);
    }

    public FileObject getFileForOutput(javax.tools.JavaFileManager.Location location, String packageName, String relativeName, FileObject sibling) throws IOException {
        return fileManager.getFileForOutput(location, packageName, relativeName, sibling);
    }

    public void flush() {
        // Do nothing
    }

    public void close() throws IOException {
        fileManager.close();
    }

    public int isSupportedOption(String option) {
        return fileManager.isSupportedOption(option);
    }

    public void clearBuffers() {
        buffers.clear();
    }

    public Map<String, byte[]> getAllBuffers() {
        synchronized (buffers) {
            Map<String, byte[]> ret = new LinkedHashMap<>(buffers.size() * 2);
            for (Map.Entry<String, ByteArrayOutputStream> entry : buffers.entrySet()) {
                ret.put(entry.getKey(), entry.getValue().toByteArray());
            }
            return ret;
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T invokeNamedMethodIfAvailable(final javax.tools.JavaFileManager.Location location, final String name) {
        final Method[] methods = fileManager.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals(name) && method.getParameterTypes().length == 1 &&
                    method.getParameterTypes()[0] == javax.tools.JavaFileManager.Location.class) {
                try {
                    unsafe.putBoolean(method, OVERRIDE_OFFSET, true);
                    return (T) method.invoke(fileManager, location);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new UnsupportedOperationException("Unable to invoke method " + name);
                }
            }
        }
        throw new UnsupportedOperationException("Unable to find method " + name);
    }




}
