/*
 * Copyright (c) 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.yangtools.yang.data.util;

import org.opendaylight.yangtools.yang.data.api.codec.Int32Codec;
import org.opendaylight.yangtools.yang.model.api.type.IntegerTypeDefinition;

final class Int32StringCodec extends LeafSimpleValueStringCodec<Integer, IntegerTypeDefinition> implements
        Int32Codec<String> {

    protected Int32StringCodec(final IntegerTypeDefinition typeDef) {
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