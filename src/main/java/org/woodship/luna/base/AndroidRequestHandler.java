package org.woodship.luna.base;

import java.io.IOException;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.vaadin.server.RequestHandler;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinSession;

@Service
public class AndroidRequestHandler implements RequestHandler{
	private static final long serialVersionUID = 1L;

	@Override
	public boolean handleRequest(VaadinSession session, VaadinRequest request,
			VaadinResponse response) throws IOException {
		if ("/android/login".equals(request.getPathInfo())) {
			String userName = request.getParameter("userName");
			String password = request.getParameter("password");
			response.setContentType("text/plain");
			if (userName != null && password != null) {
				response.getWriter().append("16");
			}else{
				response.getWriter().write("0");
			}

			return true; // We wrote a response
		} else if ("/android/add".equals(request.getPathInfo())) {
			String date = request.getParameter("date");
			String classes = request.getParameter("classes");
			System.out.println("日期："+date);
			System.out.println("班次："+classes);
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return true;
		}
			
	    return false; // No response was written
	}

}
