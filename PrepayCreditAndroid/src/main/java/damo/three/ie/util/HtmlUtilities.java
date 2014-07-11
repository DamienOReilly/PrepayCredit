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

package damo.three.ie.util;

import damo.three.ie.prepay.Constants;
import org.apache.http.HttpEntity;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlUtilities {

    public static String getPageContent(HttpEntity entity) throws IOException {

        InputStream in = entity.getContent();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in), 1024 * 8);
        StringBuilder str = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            str.append(line);
        }
        in.close();
        return str.toString();
    }

    private static void removeComments(Node node) {
        for (int i = 0; i < node.childNodes().size(); ) {
            Node child = node.childNode(i);
            if (child.nodeName().equals("#comment")) {
                child.remove();
            } else {
                removeComments(child);
                i++;
            }
        }
    }

    /**
     * Parses the My3 account usage page to nicer JSON format.
     *
     * @param pageContent Page content as HTML.
     * @return Usage information stripped out and formatted as JSON.
     * @throws JSONException
     */
    public static JSONArray parseUsageAsJSONArray(String pageContent) throws JSONException {
        // The HTML on prepay is pig-ugly, so we will use JSoup to
        // clean and parse it.
        Document doc = Jsoup.parse(pageContent);
        HtmlUtilities.removeComments(doc);

        Elements elements = doc.getElementsByTag("table");

        JSONArray jsonArray = new JSONArray();

        // three don't have a sub label for the 3-to-3 calls, which is not consistent with other items.
        // .. feck them!
        boolean three2threeCallsBug = false;

        for (Element element : elements) {

            for (Element subelement : element.select("tbody > tr")) {

                if ((subelement.text().contains("3 to 3 Calls")) && (subelement.text().contains("Valid until"))) {
                    three2threeCallsBug = true;
                }

                Elements subsubelements = subelement.select("td");

                if (subsubelements.size() == 3) {

                    // skip the "total" entries
                    if (subsubelements.select("td").get(0).text().contains("Total")) {
                        continue;
                    }

                    JSONObject currentItem = new JSONObject();

                    if (three2threeCallsBug) {
                        currentItem.put("item", "3 to 3 Calls");
                    } else {
                        // Get rid of that "non-breaking space" character if it exists
                        String titleToClean = subsubelements.select("td").get(0).text().replace("\u00a0", "").trim();
                        currentItem.put("item", titleToClean);
                    }

                    /**
                     * Check if date contains "Today", if so, change it to a date.
                     * Otherwise we will never know when usage ends, unless user refreshes, As 'today'
                     * is 'today', tomorrow.. see!
                     */
                    String value1 = subsubelements.select("td").get(1).text();
                    if (value1.equals("Today")) {
                        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yy").withLocale(Locale.UK);
                        DateTime dt = new DateTime(); // current datetime
                        value1 = "Expires " + formatter.print(dt);
                    }
                    currentItem.put("value1", value1);
                    currentItem.put("value2", subsubelements.select("td").get(2).text());

                    // Out of Bundle charges have an extra property
                    if (currentItem.getString("item").startsWith("Internet")) {

                        Pattern p1 = Pattern.compile(Constants.OUT_OF_BUNDLE_REGEX, Pattern.DOTALL);
                        Matcher m1 = p1.matcher(pageContent);

                        StringBuilder cleanedDate = new StringBuilder();
                        if (m1.matches()) {
                            cleanedDate.append(m1.group(1));
                            cleanedDate.append(' ');
                            cleanedDate.append(m1.group(2));
                            cleanedDate.append(' ');
                            cleanedDate.append(m1.group(3));
                            currentItem.put("value3", cleanedDate.toString());
                        }

                    }
                    jsonArray.put(currentItem);
                }

            }

            // reset the 3-to-3 call bug flag for next Element
            if (three2threeCallsBug) {
                three2threeCallsBug = false;
            }
        }

        return jsonArray;
    }

}