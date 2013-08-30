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

package damo.three.ie.servlet.util;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.jsoup.nodes.Node;

import java.io.IOException;

public class HtmlUtilities {

    /**
     * Return page content as String.
     *
     * @param entity HtmlEntity
     * @return String
     * @throws IOException
     */
    public static String getPageContent(HttpEntity entity) throws IOException {

        return new String(EntityUtils.toByteArray(entity), "UTF-8");
    }

    /**
     * Remove comments from the HTML code.
     *
     * @param node Node
     */
    public static void removeComments(Node node) {
        for (int i = 0; i < node.childNodes().size(); ) {
            Node child = node.childNode(i);
            if (child.nodeName().equals("#comment"))
                child.remove();
            else {
                removeComments(child);
                i++;
            }
        }
    }

}
