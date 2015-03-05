/*
 * Copyright (c) 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.yangtools.yang.data.util;

import org.opendaylight.yangtools.yang.data.api.codec.Uint16Codec;
import org.opendaylight.yangtools.yang.model.api.type.UnsignedIntegerTypeDefinition;

final class Uint16StringCodec extends LeafSimpleValueStringCodec<Integer, UnsignedIntegerTypeDefinition>
        implements Uint16Codec<String> {

    protected Uint16StringCodec(UnsignedIntegerTypeDefinition typeDef) {
        super(typeDef, Integer.class);
    }

    @Override
    public Integer deserialize(final String stringRepresentation) {
        int base = provideBase(stringRepresentation);
        if (base == 16) {
            return Integer.valueOf(normalizeHexadecimal(stringRepresentation), base);
        }
        return Integer.valueOf(stringRepresentation, base);
    }

    @Override
    public String serialize(final Integer data) {
        return data == null ? "" : data.toString();
    }
}