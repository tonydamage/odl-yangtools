/*
 * Copyright (c) 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.yangtools.yang.data.util;

import org.opendaylight.yangtools.yang.data.api.codec.Int8Codec;
import org.opendaylight.yangtools.yang.model.api.type.IntegerTypeDefinition;

final class Int8StringCodec extends LeafSimpleValueStringCodec<Byte, IntegerTypeDefinition> implements
        Int8Codec<String> {

    protected Int8StringCodec(IntegerTypeDefinition typeDef) {
        super(typeDef, Byte.class);
    }

    @Override
    public Byte deserialize(final String stringRepresentation) {
        int base = provideBase(stringRepresentation);
        if (base == 16) {
            return Byte.valueOf(normalizeHexadecimal(stringRepresentation), base);
        }
        return Byte.valueOf(stringRepresentation, base);
    }

    @Override
    public String serialize(final Byte data) {
        return data == null ? "" : data.toString();
    }
}