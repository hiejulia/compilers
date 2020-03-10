package com.compiler.core;

import com.compiler.core.String.StringToJavaSource;
import com.compiler.core.file.JavaFileManager;
import com.compiler.core.utils.CompilerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Compiler implements Closeable {
    private static final Logger LOG = LoggerFactory.getLogger(Compiler.class);

    private static final PrintWriter DEFAULT_WRITER = new PrintWriter(System.err);

    private final Map<ClassLoader, Map<String, Class>> loadedClassesMap = Collections.synchronizedMap(new WeakHashMap<>());
    private final Map<ClassLoader, JavaFileManager> fileManagerMap = Collections.synchronizedMap(new WeakHashMap<>());

    // source
    private final File sourceDir;

    // class
    private final File classDir;

    // JavaFileObjects
    private final Map<String, JavaFileObject> javaFileObjects =
            new HashMap<String, JavaFileObject>();

    public Compiler(final File sourceDir, final File classDir) {
        this.sourceDir = sourceDir;
        this.classDir = classDir;
    }

    // Close
    @Override
    public void close() throws IOException {
        try {
            // Pass list of Java file
            for (JavaFileManager fileManager : fileManagerMap.values()) {
                fileManager.close();
            }
        } catch (IOException e) {
            throw new AssertionError(e);
        }

    }

    // Load from Java
    public Class loadFromJava(String className, String javaCode) throws ClassNotFoundException {
        return loadFromJava(getClass().getClassLoader(), className, javaCode, DEFAULT_WRITER);
    }

    // Load from Java
    public Class loadFromJava(ClassLoader classLoader,
                              String className,
                              String javaCode) throws ClassNotFoundException {
        return loadFromJava(classLoader, className, javaCode, DEFAULT_WRITER);
    }

    // Compile
    Map<String, byte[]> compileFromJava( String className, String javaCode, JavaFileManager fileManager) {
        return compileFromJava(className, javaCode, DEFAULT_WRITER, fileManager);
    }


    // Compile TODO
    Map<String, byte[]> compileFromJava(String className,
                                        String javaCode,
                                        PrintWriter writer,
                                        JavaFileManager fileManager) {
        Iterable<? extends JavaFileObject> compilationUnits;
        if (sourceDir != null) {
            String filename = className.replaceAll("\\.", '\\' + File.separator) + ".java";
            File file = new File(sourceDir, filename);
            writeText(file, javaCode);
            compilationUnits = s_standardJavaFileManager.getJavaFileObjects(file);

        } else {
            javaFileObjects.put(className, new StringToJavaSource(className, javaCode));
            compilationUnits = javaFileObjects.values();
        }
        // reuse the same file manager to allow caching of jar files
        boolean ok = s_compiler.getTask(writer, fileManager, new DiagnosticListener<JavaFileObject>() {
            @Override
            public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
                if (diagnostic.getKind() == Diagnostic.Kind.ERROR) {
                    writer.println(diagnostic);
                }
            }
        }, null, null, compilationUnits).call();
        Map<String, byte[]> result = fileManager.getAllBuffers();
        if (!ok) {
            // compilation error, so we want to exclude this file from future compilation passes
            if (sourceDir == null)
                javaFileObjects.remove(className);

            // nothing to return due to compiler error
            return Collections.emptyMap();
        }
        return result;
    }


    // TODO
    public Class loadFromJava(ClassLoader classLoader,
                              String className,
                              String javaCode,
                              PrintWriter writer) throws ClassNotFoundException {
        Class clazz = null;
        Map<String, Class> loadedClasses;
        synchronized (loadedClassesMap) {
            loadedClasses = loadedClassesMap.get(classLoader);
            if (loadedClasses == null)
                loadedClassesMap.put(classLoader, loadedClasses = new LinkedHashMap<String, Class>());
            else
                clazz = loadedClasses.get(className);
        }
        PrintWriter printWriter = (writer == null ? DEFAULT_WRITER : writer);
        if (clazz != null)
            return clazz;

        JavaFileManager fileManager = fileManagerMap.get(classLoader);
        if (fileManager == null) {
            StandardJavaFileManager standardJavaFileManager = s_compiler.getStandardFileManager(null, null, null);
            fileManagerMap.put(classLoader, fileManager = new JavaFileManager(standardJavaFileManager));
        }
        for (Map.Entry<String, byte[]> entry : compileFromJava(className, javaCode, printWriter, fileManager).entrySet()) {
            String className2 = entry.getKey();
            synchronized (loadedClassesMap) {
                if (loadedClasses.containsKey(className2))
                    continue;
            }
            byte[] bytes = entry.getValue();
            if (classDir != null) {
                String filename = className2.replaceAll("\\.", '\\' + File.separator) + ".class";
                boolean changed = writeBytes(new File(classDir, filename), bytes);
                if (changed) {
                    LOG.info("Updated {} in {}", className2, classDir);
                }
            }
            Class clazz2 = CompilerUtils.defineClass(classLoader, className2, bytes);
            synchronized (loadedClassesMap) {
                loadedClasses.put(className2, clazz2);
            }
        }
        synchronized (loadedClassesMap) {
            loadedClasses.put(className, clazz = classLoader.loadClass(className));
        }
        return clazz;
    }

}
