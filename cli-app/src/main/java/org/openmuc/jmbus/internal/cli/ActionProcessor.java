/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.openmuc.jmbus.internal.cli;

import static java.lang.System.exit;
import static java.lang.System.out;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;

public class ActionProcessor {

    private static final String SEPARATOR_LINE = "------------------------------------------------------";

    private final BufferedReader reader;
    private final ActionListener actionListener;

    private final Map<String, Action> actionMap = new LinkedHashMap<>();

    private final Action helpAction = new Action("h", "print help message");
    private final Action quitAction = new Action("q", "quit the application");

    public ActionProcessor(ActionListener actionListener) {
        reader = new BufferedReader(new InputStreamReader(System.in));
        this.actionListener = actionListener;
    }

    public void addAction(Action action) {
        actionMap.put(action.getKey(), action);
    }

    public BufferedReader getReader() {
        return reader;
    }

    public void start() {

        actionMap.put(helpAction.getKey(), helpAction);
        actionMap.put(quitAction.getKey(), quitAction);

        printHelp();

        try {

            String actionKey;
            while (true) {
                System.out.println("\n** Enter action key: ");

                try {
                    actionKey = reader.readLine();
                } catch (IOException e) {
                    System.err.printf("%s. Application is being shut down.\n", e.getMessage());
                    exit(2);
                    return;
                }

                if (actionMap.get(actionKey) == null) {
                    System.err.println("Illegal action key.\n");
                    printHelp();
                    continue;
                }

                if (actionKey.equals(helpAction.getKey())) {
                    printHelp();
                    continue;
                }

                if (actionKey.equals(quitAction.getKey())) {
                    actionListener.quit();
                    return;
                }

                actionListener.actionCalled(actionKey);

            }

        } catch (Exception e) {
            actionListener.quit();
        } finally {
            close();
        }
    }

    private void printHelp() {
        final String message = " %s - %s\n";
        out.flush();
        out.println();
        out.println(SEPARATOR_LINE);

        for (Action action : actionMap.values()) {
            out.printf(message, action.getKey(), action.getDescription());
        }

        out.println(SEPARATOR_LINE);

    }

    public void close() {
        try {
            reader.close();
        } catch (IOException e) {
        }

    }

}
