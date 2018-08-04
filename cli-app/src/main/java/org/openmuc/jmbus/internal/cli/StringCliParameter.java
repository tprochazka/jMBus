/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.openmuc.jmbus.internal.cli;

public class StringCliParameter extends ValueCliParameter {

    String value;

    private String defaultValue;

    StringCliParameter(CliParameterBuilder builder, String parameterName, String defaultValue) {
        super(builder, parameterName);
        this.defaultValue = defaultValue;
        value = defaultValue;
    }

    StringCliParameter(CliParameterBuilder builder, String parameterName) {
        super(builder, parameterName);
    }

    public String getValue() {
        return value;
    }

    @Override
    int parse(String[] args, int i) throws CliParseException {
        selected = true;

        if (args.length < (i + 2)) {
            throw new CliParseException("Parameter " + name + " has no value.");
        }
        value = args[i + 1];

        return 2;
    }

    @Override
    public void appendDescription(StringBuilder sb) {
        super.appendDescription(sb);
        if (defaultValue != null) {
            sb.append(" Default is ").append(defaultValue).append(".");
        }
    }
}
