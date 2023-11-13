package cn.ksmcbrigade.JL;

import java.net.URL;
import java.net.URLClassLoader;

public class ModJarLoader extends URLClassLoader {
    public ModJarLoader(URL[] urls) {
        super(urls);
    }

    public void add(URL url){
        this.addURL(url);
    }
}
