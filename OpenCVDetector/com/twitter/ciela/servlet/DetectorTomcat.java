package com.twitter.ciela.servlet;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.naming.resources.VirtualDirContext;
import org.opencv.core.Core;

public class DetectorTomcat {
	
	public static void main(String[] args) throws Exception {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		Tomcat tomcat = new Tomcat();
		Context ctx = tomcat.addWebapp("/", ".");
		VirtualDirContext dirContext = new VirtualDirContext();
		dirContext.setExtraResourcePaths("/WEB-INF/classes=bin");
		ctx.setResources(dirContext);
		tomcat.start();
		tomcat.getServer().await();
	}

}
