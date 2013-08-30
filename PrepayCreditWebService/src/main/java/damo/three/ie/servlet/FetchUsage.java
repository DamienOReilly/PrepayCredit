/*
 * This file is part of Prepay Credit for Android
 *
 * Copyright © 2013  Damien O'Reilly
 *
 * Prepay Credit for Android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Prepay Credit for Android is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Prepay Credit for Android.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Report bugs or new features at: https://github.com/DamienOReilly/PrepayCredit
 * Contact the author at:          damienreilly@gmail.com
 */

package damo.three.ie.servlet;

import org.apache.log4j.Logger;

import damo.three.ie.servlet.util.ThreeException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "FetchUsage", urlPatterns = { "/FetchUsage/*" })
public class FetchUsage extends HttpServlet {

	private static final long serialVersionUID = -3599018287634291134L;
	private static final Logger log = Logger.getLogger(FetchUsage.class
			.getName());

	private String username;
	private String password;

	/**
	 * Handle GET requests. Will most likely remove this. Only here for testing
	 * purposes.
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {

		username = request.getParameter("username");
		password = request.getParameter("password");

		go(response);

	}

	/**
	 * Attempt to fetch the users usages.
	 * 
	 * @param response
	 */
	private void go(HttpServletResponse response) {

		response.setContentType("application/json; charset=UTF-8");
		PrintWriter out;

		try {
			out = response.getWriter();

			if (username == null || password == null) {
				throw new ThreeException("No username/password specified.");
			}

			AccountProcessor ap = new AccountProcessor(out, username, password);
			ap.go();
		} catch (IOException e) {
			log.error(e);
		} catch (ThreeException e) {
			log.error(e);
		}

	}

	/**
	 * Handle POST requests
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) {

		username = request.getParameter("username");
		password = request.getParameter("password");

		go(response);
	}

}
