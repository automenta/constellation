package org.constellation.service.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/login")
public class LoginController {

	@RequestMapping(method = RequestMethod.GET)
	public String get(HttpServletRequest request, HttpServletResponse response) {
		// String adminUrl = request.getParameter("adminUrl");
		// if(adminUrl!=null) {
		String adminUrl = request.getHeader("REFERER");
		

		// }
		request.getSession().setAttribute("adminUrl", adminUrl);
		return "redirect:../login.html";
	}

}
