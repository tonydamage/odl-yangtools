/*
 * Copyright (c) 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.yangtools.yang.data.util;

import java.math.BigInteger;
import org.opendaylight.yangtools.yang.data.api.codec.Uint64Codec;
import org.opendaylight.yangtools.yang.model.api.type.UnsignedIntegerTypeDefinition;

final class Uint64StringCodec extends
        LeafSimpleValueStringCodec<BigInteger, UnsignedIntegerTypeDefinition> implements Uint64Codec<String> {

    protected Uint64StringCodec(final UnsignedIntegerTypeDefinition typeDef) {
        super(typeDef, BigInteger.class);
    }

    @Override
    public BigInteger deserialize(final String stringRepresentation) {
        int base = provideBase(stringRepresentation);
        if (base == 16) {
            return new BigInteger(normalizeHexadecimal(stringRepresentation), base);
        }
        return new BigInteger(stringRepresentation, base);
    }

    @Override
    public String serialize(final BigInteger data) {
        return data == null ? "" : data.toString();
    }
}