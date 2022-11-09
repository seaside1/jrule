/**
 * Copyright (c) 2010-2022 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.automation.jrule.rules.value;

import java.util.Base64;

/**
 * The {@link JRuleRawValue} JRule Command
 *
 * @author Arne Seime - Initial contribution
 */
public class JRuleRawValue implements JRuleValue {

    private final String mimeType;
    private final byte[] data;

    public JRuleRawValue(String mimeType, byte[] data) {
        this.mimeType = mimeType;
        this.data = data;
    }

    public JRuleRawValue(String value) {
        int idx;
        if (value.startsWith("data:") && (idx = value.indexOf(",")) >= 0) {
            int idx2;
            if ((idx2 = value.indexOf(";")) <= 5) {
                throw new IllegalArgumentException("Missing MIME type in argument " + value);
            } else {
                data = Base64.getDecoder().decode(value.substring(idx + 1));
                mimeType = value.substring(5, idx2);
            }
        } else {
            throw new IllegalArgumentException("Invalid data URI syntax for argument " + value);
        }
    }

    public String getMimeType() {
        return mimeType;
    }

    public byte[] getData() {
        return data;
    }
}
