/*
 * This file is part of My3 Prepay for Android
 *
 * Copyright © 2013  Damien O'Reilly
 *
 * My3 Prepay for Android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * My3 Prepay for Android is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with My3 Prepay for Android.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Report bugs or new features at: https://github.com/DamienOReilly/My3Usage
 * Contact the author at:          damienreilly@gmail.com
 */

package damo.three.ie.servlet;

import org.apache.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "FetchUsage", urlPatterns = {"/FetchUsage/*"})
public class FetchUsage extends HttpServlet {

    private static final long serialVersionUID = -3599018287634291134L;
    private static final Logger log = Logger.getLogger(FetchUsage.class.getName());

    /**
     * Handle GET requests. Will most likely remove this.
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        response.setContentType("application/json; charset=UTF-8");
        PrintWriter out;
        try {
            out = response.getWriter();
            AccountProcessor ap = new AccountProcessor(out, username, password);
            ap.go();
        } catch (IOException e) {
            log.error(e);
        }

    }

    /**
     * Handle POST requests
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        response.setContentType("application/json; charset=UTF-8");
        PrintWriter out;
        try {
            out = response.getWriter();
            AccountProcessor ap = new AccountProcessor(out, username, password);
            ap.go();
        } catch (IOException e) {
            log.error(e);
        }

    }

}
