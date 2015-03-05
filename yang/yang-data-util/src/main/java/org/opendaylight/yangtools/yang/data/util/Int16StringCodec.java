/*
 * Copyright (c) 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.yangtools.yang.data.util;

import org.opendaylight.yangtools.yang.data.api.codec.Int16Codec;
import org.opendaylight.yangtools.yang.model.api.type.IntegerTypeDefinition;

final class Int16StringCodec extends LeafSimpleValueStringCodec<Short, IntegerTypeDefinition> implements
        Int16Codec<String> {

    protected Int16StringCodec(final IntegerTypeDefinition typeDef) {
        super(typeDef, Short.class);
    }

    @Override
    public Short deserialize(final String stringRepresentation) {
        int base = provideBase(stringRepresentation);
        if (base == 16) {
            return Short.valueOf(normalizeHexadecimal(stringRepresentation), base);
        }
        return Short.valueOf(stringRepresentation, base);
    }

    @Override
    public String serialize(final Short data) {
        return data == null ? "" : data.toString();
    }
}