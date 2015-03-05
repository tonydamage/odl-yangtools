/*
 * Copyright (c) 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.yangtools.yang.data.util;

import org.opendaylight.yangtools.yang.data.api.codec.Uint8Codec;
import org.opendaylight.yangtools.yang.model.api.type.UnsignedIntegerTypeDefinition;

final class Uint8StringCodec extends LeafSimpleValueStringCodec<Short, UnsignedIntegerTypeDefinition>
        implements Uint8Codec<String> {

    protected Uint8StringCodec(final UnsignedIntegerTypeDefinition typeDef) {
        super(typeDef, Short.class);
    }

    @Override
    public String serialize(final Short data) {
        return data == null ? "" : data.toString();
    }

    @Override
    public Short deserialize(final String stringRepresentation) {
        int base = provideBase(stringRepresentation);
        if (base == 16) {
            return Short.valueOf(normalizeHexadecimal(stringRepresentation), base);
        }
        return Short.valueOf(stringRepresentation, base);
    }
}