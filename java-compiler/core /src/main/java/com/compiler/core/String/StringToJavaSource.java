package com.compiler.core.String;


import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import java.net.URI;

/* A file object used to represent source coming from a string.
 */
public class StringToJavaSource extends SimpleJavaFileObject {
    // Source code of the file
    private String code;

    /**
     * Constructs a new JavaSourceFromString.
     *
     * @param name the name of the compilation unit represented by this file object
     * @param code the source code for the compilation unit represented by this file object
     */
    public StringToJavaSource(String name, String code) {
        super(URI.create("string:///" + name.replace('.', '/') + JavaFileObject.Kind.SOURCE.extension),
                JavaFileObject.Kind.SOURCE);
        this.code = code;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return code;
    }

}
