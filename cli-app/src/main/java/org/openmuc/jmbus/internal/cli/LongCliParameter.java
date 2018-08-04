/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.openmuc.jmbus.internal.cli;

public class LongCliParameter extends ValueCliParameter {

    Long value;

    private Long defaultValue;

    LongCliParameter(CliParameterBuilder builder, String parameterName, long defaultValue) {
        super(builder, parameterName);
        this.defaultValue = defaultValue;
        value = defaultValue;
    }

    LongCliParameter(CliParameterBuilder builder, String parameterName) {
        super(builder, parameterName);
    }

    public long getValue() {
        return value;
    }

    @Override
    int parse(String[] args, int i) throws CliParseException {
        selected = true;

        if (args.length < (i + 2)) {
            throw new CliParseException("Parameter " + name + " has no value.");
        }

        try {
            value = Long.decode(args[i + 1]);
        } catch (Exception e) {
            throw new CliParseException("Parameter value " + args[i + 1] + " cannot be converted to long.");
        }
        return 2;
    }

    @Override
    void appendDescription(StringBuilder sb) {
        super.appendDescription(sb);
        if (defaultValue != null) {
            sb.append(" Default is ").append(defaultValue).append(".");
        }
    }
}
